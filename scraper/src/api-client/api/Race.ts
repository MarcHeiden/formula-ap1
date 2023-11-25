import { ApiType } from "./ApiType.js";
import { Season } from "./Season.js";
import { AppError } from "../../error/AppError.js";

export class Race extends ApiType {
    readonly raceId?: string;
    readonly raceName: string;
    date?: string;
    time?: string;
    cancelled?: boolean;
    readonly season?: Season;

    constructor(raceName: string, date?: string, time?: string, cancelled?: boolean) {
        super();
        this.raceName = raceName;
        this.setDate(date);
        this.setTime(time);
        this.cancelled = cancelled;
    }

    setDate = (value: string | undefined) => {
        const months = new Map([
            ["JAN", "01"],
            ["FEB", "02"],
            ["MAR", "03"],
            ["APR", "04"],
            ["MAY", "05"],
            ["JUN", "06"],
            ["JUL", "07"],
            ["AUG", "08"],
            ["SEP", "09"],
            ["OCT", "10"],
            ["NOV", "11"],
            ["DEC", "12"]
        ]);
        value = value?.toUpperCase();
        // Map "2023 Mar 19, Sunday" to "2023-19-03" date format
        if (value?.match(/^\d{4} [A-Z]{3} \d{2}.*$/)) {
            // Remove week day
            value = value.replace(/,.*/, "");
            const splitDate = value.split(" ");
            if (splitDate[1] === undefined) {
                throw new AppError(`Race date '${value}' has the wrong format`);
            }
            value = `${splitDate[0]}-${months.get(splitDate[1])}-${splitDate[2]}`;
        }
        this.date = value;
    };

    setTime = (value: string | undefined) => {
        // Map "18:00" to "18:00:00" time format
        if (value?.match(/^\d{2}:\d{2}$/)) {
            value += ":00";
        }
        this.time = value;
    };
}
