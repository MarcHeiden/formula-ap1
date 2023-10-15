import { RaceData } from "./RaceData.js";
import { Driver } from "../f1api-client/api/Driver.js";

export class FastestPitStopData extends RaceData {
    readonly duration: string;

    constructor(duration: string, driver?: Driver) {
        super(driver);
        // map "0:24.227" to "24.227" time format
        if (duration.match("^0:(\\d){2}.(\\d){3}$")) {
            const splitDuration = duration.split(":");
            duration = `${splitDuration[1]}`;
        }
        this.duration = duration;
    }
}
