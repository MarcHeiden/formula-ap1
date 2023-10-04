import { createLogger, format, transports } from "winston";
import DailyRotateFile from "winston-daily-rotate-file";
import { AppError } from "../error/AppError.js";
import { ApiType } from "../f1api-client/api/ApiType.js";
import { ScraperError } from "../scraper/error/ScraperError.js";
import { F1ApiRequestError } from "../f1api-client/error/F1ApiRequestError.js";
import { F1ApiError } from "../f1api-client/error/F1ApiError.js";

export class Logger {
    private errorLevel = "error";

    private colorizeGrey(string: string): string {
        return format.colorize({ colors: { grey: "grey" } }).colorize("grey", string);
    }

    private logger = createLogger({
        level: process.env.LOG_LEVEL || "info",
        format: format.combine(format.errors({ stack: true }), format.timestamp(), format.json()),
        transports: [
            new transports.Console({
                format: format.combine(
                    format.timestamp(),
                    format.printf(({ timestamp, level, name, message, stack, unexpectedError, data, urls }) => {
                        const colorizedLevel = format.colorize().colorize(level, level.toUpperCase());
                        let colorizedData: string | undefined;
                        if (data !== undefined) {
                            colorizedData = this.colorizeGrey(JSON.stringify(data));
                        }
                        let colorizedUrls: string | undefined;
                        if (urls !== undefined) {
                            colorizedUrls = this.colorizeGrey(JSON.stringify(urls));
                        }
                        return `${timestamp} ${colorizedLevel} ${unexpectedError ? `[Unexpected Error] ` : ""}${
                            name ? `${name} ` : ""
                        }${message || ""} ${colorizedUrls || ""}${colorizedData || ""}${stack ? `\n${stack}` : ""}`;
                    }),
                ),
            }),
            new DailyRotateFile({
                // create new log file every month and keep the last 7 files
                // docs: https://github.com/winstonjs/winston-daily-rotate-file#options
                frequency: "1m",
                dirname: "./logs/",
                filename: "log%DATE%.txt",
                maxFiles: 7,
                utc: true, // use utc date
            }),
        ],
    });

    logAppError(error: AppError) {
        const meta: {
            name: string;
            stack: string | undefined;
            error: unknown;
        } = { name: error.name, stack: error.stack, error: error };
        let message = error.message;
        if (error instanceof ScraperError) {
            if (error.error instanceof Error) {
                message = error.error.message;
                meta.stack = error.error.stack;
                meta.error = error.error;
            }
        } else if (error instanceof F1ApiRequestError) {
            message = error.requestError.message;
            meta.error = error.requestError;
        } else if (error instanceof F1ApiError) {
            message = error.apiError.message;
            meta.error = error.apiError;
        }
        this.logger.log(this.errorLevel, message, meta);
    }

    logUnexpectedError(error: unknown) {
        const meta: {
            name?: string;
            stack?: string;
            error: unknown;
            unexpectedError: true;
        } = { error: error, unexpectedError: true };
        let message = "";
        if (error instanceof Error) {
            message = error.message;
            meta.name = error.name;
            meta.stack = error.stack;
        }
        this.logger.log(this.errorLevel, message, meta);
    }

    logExit(exitCode: number) {
        let level = "info";
        if (exitCode !== 0) {
            level = "error";
        }
        this.logger.log(level, `Process will exit with exit code ${exitCode}`);
    }

    warn(message: string) {
        this.logger.warn(message);
    }

    logAlreadyExists(message: string, data: ApiType) {
        this.logger.warn(message, { data: data });
    }

    logCreateData(message: string) {
        this.logger.info(`Create ${message}`);
    }

    logCreatedData(message: string) {
        this.logger.info(`Created ${message}`);
    }

    logScrape(message: string) {
        this.logger.info(`Scrape ${message}`);
    }

    logScraped(message: string, data: ApiType | ApiType[]) {
        this.logger.info(`Scraped ${message}`, { data: data });
    }

    logPost(message: string) {
        this.logger.info(`Post ${message}`);
    }

    logPosted(message: string, data: ApiType | ApiType[]) {
        this.logger.info(`Posted ${message}`, { data: data });
    }

    logProcessUrls(urls: string[]) {
        this.logger.info(`Process URLs`, { urls: urls });
    }

    logProcessUrl(url: string) {
        this.logger.info(`Process ${url}`);
    }

    logEnqueueUrls(urls: string[]) {
        this.logger.info(`Enqueue URLs`, { urls: urls });
    }
}
