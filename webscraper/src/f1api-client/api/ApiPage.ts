import { ApiType } from "./ApiType.js";
import { ApiData } from "./ApiData.js";

export interface ApiPage<T extends ApiType> extends ApiData {
    totalElements: number;
    totalPages: number;
    pageNumber: number;
    pageSize: number;
    content?: T[];
}
