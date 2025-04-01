<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->

<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
<a href="https://github.com/sovity/dataspace-portal">
<img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
</a>

<h3 align="center">Data Space Portal</h3>
<p align="center" style="padding-bottom:16px">
Backend + Frontend + E2E Tests
<br />
<a href="https://github.com/sovity/dataspace-portal/issues/new?template=bug_report.md">Report Bug</a>
·
<a href="https://github.com/sovity/dataspace-portal/issues/new?template=feature_request.md">Request Feature</a>
</p>
</div>

## About This Component

Backend for the Data Space Portal. Built with Quarkus, Kotlin and JOOQ.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Development

### Local Development

Use Unit Tests and Integration Tests to test complexity and functionality of API endpoints.

The naming of the types of tests is not standardized. The
distinction between E2E, Integration and Unit Tests is made here purely for the sake of explanation.
One might argue all the these types of tests to be unit tests:

- __(Backend) E2E Tests__:
    - These tests call entire API endpoints of a running application started by `@QuarkusTest`.
    - These tests need to manually clean the database after themselves, since the transaction breaks from "making a call
      to the backend via REST", so `@TestTransaction` is unavailable.
    - You can still use Quarkus' `@InjectMock` to mock parts of the running application, e.g. a KeycloakService.
- __Integration Tests__:
    - These tests test ApiServices of a running backend started by `@QuarkusTest`
    - They are faster than E2E Tests due to being able to use `@TestTransaction` which allows them to not dirty the DB.
    - They might not test for HTTP-Related things such as additional headers as the above E2E Tests do.
    - You can still use Quarkus' `@InjectMock` to mock parts of the running application, e.g. a KeycloakService.
- __Unit Tests__:
    - These tests should not be annotated with `@QuarkusTest` and should not require a DB.
    - If the unit under test is a CDI unit (e.g. a Service), Mockito will be used to create mocks for all the unit's
      dependencies.
    - __Unit tests are lightning fast__, because they don't need to start a quarkus application, postgresql
      testcontainers,
      etc.
    - Life is easy when all code with branch-wise complexity is tested locally by unit tests, so it can then be used in
      integration tests without having to test all branches again.
    - A large part of being a senior software developer is about learning how to structure code so that the complexity
      can be tested locally, allowing both code and tests to stay simple and manageable.
    - Unit test don't work well when the unit under test contains db concerns.
      For testing code that uses JooQ Integration Tests are recommended.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Local E2E Development

To start the backend in development mode, a JDK17 and docker are required.

```shell
./gradlew clean quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/

<p align="right">(<a href="#readme-top">back to top</a>)</p>

#### Local E2E Development: Basic Auth

To simplify local development there are dev users available, see:

- [application.properties](authority-portal-quarkus/src/main/resources/application.properties)
    - Test users and roles. Requests should use Basic Auth.
- [realm.dev.json](authority-portal-quarkus/src/main/resources/realm.dev.json)
    - Contains the test users.
    - Roles come from the `application.properties`, though.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Keycloak

Local development uses a Keycloak instance launched by Quarkus Dev Services both for authentication and manual testing
of our interactions with the Keycloak Admin API.

To add new Roles to the Dev Keycloak or change the Keycloak Realm
Config, please refer to our [Keycloak Realm Export Documentation](docs/dev/keycloak-realm-export).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## License

See [`LICENSE`](../LICENSE) for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contact

contact@sovity.de

<p align="right">(<a href="#readme-top">back to top</a>)</p>
