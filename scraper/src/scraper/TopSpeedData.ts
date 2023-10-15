import { RaceData } from "./RaceData.js";
import { Driver } from "../f1api-client/api/Driver.js";

export class TopSpeedData extends RaceData {
    readonly speed: number;

    private constructor(speed: number, driver?: Driver) {
        super(driver);
        this.speed = speed;
    }

    static ofSpeedString(speedString: string): TopSpeedData {
        const speed = Number(speedString.replace(",", "."));
        return new TopSpeedData(speed);
    }
}
