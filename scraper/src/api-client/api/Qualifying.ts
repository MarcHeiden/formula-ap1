import { ApiType } from "./ApiType.js";

export class Qualifying extends ApiType {
    readonly position: number;
    readonly driverId: string | undefined;
    readonly teamId: string | undefined;

    constructor(position: number, driverId: string | undefined, teamId: string | undefined) {
        super();
        this.position = position;
        this.driverId = driverId;
        this.teamId = teamId;
    }
}
