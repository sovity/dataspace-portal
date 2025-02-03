<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/authority-portal">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">Data Space Portal API TypeScript Client Library</h3>

  <p align="center">
    <a href="https://github.com/sovity/authority-portal/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/authority-portal/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this component

TypeScript Client Library to access APIs of our Data Space Portal Backend.

## How to install

Requires a NodeJS / NPM project.

```shell script
npm i --save @sovity.de/authority-portal-client
```

## How to use

Configure your Data Space Portal Client and use endpoints of our Data Space Portal
API:

```typescript
import {
    AuthorityPortalClient,
    ExamplePageQuery,
    ExamplePageResult,
    buildAuthorityPortalClient,
} from '@sovity.de/authority-portal-client';
import {ExampleQuery} from './ExampleQuery';

const authorityPortalClient: AuthorityPortalClient = buildAuthorityPortalClient(
    {
        backendUrl: 'https://my-portal.sovity.io',
    },
);

let query: ExamplePageQuery = {
    greeting: 'Hello World',
};

let result: ExamplePageResult = await authorityPortalClient.uiApi.examplePage();
```

## License

See [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
