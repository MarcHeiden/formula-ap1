import { Logger } from "../logger/Logger.js";

/**
 * Provides method to handle errors.
 */
export class ErrorHandler {
    private readonly logger: Logger;

    constructor(logger: Logger) {
        this.logger = logger;
    }

    handleErrors(error: unknown) {
        this.logger.logError(error);
        process.exitCode = 1;
    }
}
