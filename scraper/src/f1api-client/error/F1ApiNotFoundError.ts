import { F1ApiClientError } from "./F1ApiClientError.js";

export class F1ApiNotFoundError extends F1ApiClientError {
    constructor(message: string) {
        super(message);
        this.name = F1ApiNotFoundError.name;
    }
}
