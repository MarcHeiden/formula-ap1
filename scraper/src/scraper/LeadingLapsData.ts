import { RaceData } from "./RaceData.js";
import { Driver } from "../f1api-client/api/Driver.js";

export class LeadingLapsData extends RaceData {
    readonly numberOfLaps: number;

    constructor(numberOfLaps: number, driver?: Driver) {
        super(driver);
        this.numberOfLaps = numberOfLaps;
    }
}
