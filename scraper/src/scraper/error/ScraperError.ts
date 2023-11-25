import { AppError } from "../../error/AppError.js";

/**
 * Thrown if an error occurred during scraping.
 */
export class ScraperError extends AppError {
    readonly error: unknown;
    constructor(error: unknown) {
        super();
        this.name = ScraperError.name;
        this.error = error;
    }
}
