# Praxisprojekt

## Lint OpenAPI specification locally

Install [Redocly CLI](https://github.com/Redocly/redocly-cli)

```shell
> npm install @redocly/cli@"^1.0.2" -g
```

Lint OpenAPI

```shell
> redocly lint f1api-internal --config ./f1api/openapi/redocly-cli-config.yml
```

## Format Java Files with Spotless

```shell
> ./gradlew spotlessJavaApply
```
