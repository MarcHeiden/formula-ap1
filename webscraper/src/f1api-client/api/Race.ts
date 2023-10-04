import { ApiType } from "./ApiType.js";

export class Race implements ApiType {
    raceId?: string;
    raceName: string;
    date: string;
    private time?: string;
    cancelled?: boolean;

    constructor(raceName: string, date: string, time?: string, cancelled?: boolean) {
        this.raceName = raceName;
        // map "05.03.2023" to "2023-03-05" date format
        if (date.match("^\\d{2}\\.\\d{2}\\.\\d{4}$")) {
            const splitDate = date.split(".");
            date = `${splitDate[2]}-${splitDate[1]}-${splitDate[0]}`;
        }
        this.date = date;
        this.setTime(time);
        this.cancelled = cancelled;
    }

    setTime = (value: string | undefined) => {
        // map "18:00" to "18:00:00" time format
        if (value?.match("^\\d{2}:\\d{2}$")) {
            value += ":00";
        }
        this.time = value;
    };
}
