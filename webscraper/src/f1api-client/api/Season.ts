import { ApiType } from "./ApiType.js";

export interface Season extends ApiType {
    seasonId?: string;
    seasonYear: number;
}
