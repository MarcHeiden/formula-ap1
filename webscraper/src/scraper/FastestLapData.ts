import { RaceData } from "./RaceData.js";
import { Driver } from "../f1api-client/api/Driver.js";

export class FastestLapData extends RaceData {
    readonly time: string;

    constructor(time: string, driver?: Driver) {
        super(driver);
        this.time = time;
    }
}
