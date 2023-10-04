import got, { Options, Method, SearchParameters, RequestError } from "got";
import { ApiData } from "./api/ApiData.js";
import { ApiType } from "./api/ApiType.js";
import { ApiError } from "./api/ApiError.js";
import { Season } from "./api/Season.js";
import { ApiPage } from "./api/ApiPage.js";
import { Race } from "./api/Race.js";
import { F1ApiClientError } from "./error/F1ApiClientError.js";
import { F1ApiNotFoundError } from "./error/F1ApiNotFoundError.js";
import { F1ApiError } from "./error/F1ApiError.js";
import { F1ApiRequestError } from "./error/F1ApiRequestError.js";
import { Logger } from "../logger/Logger.js";

export class F1ApiClient {
    private logger: Logger;
    private seasonsEndpoint = "seasons";

    constructor(logger: Logger) {
        this.logger = logger;
    }

    private isApiError(responseBody: ApiData | ApiError): responseBody is ApiError {
        return (responseBody as ApiError).httpStatusCode !== undefined;
    }

    // TODO: Retry and timeout settings
    private async useApi<T extends ApiData>(
        endpoint: string | URL,
        searchParameters?: SearchParameters | URLSearchParams,
        method?: Method,
        body?: ApiType,
    ): Promise<T> {
        const options = new Options({
            prefixUrl: process.env.F1API_BASE_URL,
            throwHttpErrors: false,
            method: method,
            json: body,
            searchParams: searchParameters,
        });
        const client = got.extend(options);
        let responseBody: T | ApiError | undefined;
        try {
            responseBody = await client(endpoint).json();
        } catch (error) {
            if (error instanceof RequestError) {
                throw new F1ApiRequestError(error);
            }
            throw error;
        }
        if (responseBody === undefined) {
            throw new F1ApiClientError("Response body is undefined");
        }
        if (this.isApiError(responseBody)) {
            throw new F1ApiError(responseBody);
        }
        return responseBody;
    }

    private async getSeason(seasonYear: number): Promise<Season> {
        const searchParameters: SearchParameters = {
            seasonYear: seasonYear,
        };
        const responseBody = await this.useApi<ApiPage<Season>>(this.seasonsEndpoint, searchParameters);
        if (responseBody.content === undefined || responseBody.content[0] === undefined) {
            throw new F1ApiNotFoundError("Season not found");
        }
        return responseBody.content[0];
    }

    private async postSeason(seasonYear: number) {
        const postMessage = `season ${seasonYear}`;
        this.logger.logPost(postMessage);
        const season: Season = {
            seasonYear: seasonYear,
        };
        await this.useApi(this.seasonsEndpoint, undefined, "post", season);
        this.logger.logPosted(postMessage, [season]);
    }

    async postRacesOfSeason(seasonYear: number, races: Race[]) {
        const postMessage = `races of season ${seasonYear}`;
        this.logger.logPost(postMessage);
        let season: Season | undefined;
        try {
            season = await this.getSeason(seasonYear);
        } catch (error) {
            if (error instanceof F1ApiNotFoundError) {
                // Create season if not found and retry creation of races
                this.logger.warn(`Season ${seasonYear} does not exist. Create now and retry to post ${postMessage}.`);
                await this.postSeason(seasonYear);
                await this.postRacesOfSeason(seasonYear, races);
                return;
            } else {
                throw error;
            }
        }
        const endpoint = `${this.seasonsEndpoint}/${season?.seasonId}/races`;
        /*await Promise.all(races.map((race) => this.useApi(endpoint, undefined, "post", race)));*/
        for (const race of races) {
            try {
                await this.useApi(endpoint, undefined, "post", race);
            } catch (error) {
                if (error instanceof F1ApiError) {
                    // Log if race already exists and continue
                    if (error.apiError.httpStatusCode === 409) {
                        this.logger.logAlreadyExists(`Race already exists for season ${seasonYear}`, race);
                    } else {
                        throw error;
                    }
                } else {
                    throw error;
                }
            }
        }
        this.logger.logPosted(postMessage, races);
    }
}
