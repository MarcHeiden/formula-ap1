import { ApiType } from "./ApiType.js";

export class Team extends ApiType {
    readonly teamId?: string;
    readonly teamName: string;

    constructor(teamName: string) {
        super();
        this.teamName = teamName;
        // Use only Haas as team name for Haas F1 team
        if (teamName === "Haas F1") {
            this.teamName = "Haas";
        }
    }
}
