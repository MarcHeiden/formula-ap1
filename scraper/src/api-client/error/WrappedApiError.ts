import { ApiClientError } from "./ApiClientError.js";
import { ApiError } from "../api/ApiError.js";

/**
 * Thrown if API returns instance of {@link ApiError}.
 */
export class WrappedApiError extends ApiClientError {
    readonly apiError: ApiError;
    constructor(apiError: ApiError) {
        super();
        this.name = WrappedApiError.name;
        this.apiError = apiError;
    }
}
