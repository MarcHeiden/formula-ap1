import { createLogger, format, transports } from "winston";
import DailyRotateFile from "winston-daily-rotate-file";
import { AppError } from "../error/AppError.js";
import { ApiType } from "../api-client/api/ApiType.js";
import { ScraperError } from "../scraper/error/ScraperError.js";
import { ApiRequestError } from "../api-client/error/ApiRequestError.js";
import { WrappedApiError } from "../api-client/error/WrappedApiError.js";

/**
 * Acts as wrapper around {@link winston.Logger}. Logs are logged
 * to the console and a logfile located in the ./logs directory.
 */
export class Logger {
    /**
     * Colorizes the given string grey.
     * @param string - string to colorize
     * @returns colorized string
     */
    private colorizeGrey(string: string): string {
        return format.colorize({ colors: { grey: "grey" } }).colorize("grey", string);
    }

    private logger = createLogger({
        level: process.env.LOG_LEVEL || "info",
        // Format for logs which are logged to the logfile
        format: format.combine(format.errors({ stack: true }), format.timestamp(), format.json()),
        transports: [
            new transports.Console({
                // Format for logs which are logged to the console
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
                    })
                )
            }),
            new DailyRotateFile({
                // Create new logfile every month and keep the last 7 files
                // docs: https://github.com/winstonjs/winston-daily-rotate-file#options
                frequency: "1m",
                dirname: "./logs/",
                filename: "log%DATE%.txt",
                maxFiles: 7,
                utc: true // Use utc date
            })
        ]
    });

    logError(error: unknown) {
        if (error instanceof AppError) {
            this.logAppError(error);
        } else {
            this.logUnexpectedError(error);
        }
    }

    logAppError(error: AppError, logLevel: "warn" | "error" = "error") {
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
        } else if (error instanceof ApiRequestError) {
            message = error.requestError.message;
            meta.error = error.requestError;
        } else if (error instanceof WrappedApiError) {
            message = error.apiError.message;
            meta.error = error.apiError;
        }
        if (logLevel === "warn") {
            this.logger.log(logLevel, message);
        } else {
            this.logger.log(logLevel, message, meta);
        }
    }

    private logUnexpectedError(error: unknown) {
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
        this.logger.error(message, meta);
    }

    logExit(exitCode: number) {
        let level = "info";
        if (exitCode !== 0) {
            level = "error";
        }
        this.logger.log(level, `Process will exit with exit code ${exitCode}`);
    }

    logAlreadyExists(error: WrappedApiError, data: ApiType) {
        this.logger.warn(error.apiError.message, { data: data });
    }

    info(message: string) {
        this.logger.info(message);
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

    logScraped(message: string, data: unknown) {
        this.logger.info(`Scraped ${message}`, { data: data });
    }

    logPost(message: string, data: ApiType) {
        this.logger.info(`Post ${message}`, { data: data });
    }

    logPosted(message: string, data: ApiType) {
        this.logger.info(`Posted ${message}`, { data: data });
    }

    logPatched(message: string, data: ApiType) {
        this.logger.info(`Patched ${message}`, { data: data });
    }
}
