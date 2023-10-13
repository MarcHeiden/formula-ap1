import { AppError } from "../../error/AppError.js";

export class F1ApiClientError extends AppError {
    constructor(message?: string) {
        super(message);
        this.name = F1ApiClientError.name;
    }
}
