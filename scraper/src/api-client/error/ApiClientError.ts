import { AppError } from "../../error/AppError.js";

/**
 * Base error for all errors of this package.
 */
export class ApiClientError extends AppError {
    constructor(message?: string) {
        super(message);
        this.name = ApiClientError.name;
    }
}
