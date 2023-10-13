import { ApiType } from "./ApiType.js";

export class FastestLap extends ApiType {
    readonly time: string;
    readonly driverId: string | undefined;

    constructor(time: string, driverId: string | undefined) {
        super();
        this.time = time;
        this.driverId = driverId;
    }
}
