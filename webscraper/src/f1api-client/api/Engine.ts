import { ApiType } from "./ApiType.js";

export class Engine extends ApiType {
    readonly engineId?: string;
    readonly manufacturer: string;

    constructor(manufacturer: string) {
        super();
        this.manufacturer = manufacturer;
    }
}
