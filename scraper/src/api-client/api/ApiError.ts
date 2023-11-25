/**
 * Represents an API error response.
 */
export type ApiError = {
    httpStatusCode: number;
    message: string;
    timestamp: string;
    validationErrors?: {
        fieldName: string;
        message: string;
    }[];
};
