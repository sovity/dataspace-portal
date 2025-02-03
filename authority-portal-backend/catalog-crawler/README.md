<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/authority-portal">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Catalog Crawler</h3>

  <p align="center">
    <a href="https://github.com/sovity/authority-portal/issues/new?assignees=&labels=kind%2Fbug&projects=&template=bug_report.yaml">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

The Catalog Crawler is an additional deployment unit needed to determine the online status of registered connectors and populate the Data Catalog:

- It is a modified EDC connector with the task to crawl the other connectors' public data offers.
- It periodically checks the Data Space Portal's connector list for its environment.
- It crawls the given connectors in regular intervals.
- It writes the data offers and connector statuses into the Data Space Portal DB.
- Each environment configured in the Data Space Portal requires its own Catalog Crawler with credentials for that environment's DAPS.

## Why does this component exist?

The Data Space Portal uses a non-EDC stack and thus it cannot read the catalogs of participating connectors directly.

## Deployment

Please see the [Productive Deployment Guide](../../docs/deployment-guide/goals/production/README.md) for more information.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
