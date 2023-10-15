import { Team } from "../api-client/api/Team.js";
import { Engine } from "../api-client/api/Engine.js";
import { Driver } from "../api-client/api/Driver.js";

export class TeamData {
    readonly team: Team;
    readonly engine: Engine;
    readonly drivers: Driver[];

    constructor(team: Team, engine: Engine, drivers: Driver[]) {
        this.team = team;
        this.engine = engine;
        this.drivers = drivers;
    }
}
