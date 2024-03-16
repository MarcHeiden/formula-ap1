import { ApiType } from "./ApiType.js";

export class Engine extends ApiType {
    readonly engineId?: string;
    readonly manufacturer: string;

    constructor(manufacturer: string) {
        super();
        // Rename Honda RBPT to Honda
        if (manufacturer === "Honda RBPT") {
            manufacturer = "Honda";
        }
        this.manufacturer = manufacturer;
    }
}
