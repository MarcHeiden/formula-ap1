# Scraper

The scraper is built with TypeScript and NodeJs.

It runs in an infinite loop to scrape season and race data
and pass them to the API (see [`main.ts`](./src/main.ts)). The race data
(qualifying, result, fastest laps, leading laps, top speeds, fastest pit stop)
are scraped 4 hours after each race (see `createRaceData` function in [`main.ts`](./src/main.ts)) as the rules state that a race must end 3 hours after the official
race start.

<!-- It is meant to run in an infinite loop (see `main.ts`) to scrape season and race data
and pass it to the API.

It is meant to run in an infinite loop to scrape season and race data
and pass it to the API. The race data (qualifying, result, fastest laps, leading laps, top speeds, fastest pit stop)
are thereby scraped four hours after each race as the rules say that a race must end 3 hours after the official
race start. -->

<!-- ## Application Design

The application is meant to run in an infinite loop to scrape season and race data
and pass it to the API (see `main.ts`).

The logic when data is scraped and passed to the API is defined in the `main.ts`.

The `main.ts` is the entrypoint of the application. The logic when data is scraped and
passed to the API is defined here.

The `main.ts` is the entrypoint of the application.

The infinite loop in `main.ts` calls.

## `main.ts`

The `main.ts` is the entrypoint of the application. The logic when data is scraped and
passed to the API is defined here.

The infinite loop in the `main.ts` executes repetitively the `createSeasonData` and `createRaceData` function.

As the application is to run indefinitely the infinite loop in the `main.ts` executes
repetitively the `createSeasonData` and `createRaceData` function. As the name

As the application is meant to run indefinitely.

The logic when data is scraped and passed to the API is defined in `main.ts`.

This file contains the infinite loop -->

<!-- ## Package Structure

The project is divided into two major packages. The `scraper` package contains -->

## Scraping of Data

The scraping logic is implemented in the [`Scraper` class](./src/scraper/Scraper.ts) using the
[Crawlee library](https://crawlee.dev/). Thereby, the
[`CheerioCrawler`](https://crawlee.dev/docs/quick-start#cheeriocrawler) is used to scrape data from pages that
use server-side rendering. In contrast, the
[`PlaywrightCrawler`](https://crawlee.dev/docs/quick-start#playwrightcrawler) is used when JavaScript needs to
be executed to scrape the page content.

<!-- Here scraping of content of pages that use server-side rendering is done with
CheerioCrawler. -->

As logs are handled by the [`Logger` class](./src/logger/Logger.ts), the built-in logging functionality of Crawlee
is disabled via the [`crawlee.json`](./crawlee.json) config file.

## API Client

<!-- ## `ApiClient` -->

The [`ApiClient` class](./src/api-client/ApiClient.ts) provides methods to interact with the API.
HTTP requests are made using the [got library](https://github.com/sindresorhus/got).

<!-- It uses [got](https://github.com/sindresorhus/got) as underlying HTTP request library. -->

**Notice:** API responses are not deserialized into class objects, even though they are defined as class objects.
This works because only the properties of an object and not the methods defined in the corresponding
class are accessed... a quick and dirty implementation 🙃.

<!-- **Notice:** API responses are not deserialized to class objects. They remain simple
JavaScript objects and therefore only the properties of the object and not methods defined in the
class can be accessed. -->

## Logging

<!-- The [`Logger`](./src/logger/Logger.ts) class acts as a wrapper around the
[winston logger library](https://github.com/winstonjs/winston). All logs are logged to the console
and a logfile which is named `log<date>.txt` and located in the `./logs` directory. Every month a new
logfile will be created. Logfiles older than 7 months are deleted. -->

Logging of app events is handled by the [`Logger` class](./src/logger/Logger.ts). It acts as a wrapper
around the [winston logger library](https://github.com/winstonjs/winston). All logs are logged to the console
and a logfile which is named `log<date>.txt` and located in the `./logs` directory. Every month a new
logfile will be created. Logfiles older than 7 months are deleted.

## Error Handling

Errors are caught by the uncaughtException event listener (see [`main.ts`](./src/main.ts)) and handled by the
[`ErrorHandler`](./src/error/ErrorHandler.ts). Before the process is exited with the exit code 1, the error is logged by
the [`Logger`](./src/logger/Logger.ts).

[`AppError`](./src/error/AppError.ts) acts as a general application error
that is extended by all other app errors.

## Environment Variables

### `API_BASE_URL`

Specifies the base URL of the API

**Example:** http://localhost:8080

### `START_SEASON_YEAR`

Specifies the season year of the **first** season for which data is scraped and passed to the API.

**Example:** 2023

### `LOG_LEVEL`

Specifies the severity of events that are logged.

**Default value:** info <br>
**Available options:** info, warn, error

### `SCRAPE_SEASON_DATA_OF_FIRST_SEASON`

Specifies if season data are scraped for the **first** season (defined by `START_SEASON_YEAR`).
This has no effect on race data, as they are always scraped.
Can be set to `false` if season data already exist for the first season.

**Default value:** true <br>
**Available options:** true, false

## Docker

The image available on [DockerHub](https://hub.docker.com/repository/docker/marcheiden/formula-ap1) is built with
the [`Dockerfile`](./Dockerfile), that uses [Multi-stage builds](https://docs.docker.com/build/building/multi-stage/) to
speed up subsequent builds.
As the [`PlaywrightCrawler`](https://crawlee.dev/docs/quick-start#playwrightcrawler)
of the [Crawlee library](https://crawlee.dev/) uses [Playwright](https://playwright.dev/) under the hood, the image is
based on a Playwright image. More information about it can be found [here](https://playwright.dev/docs/docker) and
[here](https://mcr.microsoft.com/en-us/product/playwright/about). Since the Playwright image is itself
based on an Ubuntu image and ships with three browsers, the image is larger than 2 GB.

### Build Image

To build a container image from the Dockerfile run:

```shell
> docker build -t <imageName>:<imageTag> --pull [--no-cache] .
```

### Environment Variables

See [Environment Variables](#environment-variables).

**Notice:** Do not specify the Environment Variables in the [`.env`](./.env) file, as it is not copied
into the image during the build process.

## Linting

[ESLint](https://eslint.org/) is used to lint the codebase. The lint configuration can be found in
the [`.eslintrc.yml`](./.eslintrc.yml) file.
To lint the codebase, as done by the `Lint with eslint` job of the
[`Lint scraper and run prettier` workflow](../.github/workflows/lint-and-check-prettier-scraper.yml), run:

```shell
> npm run lint
```

<!-- This also done by the `Lint scraper and run prettier` check workflow.

The project uses the typescript-eslint plugin to lint the codebase with eslint.
The lint configuration can be found in the `.eslintrc.yml` file. -->

## Formatting

Prettier is used as code formatter. The configuration is defined in the [`.prettierrc.yml`](./.prettierrc.yml) file.
The [`.prettierignore`](./.prettierignore) file is used to exclude certain files from being formatted.
To check whether all files comply with the format, as done by the `Prettier check` job of the
[`Lint scraper and run prettier check` workflow](../.github/workflows/lint-and-check-prettier-scraper.yml), run:

```shell
> npm run prettier-check
```

To format all files accordingly, execute:

```shell
> npm run prettier-apply
```
