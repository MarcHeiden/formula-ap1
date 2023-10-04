export abstract class AppError extends Error {
    protected constructor(message?: string) {
        super(message);
        this.name = AppError.name;
    }
}
