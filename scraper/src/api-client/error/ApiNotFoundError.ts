import { ApiClientError } from "./ApiClientError.js";

export class ApiNotFoundError extends ApiClientError {
    constructor(message: string) {
        super(message);
        this.name = ApiNotFoundError.name;
    }
}
