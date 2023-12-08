# Scraper

The scraper is built with TypeScript and Node.js.

It runs in an infinite loop to scrape season and race data
and pass it to the API (see [`main.ts`](./src/main.ts)). The race data
(qualifying, result, fastest laps, leading laps, top speeds, fastest pit stop)
is scraped 4 hours after each race (see `createRaceData` function in [`main.ts`](./src/main.ts)), as
the rules state that a race must end 3 hours after the official race start.

## Scraping of Data

The scraping logic is implemented in the [`Scraper`](./src/scraper/Scraper.ts) class using the
[Crawlee library](https://crawlee.dev/). Thereby, the
[`CheerioCrawler`](https://crawlee.dev/docs/quick-start#cheeriocrawler) is used to scrape data from pages that
use server-side rendering and the
[`PlaywrightCrawler`](https://crawlee.dev/docs/quick-start#playwrightcrawler) is used when JavaScript needs to
be executed to scrape the content of a page.

As logs are handled by the [`Logger`](./src/logger/Logger.ts) class, the built-in logging functionality of Crawlee
is disabled via the [`crawlee.json`](./crawlee.json) config file.

## API Client

The [`ApiClient`](./src/api-client/ApiClient.ts) class provides methods to interact with the API.
HTTP requests are made using the [got library](https://github.com/sindresorhus/got).

**Notice:** API responses are not deserialized into class objects, even though they are defined as class objects.
This works because only the properties of an object and not the methods, defined in the corresponding
class, are accessed... a quick and dirty implementation 🙃.

## Logging

Logging of app events is handled by the [`Logger`](./src/logger/Logger.ts) class. It acts as a wrapper
around the [winston logger library](https://github.com/winstonjs/winston). All logs are logged to the console
and a logfile, which is named `log<date>.txt` and located in the `./logs` directory. Every month a new
logfile will be created. Logfiles older than 7 months are deleted.

## Errors

### Base Error

[`AppError`](./src/error/AppError.ts) acts as a general application error
that is extended by all other app errors.

### Error Handling

Errors are caught by the `uncaughtException` event listener (see [`main.ts`](./src/main.ts)) and handled by the
[`ErrorHandler`](./src/error/ErrorHandler.ts). Before the process is exited with the exit code 1, the error is logged by
the [`Logger`](./src/logger/Logger.ts).

## Environment Variables

### `API_BASE_URL`

Specifies the base URL of the API.

**Example:** http://localhost:8080

### `START_SEASON_YEAR`

Specifies the season year of the **first** season for which data is scraped and passed to the API.

**Example:** 2023

### `LOG_LEVEL`

Specifies the severity of events that are logged.

**Default value:** info <br>
**Available options:** info, warn, error

### `SCRAPE_SEASON_DATA_OF_FIRST_SEASON`

Specifies if season data is scraped for the **first** season (defined by `START_SEASON_YEAR`).
This has no effect on race data, as it is always scraped.
Can be set to `false` if season data already exists for the first season.

**Default value:** true <br>
**Available options:** true, false

## Docker

The image available on [DockerHub](https://hub.docker.com/r/marcheiden/formula-ap1) is built with
the [`Dockerfile`](./Dockerfile), which uses [Multi-stage builds](https://docs.docker.com/build/building/multi-stage/) to
speed up subsequent builds.
As the [`PlaywrightCrawler`](https://crawlee.dev/docs/quick-start#playwrightcrawler)
of the [Crawlee library](https://crawlee.dev/) uses [Playwright](https://playwright.dev/) under the hood, the image is
based on a Playwright image. More information about it can be found [here](https://playwright.dev/docs/docker) and
[here](https://mcr.microsoft.com/en-us/product/playwright/about). Since the Playwright image is itself
based on an Ubuntu image and ships with three browsers, the image is larger than 2 GB.

### Build Image

To build a container image from the Dockerfile, run:

```shell
> docker build -t <imageName>:<imageTag> --pull [--no-cache] .
```

#### Multi-platform Images

Multi-platform images can be built with builder instances whose driver type is `docker-container`.
This command creates a builder with the name `multi-platform-builder`:

```shell
> docker buildx create --driver docker-container --name multi-platform-builder
```

To use the created builder to build an `amd64` and `arm64` linux image, execute:

```shell
> docker buildx --builder multi-platform-builder build \
  --push \
  -t <imageName>:<imageTag> \
  --platform linux/amd64,linux/arm64 \
  --pull \
  [--no-cache] .
```

**Notice:** The `--push` option will try to push the built images to DockerHub. To push them to a local registry, take
a look at [this](https://uninterrupted.tech/blog/creating-docker-images-that-can-run-on-different-platforms-including-raspberry-pi/)
blog post. Unfortunately, this seems to be the only easy way to extract the built images, as Docker currently does
not support loading of multi-platform images in a stable way and therefore the `--load` option does not work
(cf. [GitHub Issue](https://github.com/docker/buildx/issues/59)).

### Environment Variables

See [Environment Variables](#environment-variables).

**Notice:** Do not specify the environment variables in the [`.env`](./.env) file, as it is not copied
into the image during the build process.

## Linting

[ESLint](https://eslint.org/) is used to lint the codebase. The lint configuration can be found in
the [`.eslintrc.yml`](./.eslintrc.yml) file.
To lint the codebase, as done by the `Lint with eslint` job of the
[`Lint scraper and run prettier`](../.github/workflows/lint-and-check-prettier-scraper.yml) workflow, run:

```shell
> npm run lint
```

## Formatting

Prettier is used as code formatter. The configuration is defined in the [`.prettierrc.yml`](./.prettierrc.yml) file.
The [`.prettierignore`](./.prettierignore) file is used to exclude certain files from being formatted.
To check whether all files comply with the format, as done by the `Prettier check` job of the
[`Lint scraper and run prettier check`](../.github/workflows/lint-and-check-prettier-scraper.yml) workflow, run:

```shell
> npm run prettier-check
```

To format all files accordingly, execute:

```shell
> npm run prettier-apply
```
