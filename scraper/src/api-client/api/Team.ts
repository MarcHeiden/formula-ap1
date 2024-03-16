import { ApiType } from "./ApiType.js";

export class Team extends ApiType {
    readonly teamId?: string;
    readonly teamName: string;

    constructor(teamName: string) {
        super();
        // Rename Haas F1 to Haas
        if (teamName === "Haas F1") {
            teamName = "Haas";
        }
        // Rename RB to Racing Bulls
        if (teamName === "RB") {
            teamName = "Racing Bulls";
        }
        this.teamName = teamName;
    }
}
