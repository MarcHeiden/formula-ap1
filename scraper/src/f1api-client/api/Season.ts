import { ApiType } from "./ApiType.js";

export class Season extends ApiType {
    readonly seasonId?: string;
    readonly seasonYear: number;

    constructor(seasonYear: number) {
        super();
        this.seasonYear = seasonYear;
    }
}
