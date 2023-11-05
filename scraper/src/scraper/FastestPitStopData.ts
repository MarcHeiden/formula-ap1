import { RaceData } from "./RaceData.js";
import { Driver } from "../api-client/api/Driver.js";

export class FastestPitStopData extends RaceData {
    readonly duration: string;

    constructor(duration: string, driver?: Driver) {
        super(driver);
        this.duration = duration;
    }
}
