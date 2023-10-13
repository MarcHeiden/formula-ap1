import { ApiType } from "./ApiType.js";

export class FastestPitStop extends ApiType {
    readonly duration: string;
    readonly driverId: string | undefined;

    constructor(duration: string, driverId: string | undefined) {
        super();
        this.duration = duration;
        this.driverId = driverId;
    }
}
