import { ApiType } from "./ApiType.js";

export class Team extends ApiType {
    readonly teamId?: string;
    readonly teamName: string;

    constructor(teamName: string) {
        super();
        this.teamName = teamName;
        if (teamName === "Haas F1") {
            this.teamName = "Haas";
        }
    }
}
