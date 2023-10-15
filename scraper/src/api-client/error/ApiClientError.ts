import { AppError } from "../../error/AppError.js";

export class ApiClientError extends AppError {
    constructor(message?: string) {
        super(message);
        this.name = ApiClientError.name;
    }
}
