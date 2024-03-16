import { ApiType } from "./ApiType.js";
import { AppError } from "../../error/AppError.js";

export class Driver extends ApiType {
    readonly driverId?: string;
    readonly firstName: string;
    readonly lastName: string;

    private constructor(firstName: string, lastName: string) {
        super();
        // Internationalize name of Nico Hülkenberg to Nico Hulkenberg
        if (firstName === "Nico" && lastName === "Hülkenberg") {
            lastName = "Hulkenberg";
        }
        // Remove suffix Jnr from Carlos Sainz' name
        if (firstName === "Carlos" && lastName.includes("Sainz")) {
            lastName = lastName.replace("Jnr", "").trim();
        }
        this.firstName = firstName;
        this.lastName = lastName;
        // Correct wrong name for Zhou Guanyu
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
