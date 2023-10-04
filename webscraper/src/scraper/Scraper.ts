import { Race } from "../f1api-client/api/Race.js";
import { CheerioCrawler, createCheerioRouter, createPlaywrightRouter, PlaywrightCrawler } from "crawlee";
import { Logger } from "../logger/Logger.js";
import { ScraperError } from "./error/ScraperError.js";

export class Scraper {
    private logger: Logger;

    constructor(logger: Logger) {
        this.logger = logger;
    }

    private async logOnHandlerError(execute: () => Promise<void> | void) {
        try {
            await execute();
        } catch (error) {
            this.logger.logAppError(new ScraperError(error));
        }
    }

    private async catchErrors(execute: () => Promise<void>) {
        try {
            await execute();
        } catch (error) {
            throw new ScraperError(error);
        }
    }

    async scrapeRacesOfSeason(seasonYear: number): Promise<Race[]> {
        const scrapeMessage = `races of season ${seasonYear}`;
        const motorSportStatsBaseUrl = "https://www.motorsportstats.com";
        const startUrl = `${motorSportStatsBaseUrl}/series/fia-formula-one-world-championship/calendar/${seasonYear}`;
        const raceLable = "race";

        type RaceUrl = {
            label: string;
            userData: { raceName: string };
            url: string;
        };

        type UserData = {
            raceName: string;
        };

        const raceUrls: RaceUrl[] = [];

        const raceMap = new Map<string, Race>();

        const cheerioRouter = createCheerioRouter();

        cheerioRouter.addDefaultHandler(async ({ $ }) => {
            await this.logOnHandlerError(() => {
                $("tbody tr").each((_index, row) => {
                    const columns = $(row).children("td");
                    const date = columns.eq(1).text();
                    const raceLink = columns.eq(2).find("a").eq(0);
                    const raceName = raceLink.text();
                    const raceUrl = `${motorSportStatsBaseUrl}${raceLink.attr("href")}`;
                    const race = new Race(raceName, date);
                    raceMap.set(race.raceName, race);
                    const userData: UserData = {
                        raceName: race.raceName,
                    };
                    raceUrls.push({
                        label: raceLable,
                        userData: userData,
                        url: raceUrl,
                    });
                    /*await enqueueLinks({
                        label: raceLable,
                        baseUrl: motorSportStatsBaseUrl,
                        urls: urls,
                        userData: race,
                    });*/
                });
            });
        });

        const cheerioCrawler = new CheerioCrawler({
            requestHandler: cheerioRouter,
        });

        const playwrightRouter = createPlaywrightRouter();

        playwrightRouter.addHandler<UserData>(raceLable, async ({ request, page, parseWithCheerio }) => {
            await this.logOnHandlerError(async () => {
                await page.waitForSelector(".styled__SessionName-sc-vpn25g-3");
                const $ = await parseWithCheerio();
                const containsRace = $(".styled__SessionName-sc-vpn25g-3:contains('Race')");
                const race = raceMap.get(request.userData.raceName);
                if (race !== undefined) {
                    race.setTime($(containsRace).next().text());
                }
            });
        });

        const playwrightCrawler = new PlaywrightCrawler({
            requestHandler: playwrightRouter,
        });

        this.logger.logScrape(scrapeMessage);

        await this.catchErrors(async () => {
            await cheerioCrawler.run([startUrl]);
            await playwrightCrawler.run(raceUrls);
        });

        const races = [...raceMap.values()];

        this.logger.logScraped(scrapeMessage, races);

        return races;
    }
}
