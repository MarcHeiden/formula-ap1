import { Driver } from "../api-client/api/Driver.js";

export abstract class RaceData {
    driver?: Driver;

    protected constructor(driver?: Driver) {
        this.driver = driver;
    }
}
