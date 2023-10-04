export interface ApiError {
    httpStatusCode: number;
    message: string;
    timestamp: string;
    validationErrors?: {
        fieldName: string;
        message: string;
    }[];
}
