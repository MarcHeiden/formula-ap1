# Praxisprojekt

## Planung

- spätester geplanter Start Bachelorarbeit Mo. 23.10 => Abgabe 27.12 - noch 10 Wochen

### Woche 1-3 - API Entwicklung

- API Design mit OpenAPI Spezifikation v3 oder v3.1
- ggf. UML anpassen
- Auf Spring Boot 3 migrieren und Verwendung von Gradle statt Maven
- Refactoring + Neuimplementierung:
  - HATEOAS (Hypermedia as the Engine of Application State) entfernen
  - PATCH/DELETE Mappings implementieren
  - evtl. unterschiedliche DTOs für verschiedene HTTP Verben
  - Unterbindung von zirkulären Abhängigkeiten
- (Tests)

### Woche 4 - Webscraper

- Webscraper neu implementieren
  - Verwendung der [F1 API](https://documenter.getpostman.com/view/11586746/SztEa7bL#07ddea42-7282-4b56-a2f0-0558e2e4ed49) für:
    - Rennen einer Saison
    - Teams einer Saison
    - Fahrer eines Teams einer Saison

### Woche 4/5-7 - Deployment

- VPS Konfiguration mit ansible:
  - SSH Setup
  - Docker installieren
  - DNS Konfiguration
  - Firewall Konfiguration
- Bereitstellung der Applikationen (Spring, Webscraper) als Docker Compose Projekt
  - evtl. Buildpack verwenden
  - Reverse Proxy/Api Gateway Konfiguration:
    - Rate Limiting
    - Authentifizierung, Autorisierung
- ggf. Monitoring
- evtl. CI/CD mit GitHub Actions
- Bereitstellung der Dokumentation
  - vlt. Vercel, GitHub Pages

### Woche 8-9/10 - Dokumentation

## Resources

### Spring

https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide

### OpenApi

https://www.ionos.de/digitalguide/websites/web-entwicklung/was-ist-openapi/

https://www.openapis.org/what-is-openapi

https://learn.openapis.org/

https://www.baeldung.com/spring-rest-openapi-documentation

https://github.com/springdoc/springdoc-openapi

https://openapi-generator.tech/

#### Documentation

https://github.com/Redocly/redoc

https://github.com/swagger-api/swagger-ui

https://github.com/peter-evans/swagger-github-pages

https://github.com/cloud-annotations/docusaurus-openapi

https://github.com/rohit-gohri/redocusaurus/

https://github.com/blueswen/mkdocs-swagger-ui-tag

https://github.com/rapi-doc/RapiDoc

https://github.com/stoplightio/elements

##### Sass

https://bump.sh/

#### Tests

https://martinfowler.com/articles/practical-test-pyramid.html

https://www.altexsoft.com/blog/api-testing/

https://github.com/Cornutum/tcases

https://github.com/stepci/stepci

https://github.com/ForAllSecure/mapi-action

https://github.com/rest-assured/rest-assured

https://github.com/karatelabs/karate/tree/master

https://github.com/Blazemeter/taurus

### API Gateways

#### Apache Apisix

https://www.predic8.de/apisix-api-gateway.htm

https://dev.to/apisix/secure-spring-boot-rest-api-with-apache-apisix-api-gateway-1nmg

https://blog.frankel.ch/spring-cloud-gateway-apache-apisix/

#### Kong

https://www.codecentric.de/wissens-hub/blog/spring-boot-kong

https://www.baeldung.com/kong

https://suraj-batuwana.medium.com/managing-spring-boot-micro-services-with-kong-part-1-install-kong-c652eec67a35

#### Janus

https://hellofresh.gitbooks.io/janus/content/

### Rate Limiting

https://konghq.com/blog/engineering/how-to-design-a-scalable-rate-limiting-algorithm

https://systemsdesign.cloud/SystemDesign/RateLimiter

https://github.com/bucket4j/bucket4j

https://github.com/resilience4j/resilience4j

### Reverse Proxy

#### Traefik

https://doc.traefik.io/traefik/

Only allow Get Requests => no authentification needed:

https://doc.traefik.io/traefik/routing/routers/#rule

### F1 API

http://ergast.com/mrd/faq/#latency

### VPS/Cloud

https://www.oracle.com/de/cloud/free/#always-free

### Webscraping

#### Python

https://github.com/scrapy/scrapy

https://github.com/psf/requests-html

https://github.com/MechanicalSoup/MechanicalSoup

https://github.com/alirezamika/autoscraper

#### Javscript, Typscript

https://github.com/apify/crawlee

https://github.com/cheeriojs/cheerio

#### JVM

https://github.com/jhy/jsoup

### DNS

https://github.com/isc-projects/bind9

https://github.com/NLnetLabs/unbound

https://github.com/DNSCrypt/dnscrypt-proxy
