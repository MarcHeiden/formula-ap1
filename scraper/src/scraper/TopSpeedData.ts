import { RaceData } from "./RaceData.js";
import { Driver } from "../api-client/api/Driver.js";

export class TopSpeedData extends RaceData {
    readonly speed: number;

    private constructor(speed: number, driver?: Driver) {
        super(driver);
        this.speed = speed;
    }

    static ofSpeedString(speedString: string): TopSpeedData {
        // Map "350,5" to "350.5" number format
        const speed = Number(speedString.replace(",", "."));
        return new TopSpeedData(speed);
    }
}
