import got, { Method, Options, RequestError, SearchParameters } from "got";
import { ApiData } from "./api/ApiData.js";
import { ApiType } from "./api/ApiType.js";
import { ApiError } from "./api/ApiError.js";
import { Season } from "./api/Season.js";
import { ApiPage } from "./api/ApiPage.js";
import { Race } from "./api/Race.js";
import { ApiClientError } from "./error/ApiClientError.js";
import { ApiNotFoundError } from "./error/ApiNotFoundError.js";
import { WrappedApiError } from "./error/WrappedApiError.js";
import { ApiRequestError } from "./error/ApiRequestError.js";
import { Logger } from "../logger/Logger.js";
import { TeamData } from "../scraper/TeamData.js";
import { Team } from "./api/Team.js";
import { Engine } from "./api/Engine.js";
import { Driver } from "./api/Driver.js";
import { TeamOfSeason } from "./api/TeamOfSeason.js";
import { QualifyingData } from "../scraper/QualifyingData.js";
import { Qualifying } from "./api/Qualifying.js";
import { ResultData } from "../scraper/ResultData.js";
import { Result } from "./api/Result.js";
import { FastestLapData } from "../scraper/FastestLapData.js";
import { FastestLap } from "./api/FastestLap.js";
import { TopSpeedData } from "../scraper/TopSpeedData.js";
import { TopSpeed } from "./api/TopSpeed.js";
import { LeadingLapsData } from "../scraper/LeadingLapsData.js";
import { LeadingLaps } from "./api/LeadingLaps.js";
import { FastestPitStopData } from "../scraper/FastestPitStopData.js";
import { FastestPitStop } from "./api/FastestPitStop.js";

export class ApiClient {
    private readonly logger: Logger;
    private readonly seasonsEndpoint = "seasons";
    private readonly teamsEndpoint = "teams";
    private readonly enginesEndpoint = "engines";
    private readonly driversEndpoint = "drivers";
    private readonly racesEndpoint = "races";
    private readonly teamOfSeasonEndpoint = "teamsOfSeasons";

    constructor(logger: Logger) {
        this.logger = logger;
    }

    private isApiError(responseBody: ApiData | ApiError): responseBody is ApiError {
        return (responseBody as ApiError).httpStatusCode !== undefined;
    }

    // On error: consider to look up retry and timeout settings
    // https://github.com/sindresorhus/got/blob/main/documentation/7-retry.md
    // https://github.com/sindresorhus/got/blob/main/documentation/6-timeout.md
    private async useApi<T extends ApiData>(
        endpoint: string | URL,
        searchParameters?: SearchParameters | URLSearchParams,
        method?: Method,
        body?: ApiType
    ): Promise<T> {
        const options = new Options({
            prefixUrl: process.env.API_BASE_URL,
            throwHttpErrors: false,
            method: method,
            json: body,
            searchParams: searchParameters
        });
        const client = got.extend(options);
        let responseBody: T | ApiError | undefined;
        try {
            responseBody = await client(endpoint).json();
        } catch (error) {
            if (error instanceof RequestError) {
                throw new ApiRequestError(error);
            }
            throw error;
        }
        if (responseBody === undefined) {
            throw new ApiClientError("Response body is undefined");
        }
        if (this.isApiError(responseBody)) {
            throw new WrappedApiError(responseBody);
        }
        return responseBody;
    }

    private async getApiTypes<T extends ApiType>(
        endpoint: string | URL,
        notFoundErrorMessage: string,
        searchParameters?: SearchParameters | URLSearchParams
    ): Promise<T[]> {
        const apiPage = await this.useApi<ApiPage<T>>(endpoint, searchParameters);
        if (apiPage.content === undefined) {
            throw new ApiNotFoundError(notFoundErrorMessage);
        }
        return apiPage.content;
    }

    private async getApiTypeBySearchParameters<T extends ApiType>(
        endpoint: string | URL,
        searchParameters: SearchParameters | URLSearchParams,
        notFoundErrorMessage: string
    ): Promise<T> {
        const apiTypes = await this.getApiTypes<T>(endpoint, notFoundErrorMessage, searchParameters);
        if (apiTypes[0] === undefined) {
            throw new ApiNotFoundError(notFoundErrorMessage);
        }
        return apiTypes[0];
    }

    private async getSeasonBySeasonYear(seasonYear: number): Promise<Season> {
        const searchParameters: SearchParameters = {
            seasonYear: seasonYear
        };
        const notFoundErrorMessage = `Season ${seasonYear} does not exist`;
        return await this.getApiTypeBySearchParameters<Season>(
            this.seasonsEndpoint,
            searchParameters,
            notFoundErrorMessage
        );
    }

    private async getTeamByTeamName(teamName: string): Promise<Team> {
        const searchParameters: SearchParameters = {
            teamName: teamName
        };
        const notFoundErrorMessage = `Team with the teamName '${teamName}' does not exist`;
        return await this.getApiTypeBySearchParameters<Team>(
            this.teamsEndpoint,
            searchParameters,
            notFoundErrorMessage
        );
    }

    private async getEngineByManufacturer(manufacturer: string): Promise<Engine> {
        const searchParameters: SearchParameters = {
            manufacturer: manufacturer
        };
        const notFoundErrorMessage = `Engine with the manufacturer '${manufacturer}' does not exist`;
        return await this.getApiTypeBySearchParameters<Engine>(
            this.enginesEndpoint,
            searchParameters,
            notFoundErrorMessage
        );
    }

    private async getDriverByName(firstName: string, lastName: string): Promise<Driver> {
        const searchParameters: SearchParameters = {
            firstName: firstName,
            lastName: lastName
        };
        // prettier-ignore
        const notFoundErrorMessage =
            `Driver with the firstName '${firstName}' and lastName '${lastName}' does not exist`;
        return await this.getApiTypeBySearchParameters<Driver>(
            this.driversEndpoint,
            searchParameters,
            notFoundErrorMessage
        );
    }

    private async getRaceByDate(date: string): Promise<Race> {
        const searchParameters: SearchParameters = {
            date: date
        };
        const notFoundErrorMessage = `Race with the date '${date}' does not exist`;
        return await this.getApiTypeBySearchParameters<Race>(
            this.racesEndpoint,
            searchParameters,
            notFoundErrorMessage
        );
    }

    private async getTeamOfSeasonBySeasonAndTeam(season: Season, team: Team): Promise<TeamOfSeason> {
        const searchParameters: SearchParameters = {
            seasonId: season.seasonId,
            teamId: team.teamId
        };
        // prettier-ignore
        const notFoundErrorMessage =
            `TeamOfSeason does not exist for the season ${season.seasonYear} and the team '${team.teamName}'`;
        return await this.getApiTypeBySearchParameters<TeamOfSeason>(
            this.teamOfSeasonEndpoint,
            searchParameters,
            notFoundErrorMessage
        );
    }

    async getRacesOfSeasonOrderedByDateAsc(seasonYear: number): Promise<Race[]> {
        const season = await this.getSeasonBySeasonYear(seasonYear);
        const endpoint = `${this.seasonsEndpoint}/${season.seasonId}/races`;
        const searchParameters: SearchParameters = {
            sort: "date,asc",
            pageSize: "50" // Get all races as there probably won't be more than 50 races in a season
        };
        const notFoundErrorMessage = `Season ${seasonYear} has no races`;
        return await this.getApiTypes<Race>(endpoint, notFoundErrorMessage, searchParameters);
    }

    private async getDriversOfTeamOfSeason(season: Season, team: Team): Promise<Driver[]> {
        const endpoint = `${this.seasonsEndpoint}/${season.seasonId}/teams/${team.teamId}/drivers`;
        const notFoundErrorMessage = `Team '${team.teamName}' has no drivers in the season ${season.seasonYear}`;
        return await this.getApiTypes<Driver>(endpoint, notFoundErrorMessage);
    }

    private async getDriverOfTeamOfSeasonByName(
        season: Season,
        team: Team,
        firstName: string,
        lastName: string
    ): Promise<Driver> {
        const drivers = await this.getDriversOfTeamOfSeason(season, team);
        const filteredDrivers = drivers.filter(
            (driver) => driver.firstName === firstName && driver.lastName === lastName
        );
        if (filteredDrivers.length === 0) {
            throw new ApiNotFoundError(
                // prettier-ignore
                `Team '${team.teamName}' has no driver with the firstName '${firstName}' ` +
                `and lastName '${lastName}' in the season ${season.seasonYear}`
            );
        }
        return filteredDrivers[0] as Driver;
    }

    private async post<T extends ApiType>(endpoint: string | URL, body: ApiType, logMessage: string): Promise<T> {
        const apiType = await this.useApi<T>(endpoint, undefined, "post", body);
        this.logger.logPosted(logMessage, apiType);
        return apiType;
    }

    private async postAndLogIfAlreadyExists<T extends ApiType>(
        endpoint: string | URL,
        body: ApiType,
        logMessage: string
    ): Promise<T | undefined> {
        try {
            return await this.post<T>(endpoint, body, logMessage);
        } catch (error) {
            if (error instanceof WrappedApiError) {
                if (error.apiError.httpStatusCode === 409 && error.apiError.message.toLowerCase().includes("already")) {
                    this.logger.logAlreadyExists(error, body);
                } else {
                    throw error;
                }
            } else {
                throw error;
            }
        }
    }

    private async postSeason(seasonYear: number) {
        const logMessage = `season ${seasonYear}`;
        const season = new Season(seasonYear);
        await this.postAndLogIfAlreadyExists(this.seasonsEndpoint, season, logMessage);
    }

    private async postRaceOfSeason(season: Season, race: Race) {
        const logMessage = `race of season ${season.seasonYear}`;
        const endpoint = `${this.seasonsEndpoint}/${season.seasonId}/races`;
        await this.postAndLogIfAlreadyExists(endpoint, race, logMessage);
    }

    async postRacesOfSeason(seasonYear: number, races: Race[]) {
        let season: Season;
        try {
            season = await this.getSeasonBySeasonYear(seasonYear);
        } catch (error) {
            if (error instanceof ApiNotFoundError) {
                this.logger.logAppError(error, "warn");
                this.logger.info(`Post season ${seasonYear} now and retry to post races`);
                await this.postSeason(seasonYear);
                await this.postRacesOfSeason(seasonYear, races);
                return;
            } else {
                throw error;
            }
        }
        await Promise.all(races.map((race) => this.postRaceOfSeason(season, race)));
    }

    private async postTeam(team: Team): Promise<Team | undefined> {
        const logMessage = "team";
        return await this.postAndLogIfAlreadyExists<Team>(this.teamsEndpoint, team, logMessage);
    }

    private async postEngine(engine: Engine): Promise<Engine | undefined> {
        const logMessage = "engine";
        return await this.postAndLogIfAlreadyExists<Engine>(this.enginesEndpoint, engine, logMessage);
    }

    private async postDriver(driver: Driver): Promise<Driver | undefined> {
        const logMessage = "driver";
        return await this.postAndLogIfAlreadyExists<Driver>(this.driversEndpoint, driver, logMessage);
    }

    private async postTeamOfSeason(teamOfSeason: TeamOfSeason): Promise<void> {
        const logMessage = "teamOfSeason";
        await this.postAndLogIfAlreadyExists(this.teamOfSeasonEndpoint, teamOfSeason, logMessage);
    }

    private async postTeamOrGetAlreadyExistingOne(team: Team): Promise<Team> {
        let postedTeam = await this.postTeam(team);
        if (postedTeam === undefined) {
            postedTeam = await this.getTeamByTeamName(team.teamName);
        }
        return postedTeam;
    }

    private async postEngineOrGetAlreadyExistingOne(engine: Engine): Promise<Engine> {
        let postedEngine = await this.postEngine(engine);
        if (postedEngine === undefined) {
            postedEngine = await this.getEngineByManufacturer(engine.manufacturer);
        }
        return postedEngine;
    }

    private async postDriverOrGetAlreadyExistingOne(driver: Driver): Promise<Driver> {
        let postedDriver = await this.postDriver(driver);
        if (postedDriver === undefined) {
            postedDriver = await this.getDriverByName(driver.firstName, driver.lastName);
        }
        return postedDriver;
    }

    async postTeamsOfSeason(seasonYear: number, teamData: TeamData[]) {
        let season: Season;
        try {
            season = await this.getSeasonBySeasonYear(seasonYear);
        } catch (error) {
            if (error instanceof ApiNotFoundError) {
                this.logger.logAppError(error, "warn");
                this.logger.info(`Post season ${seasonYear} now`);
                await this.postSeason(seasonYear);
                await this.postTeamsOfSeason(seasonYear, teamData);
                return;
            } else {
                throw error;
            }
        }
        const teamsOfSeason: TeamOfSeason[] = [];
        // Usage of Promise.all causes sql constraint violation in api
        for (const teamData_ of teamData) {
            const [team, engine, drivers] = await Promise.all([
                this.postTeamOrGetAlreadyExistingOne(teamData_.team),
                this.postEngineOrGetAlreadyExistingOne(teamData_.engine),
                Promise.all(teamData_.drivers.map((driver) => this.postDriverOrGetAlreadyExistingOne(driver)))
            ]);
            teamsOfSeason.push(
                new TeamOfSeason(
                    drivers.map((driver) => driver.driverId),
                    season.seasonId,
                    team.teamId,
                    engine.engineId
                )
            );
        }
        await Promise.all(teamsOfSeason.map((teamOfSeason) => this.postTeamOfSeason(teamOfSeason)));
    }

    private async addDriverToTeamOfSeason(teamOfSeason: TeamOfSeason, newDriver: Driver) {
        const logMessage = "teamOfSeason";
        const endpoint = `${this.teamOfSeasonEndpoint}/${teamOfSeason.teamOfSeasonId}`;
        teamOfSeason.driverIds = [newDriver.driverId];
        const patchedTeamOfSeason = await this.useApi<TeamOfSeason>(endpoint, undefined, "patch", teamOfSeason);
        this.logger.logPatched(logMessage, patchedTeamOfSeason);
    }

    private async postQualifyingForDriverOfRace(race: Race, qualifying: Qualifying) {
        const logMessage = `qualifying of race '${race.raceName}'`;
        const endpoint = `${this.racesEndpoint}/${race.raceId}/qualifying`;
        await this.postAndLogIfAlreadyExists(endpoint, qualifying, logMessage);
    }

    async postQualifyingOfRace(race: Race, qualifyingData: QualifyingData[]) {
        const qualifying = await Promise.all(
            qualifyingData.map(async (qualifyingData_) => {
                const team = await this.getTeamByTeamName(qualifyingData_.team.teamName);
                if (qualifyingData_.driver === undefined) {
                    throw new ApiClientError("Driver is undefined");
                }
                if (race.season === undefined) {
                    throw new ApiClientError(`Season of the race '${race.raceName}' is undefined`);
                }
                let driver: Driver;
                try {
                    driver = await this.getDriverOfTeamOfSeasonByName(
                        race.season,
                        team,
                        qualifyingData_.driver.firstName,
                        qualifyingData_.driver.lastName
                    );
                } catch (error) {
                    if (error instanceof ApiNotFoundError) {
                        this.logger.logAppError(error, "warn");
                        this.logger.info("Post driver now and patch teamOfSeason");
                        driver = await this.postDriverOrGetAlreadyExistingOne(qualifyingData_.driver);
                        const teamOfSeason = await this.getTeamOfSeasonBySeasonAndTeam(race.season, team);
                        await this.addDriverToTeamOfSeason(teamOfSeason, driver);
                    } else {
                        throw error;
                    }
                }
                return new Qualifying(qualifyingData_.position, driver.driverId, team.teamId);
            })
        );
        await Promise.all(qualifying.map((qualifying_) => this.postQualifyingForDriverOfRace(race, qualifying_)));
    }

    private async postResultForDriverOfRace(race: Race, result: Result) {
        const logMessage = `result of race '${race.raceName}'`;
        const endpoint = `${this.racesEndpoint}/${race.raceId}/result`;
        await this.postAndLogIfAlreadyExists(endpoint, result, logMessage);
    }

    async postResultOfRace(race: Race, resultData: ResultData[]) {
        const results = await Promise.all(
            resultData.map(async (resultData_) => {
                if (resultData_.driver === undefined) {
                    throw new ApiClientError("Driver is undefined");
                }
                const driver = await this.getDriverByName(resultData_.driver.firstName, resultData_.driver.lastName);
                return new Result(driver.driverId, resultData_.position, resultData_.dnf);
            })
        );
        await Promise.all(results.map((result) => this.postResultForDriverOfRace(race, result)));
    }

    private async postFastestLapForDriverOfRace(race: Race, fastestLap: FastestLap) {
        const logMessage = `fastest lap of race '${race.raceName}'`;
        const endpoint = `${this.racesEndpoint}/${race.raceId}/fastestLaps`;
        await this.postAndLogIfAlreadyExists(endpoint, fastestLap, logMessage);
    }

    async postFastestLapsOfRace(race: Race, fastestLapData: FastestLapData[]) {
        const fastestLaps = await Promise.all(
            fastestLapData.map(async (fastestLapData_) => {
                if (fastestLapData_.driver === undefined) {
                    throw new ApiClientError("Driver is undefined");
                }
                const driver = await this.getDriverByName(
                    fastestLapData_.driver.firstName,
                    fastestLapData_.driver.lastName
                );
                return new FastestLap(fastestLapData_.time, driver.driverId);
            })
        );
        await Promise.all(fastestLaps.map((fastestLap) => this.postFastestLapForDriverOfRace(race, fastestLap)));
    }

    private async postTopSpeedForDriverOfRace(race: Race, topSpeed: TopSpeed) {
        const logMessage = `top speed of race '${race.raceName}'`;
        const endpoint = `${this.racesEndpoint}/${race.raceId}/topSpeeds`;
        await this.postAndLogIfAlreadyExists(endpoint, topSpeed, logMessage);
    }

    async postTopSpeedsOfRace(race: Race, topSpeedData: TopSpeedData[]) {
        const topSpeeds = await Promise.all(
            topSpeedData.map(async (topSpeedData_) => {
                if (topSpeedData_.driver === undefined) {
                    throw new ApiClientError("Driver is undefined");
                }
                const driver = await this.getDriverByName(
                    topSpeedData_.driver.firstName,
                    topSpeedData_.driver.lastName
                );
                return new TopSpeed(topSpeedData_.speed, driver.driverId);
            })
        );
        await Promise.all(topSpeeds.map((topSpeed) => this.postTopSpeedForDriverOfRace(race, topSpeed)));
    }

    private async postLeadingLapsForDriverOfRace(race: Race, leadingLaps: LeadingLaps) {
        const logMessage = `leading laps of race '${race.raceName}'`;
        const endpoint = `${this.racesEndpoint}/${race.raceId}/leadingLaps`;
        await this.postAndLogIfAlreadyExists(endpoint, leadingLaps, logMessage);
    }

    async postLeadingLapsOfRace(race: Race, leadingLapsData: LeadingLapsData[]) {
        const leadingLaps = await Promise.all(
            leadingLapsData.map(async (leadingLapsData_) => {
                if (leadingLapsData_.driver === undefined) {
                    throw new ApiClientError("Driver is undefined");
                }
                const driver = await this.getDriverByName(
                    leadingLapsData_.driver.firstName,
                    leadingLapsData_.driver.lastName
                );
                return new LeadingLaps(leadingLapsData_.numberOfLaps, driver.driverId);
            })
        );
        await Promise.all(leadingLaps.map((leadingLaps_) => this.postLeadingLapsForDriverOfRace(race, leadingLaps_)));
    }

    async postFastestPitStopOfRace(race: Race, fastestPitStopData: FastestPitStopData) {
        const logMessage = `fastest pit stop of race '${race.raceName}'`;
        const endpoint = `${this.racesEndpoint}/${race.raceId}/fastestPitStop`;
        if (fastestPitStopData.driver === undefined) {
            throw new ApiClientError("Driver is undefined");
        }
        const driver = await this.getDriverByName(
            fastestPitStopData.driver.firstName,
            fastestPitStopData.driver.lastName
        );
        const fastestPitStop = new FastestPitStop(fastestPitStopData.duration, driver.driverId);
        await this.postAndLogIfAlreadyExists(endpoint, fastestPitStop, logMessage);
    }
}
