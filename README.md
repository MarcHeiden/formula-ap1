# Praxisprojekt

## Lint OpenAPI specification locally

Install [Redocly CLI](https://github.com/Redocly/redocly-cli)

```shell
> npm install @redocly/cli@"^1.0.2" -g
```

Lint OpenAPI

```shell
> redocly lint api-internal --config ./api/openapi/redocly-cli-config.yml
```

## Format Java Files with Spotless

```shell
> ./gradlew spotlessJavaApply
```

## Apply Prettier format

```shell
> npm run prettier-apply
```

## Lint web scraper project locally

```shell
> npm run lint
```

