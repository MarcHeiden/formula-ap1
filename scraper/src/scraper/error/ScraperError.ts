import { AppError } from "../../error/AppError.js";

export class ScraperError extends AppError {
    readonly error: unknown;
    constructor(error: unknown) {
        super();
        this.name = ScraperError.name;
        this.error = error;
    }
}
