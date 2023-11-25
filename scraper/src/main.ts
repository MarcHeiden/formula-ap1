import "dotenv/config";
import { ApiClient } from "./api-client/ApiClient.js";
import { Scraper } from "./scraper/Scraper.js";
import { Logger } from "./logger/Logger.js";
import { ErrorHandler } from "./error/ErrorHandler.js";
import { parseISO, isAfter, addHours } from "date-fns";
import { AppError } from "./error/AppError.js";

const logger = new Logger();
const errorHandler = new ErrorHandler(logger);
const apiClient = new ApiClient(logger);
const scraper = new Scraper(logger);

// Log process exit
process.on("exit", (exitCode) => {
    logger.logExit(exitCode);
});

// Catch errors and handle them
process.on("uncaughtException", (error: unknown) => {
    errorHandler.handleErrors(error);
});

/**
 * Invokes and logs data creation process defined in the given execute function.
 * @param execute - function that is executed
 * @param logMessage - message that is logged
 */
async function logDataCreation(execute: () => Promise<void>, logMessage: string) {
    logger.logCreateData(logMessage);
    await execute();
    logger.logCreatedData(logMessage);
}

/**
 * Creates races, teams, drivers and engines of a season.
 * @param seasonYear - of the season
 */
async function createSeasonData(seasonYear: number) {
    async function createRacesOfSeason() {
        const logMessage = `races of season ${seasonYear}`;
        await logDataCreation(async () => {
            const scrapedRaces = await scraper.scrapeRacesOfSeason(seasonYear);
            await apiClient.postRacesOfSeason(seasonYear, scrapedRaces);
        }, logMessage);
    }

    async function createTeamDataOfSeason() {
        const logMessage = `team data of season ${seasonYear}`;
        await logDataCreation(async () => {
            const teamData = await scraper.scrapeTeamDataOfSeason(seasonYear);
            await apiClient.postTeamsOfSeason(seasonYear, teamData);
        }, logMessage);
    }

    const logMessage = `season data for season ${seasonYear}`;
    await logDataCreation(async () => {
        // Create season data concurrently
        await Promise.all([createRacesOfSeason(), createTeamDataOfSeason()]);
    }, logMessage);
}

/**
 * Creates qualifying, result, fastest laps, leading laps, top speeds and
 * fastest pit stop for races of a season.
 * @param seasonYear - of the season to that the races belong
 */
async function createRaceData(seasonYear: number) {
    const logMessage = `race data for races of season ${seasonYear}`;
    await logDataCreation(async () => {
        const createdRaceDataForRacesWithDate: string[] = []; // Stores dates of races for that race data was created
        // eslint-disable-next-line no-constant-condition
        while (true) {
            const races = await apiClient.getRacesOfSeasonOrderedByDateAsc(seasonYear);
            const uncancelledRaces = races.filter((race) => race.cancelled === false);
            if (uncancelledRaces.length === createdRaceDataForRacesWithDate.length) {
                break; // Exit loop since race data was created for all uncancelled races of the season
            }
            for (let i = 0; i < uncancelledRaces.length; i++) {
                const race = uncancelledRaces[i];
                if (
                    race !== undefined &&
                    race.date !== undefined &&
                    !createdRaceDataForRacesWithDate.includes(race.date)
                ) {
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
                        // Qualifying must be created first, as this creates the driverOfRace object.
                        await scraper
                            .scrapeQualifyingDataOfRace(race, raceUrl)
                            .then((qualifyingData) => apiClient.postQualifyingOfRace(race, qualifyingData));
                        // Race result must be created second, as the API for all other data checks that it exists.
                        await scraper
                            .scrapeResultDataOfRace(race, raceUrl)
                            .then((resultData) => apiClient.postResultOfRace(race, resultData));
                        // Create other race data concurrently
                        await Promise.all([
                            scraper
                                .scrapeFastestLapData(race, raceUrl)
                                .then((fastestLapData) => apiClient.postFastestLapsOfRace(race, fastestLapData)),
                            scraper
                                .scrapeTopSpeedData(race, raceUrl)
                                .then((topSpeedData) => apiClient.postTopSpeedsOfRace(race, topSpeedData)),
                            scraper
                                .scrapeLeadingLapsData(race, raceUrl)
                                .then((leadingLapsData) => apiClient.postLeadingLapsOfRace(race, leadingLapsData)),
                            scraper
                                .scrapeFastestPitStopData(race, raceUrl)
                                .then((fastestPitStopData) =>
                                    apiClient.postFastestPitStopOfRace(race, fastestPitStopData)
                                )
                        ]);
                        if (race.date !== undefined) {
                            // Add date of current race to createdRaceDataForRacesWithDate
                            // as race data was created for it.
                            createdRaceDataForRacesWithDate.push(race.date);
                        }
                    }, logMessage);
                    // Exit loop to get latest races of season. This is necessary as it could be that
                    // new races were added to the season or a race was postponed.
                    break;
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
