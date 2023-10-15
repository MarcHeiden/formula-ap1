import { Driver } from "../f1api-client/api/Driver.js";
import { Team } from "../f1api-client/api/Team.js";
import { RaceData } from "./RaceData.js";

export class QualifyingData extends RaceData {
    readonly position: number;
    readonly team: Team;

    constructor(position: number, team: Team, driver?: Driver) {
        super(driver);
        this.position = position;
        this.team = team;
    }
}
