import { ApiType } from "./ApiType.js";
import { AppError } from "../../error/AppError.js";

export class Driver extends ApiType {
    readonly driverId?: string;
    readonly firstName: string;
    readonly lastName: string;

    private constructor(firstName: string, lastName: string) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        if (this.firstName === "Carlos" && lastName.includes("Sainz")) {
            this.lastName = lastName.replace("Jnr", "").trim();
        }
        if (this.firstName === "Nico" && lastName === "Hülkenberg") {
            this.lastName = "Hulkenberg";
        }
        if (firstName === "Guanyu" && lastName === "Zhou") {
            this.firstName = lastName;
            this.lastName = firstName;
        }
    }

    static ofName(name: string): Driver {
        const splitDriverName = name.split(" ");
        const [firstName, ...lastNames] = splitDriverName;
        const lastName = lastNames.join(" ");
        if (firstName === undefined || lastName === undefined) {
            throw new AppError("Driver's firstName or lastName is undefined");
        }
        return new Driver(firstName, lastName);
    }
}
