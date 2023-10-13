import { Race } from "../f1api-client/api/Race.js";
import {
    CheerioCrawler,
    Configuration,
    createCheerioRouter,
    createPlaywrightRouter,
    PlaywrightCrawler,
    CheerioCrawlerOptions,
    PlaywrightCrawlerOptions
} from "crawlee";
import { MemoryStorage } from "@crawlee/memory-storage";
import { Logger } from "../logger/Logger.js";
import { ScraperError } from "./error/ScraperError.js";
import { Team } from "../f1api-client/api/Team.js";
import { Engine } from "../f1api-client/api/Engine.js";
import { Driver } from "../f1api-client/api/Driver.js";
import { TeamData } from "./TeamData.js";
import { QualifyingData } from "./QualifyingData.js";
import { RaceData } from "./RaceData.js";
import { ResultData } from "./ResultData.js";
import { FastestLapData } from "./FastestLapData.js";
import { TopSpeedData } from "./TopSpeedData.js";
import { LeadingLapsData } from "./LeadingLapsData.js";
import { FastestPitStopData } from "./FastestPitStopData.js";

type RaceDataUserData<T extends RaceData> = {
    raceData: T;
};

type DriverOfRaceDataRequest<T extends RaceData> = {
    userData: RaceDataUserData<T>;
    url: string;
};

export class Scraper {
    private readonly logger: Logger;

    constructor(logger: Logger) {
        this.logger = logger;
    }

    private getCheerioCrawler(options: CheerioCrawlerOptions) {
        return new CheerioCrawler(
            options,
            // https://github.com/apify/crawlee/discussions/2062
            new Configuration({
                storageClient: new MemoryStorage({
                    persistStorage: false // Allows to rescrape
                })
            })
        );
    }

    private getPlaywrightCrawler(options: PlaywrightCrawlerOptions) {
        return new PlaywrightCrawler(
            options,
            // https://github.com/apify/crawlee/discussions/2062
            new Configuration({
                storageClient: new MemoryStorage({
                    persistStorage: false // Allows to rescrape
                })
            })
        );
    }

    private getTrimmedText(cheerio: cheerio.Cheerio): string {
        return cheerio.text().trim();
    }

    private async logScraping<T>(execute: () => Promise<T>, logMessage: string) {
        this.logger.logScrape(logMessage);
        try {
            const scrapedData = await execute();
            this.logger.logScraped(logMessage, scrapedData);
            return scrapedData;
        } catch (error) {
            throw new ScraperError(error);
        }
    }

    private async logOnHandlerError(execute: () => Promise<void> | void) {
        try {
            await execute();
        } catch (error) {
            this.logger.logAppError(new ScraperError(error));
        }
    }

    async scrapeRacesOfSeason(seasonYear: number): Promise<Race[]> {
        const logMessage = `races of season ${seasonYear}`;
        const motorSportStatsBaseUrl = "https://www.motorsportstats.com";
        const startUrl = `${motorSportStatsBaseUrl}/series/fia-formula-one-world-championship/calendar/${seasonYear}`;
        const raceMap = new Map<string, Race>();

        type UserData = {
            raceName: string;
        };

        type RaceRequest = {
            userData: UserData;
            url: string;
        };

        const raceRequests: RaceRequest[] = [];

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                $("tbody tr").each((_index, row) => {
                    const columns = $(row).children("td");
                    const date = this.getTrimmedText(columns.eq(1));
                    const raceLink = columns.eq(2).find("a").eq(0);
                    const raceName = this.getTrimmedText(raceLink);
                    const raceUrl = `${motorSportStatsBaseUrl}${raceLink.attr("href")}`;
                    const race = new Race(raceName, date);
                    raceMap.set(race.raceName, race);
                    const userData: UserData = {
                        raceName: race.raceName
                    };
                    raceRequests.push({
                        userData: userData,
                        url: raceUrl
                    });
                });
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        const playwrightRouter = createPlaywrightRouter();

        playwrightRouter.addDefaultHandler<UserData>(async ({ request, page }) => {
            await this.logOnHandlerError(async () => {
                await page.getByText("Race time").first().click();
                await page.getByText("Your time").click();
                const time = await page
                    .locator("h3 ~ div")
                    .filter({ hasText: "Race" })
                    .locator("div")
                    .nth(1)
                    .textContent();
                if (time === null) {
                    throw new ScraperError(`Could no scrape race time from ${request.url}`);
                }
                const race = raceMap.get(request.userData.raceName);
                if (race !== undefined) {
                    race.setTime(time.trim());
                }
            });
        });

        const playwrightCrawler = this.getPlaywrightCrawler({
            requestHandler: playwrightRouter
        });

        return await this.logScraping(async () => {
            await cheerioCrawler.run([startUrl]);
            await playwrightCrawler.run(raceRequests);
            return [...raceMap.values()];
        }, logMessage);
    }

    async scrapeTeamDataOfSeason(seasonYear: number): Promise<TeamData[]> {
        const url = `https://www.racefans.net/${seasonYear}-f1-season/${seasonYear}-f1-drivers-and-teams/`;
        const logMessage = `team data of season ${seasonYear} from ${url}`;
        const teamData: TeamData[] = [];

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                $(".entry-content h3").each((index_, element) => {
                    const team = new Team(this.getTrimmedText($(element)));
                    const teamSpecificData = $(element).nextUntil("p:contains('Other drivers')");
                    const engine = new Engine(
                        $(teamSpecificData).filter("p:contains('Engines')").text().replace("Engines:", "").trim()
                    );
                    const driverData = $(teamSpecificData).find("strong a");
                    const drivers: Driver[] = [];
                    $(driverData).each((index_, element) => {
                        drivers.push(Driver.ofName(this.getTrimmedText($(element))));
                    });
                    teamData.push({ team: team, engine: engine, drivers: drivers });
                });
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        return await this.logScraping(async () => {
            await cheerioCrawler.run([url]);
            return teamData;
        }, logMessage);
    }

    async scrapeRaceUrls(seasonYear: number): Promise<string[]> {
        const url = `https://www.motorsport-total.com/formel-1/ergebnisse/${seasonYear}`;
        const logMessage = `race urls of season ${seasonYear} from ${url}`;
        const raceUrls: string[] = [];

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                $("tbody a").each((index_, raceLink) => {
                    raceUrls.push($(raceLink).attr("href") as string);
                });
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        return await this.logScraping(async () => {
            await cheerioCrawler.run([url]);
            return raceUrls;
        }, logMessage);
    }

    private async scrapeDriverOfRaceData<T extends RaceData>(requests: DriverOfRaceDataRequest<T>[]): Promise<T[]> {
        const data: T[] = [];

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler<RaceDataUserData<T>>(async ({ request, $ }) => {
            await this.logOnHandlerError(() => {
                const driverName = this.getTrimmedText($(".driver-name"));
                request.userData.raceData.driver = Driver.ofName(driverName);
                data.push(request.userData.raceData);
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        await cheerioCrawler.run(requests);
        return data;
    }

    async scrapeQualifyingDataOfRace(race: Race, raceUrl: string): Promise<QualifyingData[]> {
        const logMessage = `qualifying data of race '${race.raceName}' from ${raceUrl}`;
        const driverRequests: DriverOfRaceDataRequest<QualifyingData>[] = [];

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                $("h3:contains('Gesamtklassement') + div tbody tr")
                    .has("td.driver")
                    .each((index_, row) => {
                        const columns = $(row).children("td");
                        const position = Number(this.getTrimmedText(columns.eq(0)));
                        const team = new Team(this.getTrimmedText(columns.eq(3)));
                        const driverUrl = columns.find(".driver a").attr("href");
                        if (driverUrl === undefined) {
                            throw new ScraperError(`Could not scrape driver url from ${raceUrl}`);
                        }
                        const userData: RaceDataUserData<QualifyingData> = {
                            raceData: new QualifyingData(position, team)
                        };
                        driverRequests.push({
                            userData: userData,
                            url: driverUrl
                        });
                    });
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        return await this.logScraping(async () => {
            await cheerioCrawler.run([`${raceUrl}/qualifying`]);
            return await this.scrapeDriverOfRaceData<QualifyingData>(driverRequests);
        }, logMessage);
    }

    async scrapeResultDataOfRace(race: Race, raceUrl: string): Promise<ResultData[]> {
        const logMessage = `result data of race '${race.raceName}' from ${raceUrl}`;
        const driverRequests: DriverOfRaceDataRequest<ResultData>[] = [];

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                const tables = $(".content tbody");
                // Scrape result data for driver who finished the race
                tables
                    .eq(0)
                    .children("tr")
                    .has("td.text-right")
                    .each((index_, row) => {
                        const columns = $(row).children("td");
                        const position = Number(this.getTrimmedText(columns.eq(0)));
                        const driverUrl = columns.eq(2).find("a").attr("href");
                        if (driverUrl === undefined) {
                            throw new ScraperError(`Could not scrape driver url from ${raceUrl}`);
                        }
                        const userData: RaceDataUserData<ResultData> = {
                            raceData: new ResultData(position)
                        };
                        driverRequests.push({
                            userData: userData,
                            url: driverUrl
                        });
                    });
                // Scrape result data for driver who did not finish the race
                tables
                    .eq(1)
                    .children("tr")
                    .has("td.text-right")
                    .each((index_, row) => {
                        const columns = $(row).children("td");
                        const driverUrl = columns.eq(2).find("a").attr("href");
                        if (driverUrl === undefined) {
                            throw new ScraperError(`Could not scrape driver url from ${raceUrl}`);
                        }
                        const userData: RaceDataUserData<ResultData> = {
                            raceData: new ResultData(undefined, true)
                        };
                        driverRequests.push({
                            userData: userData,
                            url: driverUrl
                        });
                    });
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        return await this.logScraping(async () => {
            await cheerioCrawler.run([`${raceUrl}/rennen`]);
            return await this.scrapeDriverOfRaceData<ResultData>(driverRequests);
        }, logMessage);
    }

    async scrapeFastestLapData(race: Race, raceUrl: string): Promise<FastestLapData[]> {
        const logMessage = `fastest lap data of race '${race.raceName}' from ${raceUrl}`;
        const driverRequests: DriverOfRaceDataRequest<FastestLapData>[] = [];

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                $(".content tbody tr")
                    .has("td.driver")
                    .each((index_, row) => {
                        const columns = $(row).children("td");
                        const time = this.getTrimmedText(columns.eq(4));
                        const driverUrl = columns.eq(2).find("a").attr("href");
                        if (driverUrl === undefined) {
                            throw new ScraperError(`Could not scrape driver url from ${raceUrl}`);
                        }
                        const userData: RaceDataUserData<FastestLapData> = {
                            raceData: new FastestLapData(time)
                        };
                        driverRequests.push({
                            userData: userData,
                            url: driverUrl
                        });
                    });
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        return await this.logScraping(async () => {
            await cheerioCrawler.run([`${raceUrl}/rennen/schnellste-runden`]);
            return await this.scrapeDriverOfRaceData<FastestLapData>(driverRequests);
        }, logMessage);
    }

    async scrapeTopSpeedData(race: Race, raceUrl: string): Promise<TopSpeedData[]> {
        const logMessage = `top speed data of race '${race.raceName}' from ${raceUrl}`;
        const driverRequests: DriverOfRaceDataRequest<TopSpeedData>[] = [];

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                $(".content tbody tr")
                    .has("td.driver")
                    .each((index_, row) => {
                        const columns = $(row).children("td");
                        const speed = this.getTrimmedText(columns.eq(3));
                        const driverUrl = columns.eq(1).find("a").attr("href");
                        if (driverUrl === undefined) {
                            throw new ScraperError(`Could not scrape driver url from ${raceUrl}`);
                        }
                        const userData: RaceDataUserData<TopSpeedData> = {
                            raceData: TopSpeedData.ofSpeedString(speed)
                        };
                        driverRequests.push({
                            userData: userData,
                            url: driverUrl
                        });
                    });
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        return await this.logScraping(async () => {
            await cheerioCrawler.run([`${raceUrl}/rennen/topspeeds`]);
            return await this.scrapeDriverOfRaceData<TopSpeedData>(driverRequests);
        }, logMessage);
    }

    async scrapeLeadingLapsData(race: Race, raceUrl: string): Promise<LeadingLapsData[]> {
        const logMessage = `leading laps data of race '${race.raceName}' from ${raceUrl}`;
        const driverRequests: DriverOfRaceDataRequest<LeadingLapsData>[] = [];

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                $("h3:contains('Runden in Führung') + div tbody tr")
                    .has("td.text-right")
                    .each((index_, row) => {
                        const columns = $(row).children("td");
                        const numberOfLaps = Number(this.getTrimmedText(columns.eq(4)));
                        const driverUrl = columns.eq(2).find("a").attr("href");
                        if (driverUrl === undefined) {
                            throw new ScraperError(`Could not scrape driver url from ${raceUrl}`);
                        }
                        const userData: RaceDataUserData<LeadingLapsData> = {
                            raceData: new LeadingLapsData(numberOfLaps)
                        };
                        driverRequests.push({
                            userData: userData,
                            url: driverUrl
                        });
                    });
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        return await this.logScraping(async () => {
            await cheerioCrawler.run([`${raceUrl}/rennen/fuehrungsrunden`]);
            return await this.scrapeDriverOfRaceData<LeadingLapsData>(driverRequests);
        }, logMessage);
    }

    async scrapeFastestPitStopData(race: Race, raceUrl: string): Promise<FastestPitStopData> {
        const logMessage = `fastest pit stop data of race '${race.raceName}' from ${raceUrl}`;
        let driverRequest: DriverOfRaceDataRequest<FastestPitStopData>;

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                const columns = $("tr.green").first().children("td");
                const duration = this.getTrimmedText(columns.eq(5));
                const driverUrl = columns.eq(2).find("a").attr("href");
                if (driverUrl === undefined) {
                    throw new ScraperError(`Could not scrape driver url from ${raceUrl}`);
                }
                const userData: RaceDataUserData<FastestPitStopData> = {
                    raceData: new FastestPitStopData(duration)
                };
                driverRequest = {
                    userData: userData,
                    url: driverUrl
                };
            });
        });

        const cheerioCrawler = this.getCheerioCrawler({
            requestHandler: cheerioRouter
        });

        return await this.logScraping(async () => {
            await cheerioCrawler.run([`${raceUrl}/rennen/boxenstopps`]);
            const fastestPitStopData = await this.scrapeDriverOfRaceData<FastestPitStopData>([driverRequest]);
            if (fastestPitStopData[0] === undefined) {
                throw new ScraperError(`Could not scrape fastest pit stop data from ${raceUrl}`);
            }
            return fastestPitStopData[0];
        }, logMessage);
    }
}
