import "dotenv/config";
import { F1ApiClient } from "./f1api-client/F1ApiClient.js";
import { Scraper } from "./scraper/Scraper.js";
import { Logger } from "./logger/Logger.js";
import { ErrorHandler } from "./error/ErrorHandler.js";
import { parseISO, isAfter, addHours } from "date-fns";
import { AppError } from "./error/AppError.js";

process.on("exit", (exitCode) => {
    logger.logExit(exitCode);
});
process.on("uncaughtException", (error: unknown) => {
    errorHandler.handleErrors(error);
});

const logger = new Logger();
const errorHandler = new ErrorHandler(logger);
const f1ApiClient = new F1ApiClient(logger);
const scraper = new Scraper(logger);

async function logDataCreation(execute: () => Promise<void>, logMessage: string) {
    logger.logCreateData(logMessage);
    await execute();
    logger.logCreatedData(logMessage);
}

async function createSeasonData(seasonYear: number) {
    async function createRacesOfSeason() {
        const logMessage = `races of season ${seasonYear}`;
        await logDataCreation(async () => {
            const scrapedRaces = await scraper.scrapeRacesOfSeason(seasonYear);
            await f1ApiClient.postRacesOfSeason(seasonYear, scrapedRaces);
        }, logMessage);
    }

    async function createTeamDataOfSeason() {
        const logMessage = `team data of season ${seasonYear}`;
        await logDataCreation(async () => {
            const teamData = await scraper.scrapeTeamDataOfSeason(seasonYear);
            await f1ApiClient.postTeamsOfSeason(seasonYear, teamData);
        }, logMessage);
    }

    const logMessage = `season data for season ${seasonYear}`;
    await logDataCreation(async () => {
        // Create season data concurrently
        await Promise.all([createRacesOfSeason(), createTeamDataOfSeason()]);
    }, logMessage);
}

async function createRaceData(seasonYear: number) {
    const logMessage = `race data for races of season ${seasonYear}`;
    await logDataCreation(async () => {
        const createdRaceDataForRacesWithDate: string[] = [];
        // eslint-disable-next-line no-constant-condition
        while (true) {
            const races = await f1ApiClient.getRacesOfSeasonOrderedByDateAsc(seasonYear);
            const uncancelledRaces = races.filter((race) => race.cancelled === false);
            if (uncancelledRaces.length === createdRaceDataForRacesWithDate.length) {
                break; // Exit loop since race data was created for all races of the season
            }
            for (let i = 0; i < uncancelledRaces.length; i++) {
                const race = uncancelledRaces[i];
                if (race !== undefined && !createdRaceDataForRacesWithDate.includes(race.date)) {
                    const raceDatePlusFourHours = addHours(parseISO(`${race.date} ${race.time}`), 4);
                    while (!isAfter(new Date(), raceDatePlusFourHours)) {
                        // Wait until race finishes
                    }
                    const logMessage = `race data for race '${race.raceName}'`;
                    await logDataCreation(async () => {
                        const raceUrls = await scraper.scrapeRaceUrls(seasonYear);
                        const raceUrl = raceUrls[i];
                        if (raceUrl === undefined) {
                            throw new AppError(`Race url of race '${race.raceName}' does not exist`);
                        }
                        // Qualifying and result must be created first to create drivers of race,
                        // update teams of season with new drivers and create race result for driver of race
                        await scraper
                            .scrapeQualifyingDataOfRace(race, raceUrl)
                            .then((qualifyingData) => f1ApiClient.postQualifyingOfRace(race, qualifyingData));
                        await scraper
                            .scrapeResultDataOfRace(race, raceUrl)
                            .then((resultData) => f1ApiClient.postResultOfRace(race, resultData));
                        // Create other race data concurrently
                        await Promise.all([
                            scraper
                                .scrapeFastestLapData(race, raceUrl)
                                .then((fastestLapData) => f1ApiClient.postFastestLapsOfRace(race, fastestLapData)),
                            scraper
                                .scrapeTopSpeedData(race, raceUrl)
                                .then((topSpeedData) => f1ApiClient.postTopSpeedsOfRace(race, topSpeedData)),
                            scraper
                                .scrapeLeadingLapsData(race, raceUrl)
                                .then((leadingLapsData) => f1ApiClient.postLeadingLapsOfRace(race, leadingLapsData)),
                            scraper
                                .scrapeFastestPitStopData(race, raceUrl)
                                .then((fastestPitStopData) =>
                                    f1ApiClient.postFastestPitStopOfRace(race, fastestPitStopData)
                                )
                        ]);
                        createdRaceDataForRacesWithDate.push(race.date);
                    }, logMessage);
                    break; // Exit loop to get latest races of season
                }
            }
        }
    }, logMessage);
}

let seasonYear = Number(process.env.START_SEASON_YEAR);
if (isNaN(seasonYear)) {
    throw new AppError("START_SEASON_YEAR is undefined or not a number");
}
let scrapeSeasonData = process.env.SCRAPE_SEASON_DATA_OF_FIRST_SEASON || "true";
if (scrapeSeasonData !== "true" && scrapeSeasonData !== "false") {
    throw new AppError("SCRAPE_SEASON_DATA_OF_FIRST_SEASON can only be set to 'true' or 'false'");
}
// eslint-disable-next-line no-constant-condition
while (true) {
    if (scrapeSeasonData === "true") {
        await createSeasonData(seasonYear);
    }
    await createRaceData(seasonYear);
    seasonYear++;
    scrapeSeasonData = "true";
}
