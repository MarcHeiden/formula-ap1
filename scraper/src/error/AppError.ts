/**
 * General app error that is extended by all other app errors.
 */
export class AppError extends Error {
    constructor(message?: string) {
        super(message);
        this.name = AppError.name;
    }
}
