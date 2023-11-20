ARG appRootPath=/app
ARG extractedJarLayers=${appRootPath}/build/extracted

FROM eclipse-temurin:17-jdk-alpine AS build
ARG appRootPath
WORKDIR ${appRootPath}
COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY src src
# Cache build dependencies
# https://docs.docker.com/engine/reference/builder/#run---mount
RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build --exclude-task test
ARG extractedJarLayers
# Extract jar in layers
# https://www.baeldung.com/docker-layers-spring-boot
# https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#packaging.layers
RUN mkdir -p ${extractedJarLayers} && java -Djarmode=layertools -jar build/libs/*.jar extract --destination ${extractedJarLayers}

FROM eclipse-temurin:17-jre-alpine as prod
# Run app as system user app
RUN addgroup -S app && adduser -S app -G app
USER app:app
ARG appRootPath
WORKDIR ${appRootPath}
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
# Copy application artifacts from build
ARG extractedJarLayers
COPY --from=build ${extractedJarLayers}/dependencies/ ./
COPY --from=build ${extractedJarLayers}/spring-boot-loader/ ./
COPY --from=build ${extractedJarLayers}/snapshot-dependencies/ ./
COPY --from=build ${extractedJarLayers}/application/ ./
