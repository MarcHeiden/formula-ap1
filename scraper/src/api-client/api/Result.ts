import { ApiType } from "./ApiType.js";

export class Result extends ApiType {
    readonly position?: number;
    readonly dnf?: boolean;
    readonly driverId: string | undefined;

    constructor(driverId: string | undefined, position?: number, dnf?: boolean) {
        super();
        this.position = position;
        this.dnf = dnf;
        this.driverId = driverId;
    }
}
