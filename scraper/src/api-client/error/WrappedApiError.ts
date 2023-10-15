import { ApiClientError } from "./ApiClientError.js";
import { ApiError } from "../api/ApiError.js";

export class WrappedApiError extends ApiClientError {
    readonly apiError: ApiError;
    constructor(apiError: ApiError) {
        super();
        this.name = WrappedApiError.name;
        this.apiError = apiError;
    }
}
