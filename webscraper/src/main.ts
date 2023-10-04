import "dotenv/config";
import { F1ApiClient } from "./f1api-client/F1ApiClient.js";
import { Scraper } from "./scraper/Scraper.js";
import { Logger } from "./logger/Logger.js";
import { ErrorHandler } from "./error/ErrorHandler.js";

process.on("exit", (exitCode) => {
    logger.logExit(exitCode);
});
process.on("uncaughtException", (error: unknown) => {
    errorHandler.handleUncaughtException(error);
});

const logger = new Logger();
const errorHandler = new ErrorHandler(logger);
const f1ApiClient = new F1ApiClient(logger);
const scraper = new Scraper(logger);

async function createSeasonData(seasonYear: number) {
    const createMessage = `season data for season ${seasonYear}`;
    async function createRacesOfSeason(seasonYear: number) {
        const createMessage = `races of season ${seasonYear}`;
        logger.logCreateData(createMessage);
        const races = await scraper.scrapeRacesOfSeason(seasonYear);
        await f1ApiClient.postRacesOfSeason(seasonYear, races);
        logger.logCreatedData(createMessage);
    }

    logger.logCreateData(createMessage);
    await createRacesOfSeason(seasonYear);
    logger.logCreatedData(createMessage);
}

// eslint-disable-next-line no-constant-condition
while (true) {
    try {
        await createSeasonData(2023);
    } catch (error) {
        const exit = errorHandler.handleErrors(error);
        if (exit) {
            break;
        }
    }
}
