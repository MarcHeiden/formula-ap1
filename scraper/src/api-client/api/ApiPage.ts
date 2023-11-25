import { ApiType } from "./ApiType.js";
import { ApiData } from "./ApiData.js";

/**
 * Represents an API page response of instances of {@link ApiType}.
 * @typeParam T - type of instance of {@link ApiType}
 */
export class ApiPage<T extends ApiType> extends ApiData {
    readonly totalElements: number;
    readonly totalPages: number;
    readonly pageNumber: number;
    readonly pageSize: number;
    readonly content?: T[];

    constructor(totalElements: number, totalPages: number, pageNumber: number, pageSize: number, content?: T[]) {
        super();
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.content = content;
    }
}
