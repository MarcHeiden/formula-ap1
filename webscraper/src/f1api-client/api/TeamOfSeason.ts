import { ApiType } from "./ApiType.js";

export class TeamOfSeason extends ApiType {
    readonly teamOfSeasonId?: string;
    readonly seasonId?: string;
    readonly teamId?: string;
    readonly engineId?: string;
    driverIds: (string | undefined)[];

    constructor(driverIds: (string | undefined)[], seasonId?: string, teamId?: string, engineId?: string) {
        super();
        this.seasonId = seasonId;
        this.teamId = teamId;
        this.engineId = engineId;
        this.driverIds = driverIds;
    }
}
