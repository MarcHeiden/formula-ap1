# API

The API is built in Java with Spring Boot and PostgreSQL. Gradle is used as the build tool.

## API Documentation

The API endpoints are documented with the OpenAPI Specification 3.0.
To avoid dealing with a single large file, the
parameter, response and schema components are documented in seperated files.
During the deployment of the documentation to GitHub Pages, the files are bundled into a
[Swagger UI](https://swagger.io/tools/swagger-ui/) compliant file
(see [`Bundle and deploy OpenAPI to GitHub Pages`](../.github/workflows/bundle-and-deploy-openapi.yml) workflow).
Thanks to a feature of the [Redocly CLI](https://github.com/Redocly/redocly-cli),
which is used for linting and bundling of the documentation, a public and an internal documentation
can be generated, as paths for the public documentation can be excluded if the `x-internal` field is set to `true`.

### Lint OpenAPI Documentation

In order to lint the OpenAPI documentation locally, as done by the
[`Lint OpenAPI`](../.github/workflows/lint-openapi.yml) workflow, install the Redocly CLI globally via npm:

```shell
> npm install @redocly/cli@"^1.0.2" -g
```

and run:

```shell
> redocly lint api-internal --config ./api/openapi/redocly-cli-config.yml
```

## Domain Model

The following model represents the domain layer:

<img alt="Domain Model" src="domain-model.png">

VO stands for Value Object, which leads to an implementation as Embeddable in Spring.

## Package Structure

Every entity from the domain model has its own package, which is dived into a domain and an application package.
The first contains the entity and repository, while the latter is used for the application logic.
The Controller, Application Service and DTO can be found here.

## DTOs

To encapsulate domain objects, only DTOs are passed to the outside.

### Mapping

The mapping between a domain object and the corresponding DTO is implemented with the
[MapStruct](https://mapstruct.org/) mapping framework in a `*Mapper` class of the application package
(see e.g. [`SeasonMapper`](./src/main/java/api/season/application/SeasonMapper.java)).

### Validation

DTOs passed with a request are validated with
[Spring's Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html).
As the validation constraints are not always the same for create and update operations, the validation groups
[`OnCreate`](./src/main/java/api/validation/OnCreate.java) and
[`OnUpdate`](./src/main/java/api/validation/OnUpdate.java) are used to
specify when a constraint should apply, such as in the
[`DriverInfoDTO`](./src/main/java/api/driverofrace/application/DriverInfoDTO.java).

More information about validation with Spring can be found in
[this](https://reflectoring.io/bean-validation-with-spring-boot/) blog post.

## Exception Handling

Every exception that is thrown is caught by the
[`ApiExceptionHandler`](./src/main/java/api/exception/ApiExceptionHandler.java)
, which creates and returns a corresponding
[`ApiExceptionInfo`](./src/main/java/api/exception/ApiExceptionInfo.java)
from the caught exception.

### Base Exception

[`ApiException`](./src/main/java/api/exception/ApiException.java)
acts as abstract base exception that is extended by all project specific
exceptions.

## Query Parameters / Paging and Sorting

Paging is used to reduce network traffic if only a chunk of data is needed.
General information about Paging with Spring can be found in these blog posts on
[Reflectoring](https://reflectoring.io/spring-boot-paging/) and
[HowToDoInJava](https://howtodoinjava.com/spring-data/pagination-sorting-example/).

### Sort Property Handling

The `sort` query parameter can be used to sort entities by their properties.
As a [`PropertyReferenceException`](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/mapping/PropertyReferenceException.html)
is thrown, if a given sort property does not exist in the entity, properties that are named differently in the entity
and the corresponding DTO must be mapped to each other. <br>
This mapping is implemented in the
[`SortPropertyMapper`](./src/main/java/api/queryparameter/sort/SortPropertyMapper.java) interface.
It defines a `getSortProperties` and a `map` function. <br> The first returns a map of the related sort properties,
where the keys are the property names in the DTO and the values are the corresponding property names in the entity.
The concrete map is defined in the `SortPropertyMapper` implementation for the corresponding entity
(see e.g. [`SeasonSortPropertyMapper`](./src/main/java/api/season/application/SeasonSortPropertyMapper.java)). <br>
The latter uses the map to create a valid `Sort` object: For each sort property of the given `Sort` object is checked
whether it is a key in the `sortProperties` map.
If this is the case, the sort property is replaced by the corresponding property name of the entity.
If not, it is assumed that the property has the same name in the entity and the DTO, and it is checked whether
the sort property is contained in the `properties` list
(cf. e.g [`SeasonDTO`](./src/main/java/api/season/application/SeasonDTO.java)) of the DTO and whether it is not an ID,
as there is no point in sorting objects by their ID.
If true, the property is left as it is. Otherwise, an
[`ApiInvalidSortPropertyException`](./src/main/java/api/exception/ApiInvalidSortPropertyException.java) is thrown.

Since the `Sort` object is encapsulated inside a `Pageable` object, the `map` function is called
from the `handleSortProperties` functions of the
[`SortPropertyHandler`](./src/main/java/api/queryparameter/sort/SortPropertyHandler.java), which create a new
`Pageable` instance that can be passed to repository methods.

### Query Parameter Validation

Spring ignores invalid query parameters by default. For example, the GET Request
`/seasons?foo=bar` would be interpreted as GET `/seasons`. As this behaviour is not intuitive, the
`checkIfParametersAreValid` method of the
[`QueryParameter`](./src/main/java/api/queryparameter/QueryParameter.java) interface checks if given query parameters
are contained in the `queryParameters` set, defined for the corresponding entity and, if not,
throws an [`ApiInvalidQueryParameterException`](./src/main/java/api/exception/ApiInvalidQueryParameterException.java).

### Combination of Sort Property Handling and Query Parameter Validation

To check given query parameters and sort properties together the `handleQueryParameters` functions of the
[`QueryParameterHandler`](./src/main/java/api/queryparameter/QueryParameterHandler.java) are used.

### `ResponsePage`

Instead of passing the complete page object as response body, only
the properties `totalElements`, `totalPages`, `pageNumber`, `pageSize`
and `content` of the page are returned as an instance of
[`ResponsePage`](./src/main/java/api/responsepage/ResponsePage.java).

## Environment Variables

### `SPRING_PROFILES_ACTIVE`

Specifies the Spring Profile. Available options are `prod` and `dev`.
Depending on which profile is used, either the configurations defined in
[`application-prod.yml`](./src/main/resources/application-prod.yml) or
[`application-dev.yml`](./src/main/resources/application-dev.yml) take effect.

If set to `dev`, starting the application will also start the postgres service, specified
in the [`compose-dev.yml`](./compose-dev.yml), via the
[Spring Docker Compose Support](https://spring.io/blog/2023/06/21/docker-compose-support-in-spring-boot-3-1).

### `DEFAULT_PAGE_SIZE`

Specifies the default `pageSize` used for paging of collections.

**Default value:** 25

### `MAX_PAGE_SIZE`

Specifies the maximum allowed `pageSize`.

**Default value:** 100

### `prod` specific Environment Variables

#### `DB_URL`

Specifies the PostgreSQL URL.

**Default value:** //postgres:5432/postgres

#### `DB_USERNAME`

Specifies the PostgreSQL user.

#### `DB_PASSWORD`

Specifies the password for the specified `DB_USERNAME`.

## Docker

The [`Dockerfile`](./Dockerfile), used to build the image of the API available on
[DockerHub](https://hub.docker.com/r/marcheiden/formula-ap1), is based on suggestions of
[this](https://spring.io/guides/topicals/spring-boot-docker/) Spring Guide. It makes use of
[Multi-stage builds](https://docs.docker.com/build/building/multi-stage/) and JAR layers to
speed up subsequent builds.
The [Eclipse Temurin JRE image](https://hub.docker.com/_/eclipse-temurin) is used as base image.

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

**Notice:** Set `SPRING_PROFILES_ACTIVE` to `prod`, as `dev` is meant to be used for local development only
and does not work when running the application via Docker with the provided image.

## Formatting

This project uses the [palantir-java-format](https://github.com/palantir/palantir-java-format). To format files
accordingly, execute:

```shell
> ./gradlew spotlessJavaApply
```

To check whether all files comply with the format, as done by the
[`Spotless check`](../.github/workflows/spotless-check.yml) workflow, run:

```shell
> ./gradlew spotlessJavaCheck
```
