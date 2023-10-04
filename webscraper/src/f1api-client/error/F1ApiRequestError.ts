import { F1ApiClientError } from "./F1ApiClientError.js";
import { RequestError } from "got";

export class F1ApiRequestError extends F1ApiClientError {
    requestError: RequestError;
    constructor(requestError: RequestError) {
        super();
        this.name = F1ApiRequestError.name;
        this.requestError = requestError;
    }
}
