import { F1ApiClientError } from "./F1ApiClientError.js";
import { ApiError } from "../api/ApiError.js";

export class F1ApiError extends F1ApiClientError {
    apiError: ApiError;
    constructor(apiError: ApiError) {
        super();
        this.name = F1ApiError.name;
        this.apiError = apiError;
    }
}
