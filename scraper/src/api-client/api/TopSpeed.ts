import { ApiType } from "./ApiType.js";

export class TopSpeed extends ApiType {
    readonly speed: number;
    readonly driverId: string | undefined;

    constructor(speed: number, driverId: string | undefined) {
        super();
        this.speed = speed;
        this.driverId = driverId;
    }
}
