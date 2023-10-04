import { AppError } from "./AppError.js";
import { Logger } from "../logger/Logger.js";

export class ErrorHandler {
    private logger: Logger;

    constructor(logger: Logger) {
        this.logger = logger;
    }

    handleErrors(error: unknown): boolean {
        let exit = true;
        if (error instanceof AppError) {
            exit = false;
            this.logger.logAppError(error);
        } else {
            this.handleUncaughtException(error);
        }
        return exit;
    }

    handleUncaughtException(error: unknown) {
        this.logger.logUnexpectedError(error);
        process.exitCode = 1;
    }
}
