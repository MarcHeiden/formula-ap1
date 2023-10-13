import { ApiType } from "./ApiType.js";

export class LeadingLaps extends ApiType {
    readonly numberOfLaps: number;
    readonly driverId: string | undefined;

    constructor(numberOfLaps: number, driverId: string | undefined) {
        super();
        this.numberOfLaps = numberOfLaps;
        this.driverId = driverId;
    }
}
