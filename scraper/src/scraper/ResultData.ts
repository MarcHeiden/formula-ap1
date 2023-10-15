import { Driver } from "../api-client/api/Driver.js";
import { RaceData } from "./RaceData.js";

export class ResultData extends RaceData {
    readonly position?: number;
    readonly dnf?: boolean;

    constructor(position?: number, dnf?: boolean, driver?: Driver) {
        super(driver);
        this.position = position;
        this.dnf = dnf;
    }
}
