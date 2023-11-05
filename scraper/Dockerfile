ARG appRootPath=/app

FROM node:18 AS build
ARG appRootPath
WORKDIR ${appRootPath}
COPY package*.json tsconfig.json ./
COPY src src
RUN --mount=type=cache,target=/root/.npm npm ci
RUN npm run build

# https://playwright.dev/docs/docker
FROM mcr.microsoft.com/playwright:v1.39.0 as prod
ARG appRootPath
WORKDIR ${appRootPath}
COPY package*.json crawlee.json ./
COPY --from=build ${appRootPath}/dist ./dist
# Create directory for logs and set pwuser as owner
RUN mkdir logs && chown pwuser:pwuser logs
VOLUME ["/app/logs"]
# Only install production dependencies
RUN --mount=type=cache,target=/root/.npm npm ci --omit=dev --omit=optional
USER pwuser
ENV NODE_ENV production
ENTRYPOINT ["node", "dist/main.js"]
