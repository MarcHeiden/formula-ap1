import { ApiClientError } from "./ApiClientError.js";
import { RequestError } from "got";

/**
 * Thrown if API request via Got failed.
 */
export class ApiRequestError extends ApiClientError {
    readonly requestError: RequestError;
    constructor(requestError: RequestError) {
        super();
        this.name = ApiRequestError.name;
        this.requestError = requestError;
    }
}
