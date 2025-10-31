/*
 * Data Space Portal
 * Copyright (C) 2025 sovity GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.sovity.authorityportal.web.tests.services.connector

import de.sovity.authorityportal.api.UiResource
import de.sovity.authorityportal.api.model.ConfigureProvidedConnectorWithCertificateRequest
import de.sovity.authorityportal.api.model.ConfigureProvidedConnectorWithJwksRequest
import de.sovity.authorityportal.api.model.CreateConnectorRequest
import de.sovity.authorityportal.api.model.CreateConnectorStatusDto
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.ConnectorContractOffersExceeded
import de.sovity.authorityportal.db.jooq.enums.ConnectorDataOffersExceeded
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus
import de.sovity.authorityportal.db.jooq.enums.ConnectorType
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.seeds.utils.dummyDevConnectorId
import de.sovity.authorityportal.seeds.utils.dummyDevOrganizationId
import de.sovity.authorityportal.seeds.utils.dummyDevOrganizationName
import de.sovity.authorityportal.seeds.utils.dummyDevUserUuid
import de.sovity.authorityportal.web.Roles
import de.sovity.authorityportal.web.tests.loadTestResource
import de.sovity.authorityportal.web.tests.useDevUser
import de.sovity.authorityportal.web.tests.useMockNow
import de.sovity.authorityportal.web.tests.withOffsetDateTimeComparator
import de.sovity.authorityportal.web.thirdparty.daps.DapsClient
import de.sovity.authorityportal.web.thirdparty.daps.DapsClientService
import de.sovity.authorityportal.web.utils.idmanagement.ClientIdUtils
import io.quarkus.test.InjectMock
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.NotAuthorizedException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.TableLike
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class ConnectorManagementApiServiceTest {

    @Inject
    lateinit var uiResource: UiResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Inject
    lateinit var dsl: DSLContext

    @Inject
    lateinit var clientIdUtils: ClientIdUtils

    @InjectMock
    lateinit var dapsClientService: DapsClientService

    @Test
    @TestTransaction
    fun `get connector details as authority user`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.AUTHORITY_USER))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            connector(0, 0, 0) {
                it.name = "Connector 0"
            }
            connector(1, 0, 0)
            connector(2, 0, 0)

            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.getConnector(dummyDevConnectorId(0, 0))

        // assert
        assertThat(result.connectorId).isEqualTo(dummyDevConnectorId(0, 0))
        assertThat(result.connectorName).isEqualTo("Connector 0")
        assertThat(result.organizationId).isEqualTo(dummyDevOrganizationId(0))
    }

    @Test
    @TestTransaction
    fun `get connector details as operator admin`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.OPERATOR_ADMIN))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            connector(0, 0, 0) {
                it.name = "Connector 0"
            }
            connector(1, 0, 0)
            connector(2, 0, 0)

            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.getConnector(dummyDevConnectorId(0, 0))

        // assert
        assertThat(result.connectorId).isEqualTo(dummyDevConnectorId(0, 0))
        assertThat(result.connectorName).isEqualTo("Connector 0")
        assertThat(result.organizationId).isEqualTo(dummyDevOrganizationId(0))
    }

    @Test
    @TestTransaction
    fun `get all connectors as authority user for environment test shows only connectors from that env`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.AUTHORITY_USER))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            connector(0, 0, 0)
            connector(1, 0, 0)

            connector(2, 0, 0) {
                it.environment = "other-environment"
            }

            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.getAllConnectors("test")

        // assert
        assertThat(result.connectors).hasSize(2)
        assertThat(result.connectors).noneMatch { it.environment.environmentId != "test" }
        assertThat(result.connectors).noneMatch { it.id == dummyDevConnectorId(0, 2) }
        assertThat(result.connectors).allMatch { it.frontendUrl == null }

        assertThat(result.connectors[0].id).isEqualTo(dummyDevConnectorId(0, 0))
        assertThat(result.connectors[1].id).isEqualTo(dummyDevConnectorId(0, 1))
    }

    @Test
    @TestTransaction
    fun `get all connectors as operator admin for different environment shows only connectors from that env`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.OPERATOR_ADMIN))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            connector(0, 0, 0)
            connector(1, 0, 0)

            connector(2, 0, 0) {
                it.environment = "other-environment"
            }

            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.getAllConnectors("other-environment")

        // assert
        assertThat(result.connectors).hasSize(1)
        assertThat(result.connectors).noneMatch { it.environment.environmentId != "other-environment" }
        assertThat(result.connectors).noneMatch { it.id == dummyDevConnectorId(0, 0) }
        assertThat(result.connectors).noneMatch { it.id == dummyDevConnectorId(0, 1) }
        assertThat(result.connectors).allMatch { it.frontendUrl == null }

        assertThat(result.connectors[0].id).isEqualTo(dummyDevConnectorId(0, 2))
        assertThat(result.connectors[0].environment.environmentId).isEqualTo("other-environment")
    }

    @Test
    @TestTransaction
    fun `get provided connectors returns all connectors where user organizationId is host but not owner`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.SERVICE_PARTNER_ADMIN))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            connector(0, 0, 0)

            organization(1, 1)
            user(1, 1)

            connector(1, 1, 0) {
                it.providerOrganizationId = dummyDevOrganizationId(0)
            }
            connector(2, 1, 0) {
                it.providerOrganizationId = dummyDevOrganizationId(0)
            }

            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.getProvidedConnectors("test")

        // assert
        assertThat(result.connectors).hasSize(2)
        assertThat(result.connectors).noneMatch { it.environment.environmentId != "test" }
        assertThat(result.connectors).noneMatch { it.id == dummyDevConnectorId(0, 0) }

        assertThat(result.connectors[0].id).isEqualTo(dummyDevConnectorId(1, 1))
        assertThat(result.connectors[1].id).isEqualTo(dummyDevConnectorId(1, 2))
    }

    @Test
    @TestTransaction
    fun `get provided connector details with valid provided connectorId`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.SERVICE_PARTNER_ADMIN))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            connector(0, 0, 0)

            organization(1, 1)
            user(1, 1)

            connector(1, 1, 0) {
                it.providerOrganizationId = dummyDevOrganizationId(0)
                it.name = "Connector 1"
            }
            connector(2, 1, 0) {
                it.providerOrganizationId = dummyDevOrganizationId(0)
                it.name = "Connector 2"
            }

            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.getProvidedConnectorDetails(dummyDevConnectorId(1, 1))

        // assert
        assertThat(result.connectorId).isEqualTo(dummyDevConnectorId(1, 1))
        assertThat(result.connectorName).isEqualTo("Connector 1")
        assertThat(result.organizationId).isEqualTo(dummyDevOrganizationId(1))
        assertThat(result.hostOrganizationId).isEqualTo(dummyDevOrganizationId(0))
        assertThat(result.hostOrganizationName).isEqualTo("Organization 0")
    }

    @Test
    @TestTransaction
    fun `get provided connector details with connectorId not hosted by user org`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.SERVICE_PARTNER_ADMIN))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            connector(0, 0, 0)

            organization(1, 1)
            user(1, 1)

            connector(1, 1, 0) {
                it.providerOrganizationId = dummyDevOrganizationId(1)
                it.name = "Connector 1"
            }
            connector(2, 1, 0) {
                it.providerOrganizationId = dummyDevOrganizationId(1)
                it.name = "Connector 2"
            }

            scenarioInstaller.install(this)
        }

        // act & assert
        assertThatThrownBy {
            uiResource.getProvidedConnectorDetails(dummyDevConnectorId(1, 1))
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Connector ID does not match with organization or host organization")
    }

    @Test
    @TestTransaction
    fun `delete self-hosted connector`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_CURATOR))

        val dapsClient = mock<DapsClient>()
        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)
        doNothing().whenever(dapsClient).deleteClient(any())

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            connector(0, 0, 0)

            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.deleteOwnConnector(dummyDevConnectorId(0, 0))

        // assert
        val connectorCountQuery = dsl.selectCount()
            .from(Tables.CONNECTOR)
            .where(Tables.CONNECTOR.CONNECTOR_ID.eq(dummyDevConnectorId(0, 0)))
            .fetchOne(0, Int::class.java)

        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevConnectorId(0, 0))
        assertThat(connectorCountQuery).isEqualTo(0)
    }

    @Test
    @TestTransaction
    fun `delete provided connector also tests delete with constraint release`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.SERVICE_PARTNER_ADMIN))

        val dapsClient = mock<DapsClient>()
        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)
        doNothing().whenever(dapsClient).deleteClient(any())

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            organization(1, 1)
            user(1, 1)
            connector(0, 1, 0) {
                it.providerOrganizationId = dummyDevOrganizationId(0)
            }

            dataOffer(0, 1, 0, viewCount = 2)
            contractOffer(0, 1, 0, 0)
            crawlerLogEntry(0, 1)

            scenarioInstaller.install(this)
        }
        val connectorId = dummyDevConnectorId(1, 0)

        // act
        val result = uiResource.deleteProvidedConnector(connectorId)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(connectorId)

        assertThat(
            count(
                Tables.CONNECTOR,
                Tables.CONNECTOR.CONNECTOR_ID.eq(connectorId)
            )
        ).isEqualTo(0)

        assertThat(
            count(
                Tables.DATA_OFFER,
                Tables.DATA_OFFER.CONNECTOR_ID.eq(connectorId)
            )
        ).isEqualTo(0)

        assertThat(
            count(
                Tables.CONTRACT_OFFER,
                Tables.CONTRACT_OFFER.CONNECTOR_ID.eq(connectorId)
            )
        ).isEqualTo(0)

        assertThat(
            count(
                Tables.DATA_OFFER_VIEW_COUNT,
                Tables.DATA_OFFER_VIEW_COUNT.CONNECTOR_ID.eq(connectorId)
            )
        ).isEqualTo(0)

        // Ensure Crawler Event Log is untouched
        assertThat(
            count(
                Tables.CRAWLER_EVENT_LOG,
                Tables.CRAWLER_EVENT_LOG.CONNECTOR_ID.eq(connectorId)
            )
        ).isEqualTo(1)
    }

    @Test
    @TestTransaction
    fun `get own organization connectors does not show other connectors than own`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_USER))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            connector(0, 0, 0)
            connector(1, 0, 0)
            connector(2, 0, 0)

            organization(1, 1)
            user(1, 1)
            connector(3, 1, 0)

            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.ownOrganizationConnectors("test")

        // assert
        assertThat(result.connectors).hasSize(3)
        assertThat(result.connectors).noneMatch { it.id == dummyDevConnectorId(1, 3) }
        assertThat(result.connectors).noneMatch { it.environment.environmentId != "test" }

        result.connectors.forEachIndexed { i, connector ->
            assertThat(connector.id).isEqualTo(dummyDevConnectorId(0, i))
            assertThat(connector.organizationName).isEqualTo(dummyDevOrganizationName(0))
        }
    }

    @Test
    @TestTransaction
    fun `get own connector details fails for other organization connector`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_USER))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            connector(0, 0, 0)
            connector(1, 0, 0)
            connector(2, 0, 0)

            organization(1, 1)
            user(1, 1)
            connector(3, 1, 0)

            scenarioInstaller.install(this)
        }

        // act & assert
        assertThatThrownBy {
            uiResource.ownOrganizationConnectorDetails(dummyDevConnectorId(1, 3))
        }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Connector ID does not match with organization or host organization")
    }

    @Test
    @TestTransaction
    fun `create own connector creates a connector for user organization`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_CURATOR))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            scenarioInstaller.install(this)
        }

        val dapsClient = mock<DapsClient>()

        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)
        doNothing().whenever(dapsClient).createClient(any())
        doNothing().whenever(dapsClient).addCertificate(any(), any())
        doNothing().whenever(dapsClient).configureMappers(any())

        val request = CreateConnectorRequest(
            name = "Test Connector",
            location = "DE",
            frontendUrl = "https://connector.test.sovity.io/",
            endpointUrl = "https://connector.test.sovity.io/dsp/",
            managementUrl = "https://connector.test.sovity.io/api/management/",
            certificate = loadTestResource("create-connector-certificate.pem")
        )

        // act
        val result = uiResource.createOwnConnector("test", request)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).contains(dummyDevOrganizationId(0))
        assertThat(result.changedDate).isEqualTo(now)
        assertThat(result.status).isEqualTo(CreateConnectorStatusDto.OK)

        val actual = dsl.selectFrom(Tables.CONNECTOR)
            .where(Tables.CONNECTOR.CONNECTOR_ID.eq(result.id))
            .fetchOne()

        assertThat(actual).isNotNull

        val expected = dsl.newRecord(Tables.CONNECTOR).also {
            it.connectorId = actual!!.connectorId // id is generated, so we can only predict the first part of it
            it.organizationId = dummyDevOrganizationId(0)
            it.providerOrganizationId = it.organizationId
            it.type = ConnectorType.OWN
            it.environment = "test"
            it.clientId = clientIdUtils.generateFromConnectorId(actual.connectorId)
            it.name = "Test Connector"
            it.location = "DE"
            it.frontendUrl = "https://connector.test.sovity.io" // service should remove trailing slashes
            it.endpointUrl = "https://connector.test.sovity.io/dsp" // service should remove trailing slashes
            it.managementUrl =
                "https://connector.test.sovity.io/api/management" // service should remove trailing slashes
            it.createdBy = dummyDevUserUuid(0)
            it.createdAt = now
            it.jwksUrl = null
            it.caasStatus = null
            it.lastRefreshAttemptAt = null
            it.lastSuccessfulRefreshAt = null
            it.onlineStatus = ConnectorOnlineStatus.OFFLINE
            it.dataOffersExceeded = ConnectorDataOffersExceeded.OK
            it.contractOffersExceeded = ConnectorContractOffersExceeded.OK
        }

        assertThat(actual!!.copy())
            .usingRecursiveComparison()
            .withOffsetDateTimeComparator()
            .withStrictTypeChecking()
            .isEqualTo(expected.copy())
        assertThat(actual.connectorId).isEqualTo(actual.clientId)
    }

    @Test
    @TestTransaction
    fun `create own connector fails because of invalid urls`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_CURATOR))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            scenarioInstaller.install(this)
        }

        val request = CreateConnectorRequest(
            name = "Test Connector",
            location = "DE",
            frontendUrl = "http://connector.test.sovity.io/",
            endpointUrl = "http://connector.test.sovity.io/dsp/",
            managementUrl = "http://connector.test.sovity.io/api/management/",
            certificate = loadTestResource("create-connector-certificate.pem")
        )

        // act
        val result = uiResource.createOwnConnector("test", request)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isNull()
        assertThat(result.changedDate).isEqualTo(now)
        assertThat(result.status).isEqualTo(CreateConnectorStatusDto.ERROR)
        assertThat(result.message).isEqualTo("Connector URL is not valid.")
    }

    @Test
    @TestTransaction
    fun `configure provided connector creates a connector with certificate for target organization`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.SERVICE_PARTNER_ADMIN))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            organization(1, 1)
            user(1, 1)
            connector(0, 1, 0) {
                it.type = ConnectorType.CONFIGURING
                it.providerOrganizationId = dummyDevOrganizationId(0)
                it.onlineStatus = ConnectorOnlineStatus.OFFLINE
                it.lastRefreshAttemptAt = null
                it.lastSuccessfulRefreshAt = null
            }

            scenarioInstaller.install(this)
        }

        val dapsClient = mock<DapsClient>()

        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)
        doNothing().whenever(dapsClient).createClient(any())
        doNothing().whenever(dapsClient).addCertificate(any(), any())
        doNothing().whenever(dapsClient).configureMappers(any())

        val request = ConfigureProvidedConnectorWithCertificateRequest(
            frontendUrl = "https://connector.test.sovity.io/",
            endpointUrl = "https://connector.test.sovity.io/dsp/",
            managementUrl = "https://connector.test.sovity.io/api/management/",
            certificate = loadTestResource("create-connector-certificate.pem")
        )

        // act
        val result = uiResource.configureProvidedConnectorWithCertificate(
            dummyDevOrganizationId(1), dummyDevConnectorId(1, 0),
            "test", request
        )

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).contains(dummyDevOrganizationId(1))
        assertThat(result.changedDate).isEqualTo(now)
        assertThat(result.status).isEqualTo(CreateConnectorStatusDto.OK)

        val actual = dsl.selectFrom(Tables.CONNECTOR)
            .where(Tables.CONNECTOR.CONNECTOR_ID.eq(result.id))
            .fetchOne()

        assertThat(actual).isNotNull

        val expected = dsl.newRecord(Tables.CONNECTOR).also {
            it.connectorId = actual!!.connectorId // id is generated, so we can only predict the first part of it
            it.organizationId = dummyDevOrganizationId(1)
            it.providerOrganizationId = dummyDevOrganizationId(0)
            it.type = ConnectorType.PROVIDED
            it.environment = "test"
            it.clientId = "clientId"
            it.name = "Connector"
            it.location = "Location"
            it.frontendUrl = "https://connector.test.sovity.io" // service should remove trailing slashes
            it.endpointUrl = "https://connector.test.sovity.io/dsp" // service should remove trailing slashes
            it.managementUrl =
                "https://connector.test.sovity.io/api/management" // service should remove trailing slashes
            it.createdBy = dummyDevUserUuid(0)
            it.createdAt = actual.createdAt
            it.onlineStatus = ConnectorOnlineStatus.OFFLINE
            it.dataOffersExceeded = ConnectorDataOffersExceeded.OK
            it.contractOffersExceeded = ConnectorContractOffersExceeded.OK
        }

        assertThat(actual!!.copy())
            .usingRecursiveComparison()
            .withOffsetDateTimeComparator()
            .withStrictTypeChecking()
            .isEqualTo(expected.copy())
    }

    @Test
    @TestTransaction
    fun `configure provided connector creates a connector with jwks url for target organization`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.SERVICE_PARTNER_ADMIN))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            organization(1, 1)
            user(1, 1)
            connector(0, 1, 0) {
                it.type = ConnectorType.CONFIGURING
                it.providerOrganizationId = dummyDevOrganizationId(0)
                it.onlineStatus = ConnectorOnlineStatus.OFFLINE
                it.lastRefreshAttemptAt = null
                it.lastSuccessfulRefreshAt = null
            }

            scenarioInstaller.install(this)
        }

        val dapsClient = mock<DapsClient>()

        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)
        doNothing().whenever(dapsClient).createClient(any())
        doNothing().whenever(dapsClient).addJwksUrl(any(), any())
        doNothing().whenever(dapsClient).configureMappers(any())

        val request = ConfigureProvidedConnectorWithJwksRequest(
            frontendUrl = "https://connector.test.sovity.io/",
            endpointUrl = "https://connector.test.sovity.io/dsp/",
            managementUrl = "https://connector.test.sovity.io/api/management/",
            jwksUrl = "https://connector.test.sovity.io/api/v1/dsp/jwks",
        )

        // act
        val result = uiResource.configureProvidedConnectorWithJwks(dummyDevOrganizationId(1), dummyDevConnectorId(1, 0) , "test", request)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).contains(dummyDevOrganizationId(1))
        assertThat(result.changedDate).isEqualTo(now)
        assertThat(result.status).isEqualTo(CreateConnectorStatusDto.OK)

        val actual = dsl.selectFrom(Tables.CONNECTOR)
            .where(Tables.CONNECTOR.CONNECTOR_ID.eq(result.id))
            .fetchOne()

        assertThat(actual).isNotNull

        val expected = dsl.newRecord(Tables.CONNECTOR).also {
            it.connectorId = actual!!.connectorId // id is generated, so we can only predict the first part of it
            it.organizationId = dummyDevOrganizationId(1)
            it.providerOrganizationId = dummyDevOrganizationId(0)
            it.type = ConnectorType.PROVIDED
            it.environment = "test"
            it.clientId = "clientId"
            it.name = "Connector"
            it.location = "Location"
            it.frontendUrl = "https://connector.test.sovity.io" // service should remove trailing slashes
            it.endpointUrl = "https://connector.test.sovity.io/dsp" // service should remove trailing slashes
            it.managementUrl =
                "https://connector.test.sovity.io/api/management" // service should remove trailing slashes
            it.jwksUrl = "https://connector.test.sovity.io/api/v1/dsp/jwks"
            it.createdBy = dummyDevUserUuid(0)
            it.createdAt = actual.createdAt
            it.onlineStatus = ConnectorOnlineStatus.OFFLINE
            it.dataOffersExceeded = ConnectorDataOffersExceeded.OK
            it.contractOffersExceeded = ConnectorContractOffersExceeded.OK
        }

        assertThat(actual!!.copy())
            .usingRecursiveComparison()
            .withOffsetDateTimeComparator()
            .withStrictTypeChecking()
            .isEqualTo(expected.copy())
    }

    @Test
    @TestTransaction
    fun `configure provided connector fails because of invalid urls`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.SERVICE_PARTNER_ADMIN))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            organization(1, 1)
            user(1, 1)
            connector(0, 1, 0) {
                it.type = ConnectorType.CONFIGURING
                it.providerOrganizationId = dummyDevOrganizationId(0)
            }

            scenarioInstaller.install(this)
        }

        val request = ConfigureProvidedConnectorWithCertificateRequest(
            frontendUrl = "http://connector.test.sovity.io/",
            endpointUrl = "http://connector.test.sovity.io/dsp/",
            managementUrl = "http://connector.test.sovity.io/api/management/",
            certificate = loadTestResource("create-connector-certificate.pem")
        )

        // act
        val result = uiResource.configureProvidedConnectorWithCertificate(dummyDevOrganizationId(1), dummyDevConnectorId(1, 0), "test", request)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isNull()
        assertThat(result.changedDate).isEqualTo(now)
        assertThat(result.status).isEqualTo(CreateConnectorStatusDto.ERROR)
        assertThat(result.message).isEqualTo("Connector URL is not valid.")
    }

    @Test
    @TestTransaction
    fun `configure provided connector fails because of insufficient permissions`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_CURATOR))

        val request = ConfigureProvidedConnectorWithCertificateRequest(
            frontendUrl = "https://connector.test.sovity.io/",
            endpointUrl = "https://connector.test.sovity.io/dsp/",
            managementUrl = "https://connector.test.sovity.io/api/management/",
            certificate = loadTestResource("create-connector-certificate.pem")
        )

        // act & assert
        assertThatThrownBy {
            uiResource.configureProvidedConnectorWithCertificate(
                dummyDevOrganizationId(1),
                dummyDevConnectorId(1, 0),
                "test",
                request
            )
        }
            .isInstanceOf(NotAuthorizedException::class.java)
    }

    @Test
    @TestTransaction
    fun `delete own connector fails because of missing membership to owning organization`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_CURATOR))

        ScenarioData().apply {
            organization(0, 0)
            organization(1, 1)
            user(0, 0)
            user(1, 1)
            connector(0, 1, 1)

            scenarioInstaller.install(this)
        }

        // act && assert
        assertThatThrownBy { uiResource.deleteOwnConnector(dummyDevConnectorId(1, 0)) }
            .isInstanceOf(NotAuthorizedException::class.java)
            .hasMessageContaining("User is not a member of the owning organization")
    }

    @Test
    @TestTransaction
    fun `delete provided connector fails because of missing membership to provider organization`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.SERVICE_PARTNER_ADMIN))

        ScenarioData().apply {
            organization(0, 0)
            organization(1, 1)
            organization(2, 2)
            user(0, 0)
            user(1, 1)
            user(2, 2)
            connector(0, 1, 2) {
                it.providerOrganizationId = dummyDevOrganizationId(2)
            }

            scenarioInstaller.install(this)
        }

        // act && assert
        assertThatThrownBy { uiResource.deleteProvidedConnector(dummyDevConnectorId(1, 0)) }
            .isInstanceOf(NotAuthorizedException::class.java)
            .hasMessageContaining("User is not a member of the provider organization")
    }

    @Test
    @TestTransaction
    fun `delete provided connector as service partner admin`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.SERVICE_PARTNER_ADMIN))

        val dapsClient = mock<DapsClient>()
        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)
        doNothing().whenever(dapsClient).deleteClient(any())

        ScenarioData().apply {
            organization(0, 0)
            organization(1, 1)
            organization(2, 2)
            user(0, 0)
            user(1, 1)
            user(2, 2)
            connector(0, 1, 0) {
                it.providerOrganizationId = dummyDevOrganizationId(0)
            }

            scenarioInstaller.install(this)
        }
        val connectorId = dummyDevConnectorId(1, 0)

        // act
        val result = uiResource.deleteProvidedConnector(connectorId)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(connectorId)

        assertThat(
            count(
                Tables.CONNECTOR,
                Tables.CONNECTOR.CONNECTOR_ID.eq(connectorId)
            )
        ).isEqualTo(0)
    }

    @Test
    @TestTransaction
    fun `delete provided connector as operator admin`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.OPERATOR_ADMIN))

        val dapsClient = mock<DapsClient>()
        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)
        doNothing().whenever(dapsClient).deleteClient(any())

        ScenarioData().apply {
            organization(0, 0)
            organization(1, 1)
            organization(2, 2)
            user(0, 0)
            user(1, 1)
            user(2, 2)
            connector(0, 1, 2) {
                it.providerOrganizationId = dummyDevOrganizationId(2)
            }

            scenarioInstaller.install(this)
        }
        val connectorId = dummyDevConnectorId(1, 0)

        // act
        val result = uiResource.deleteProvidedConnector(connectorId)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(connectorId)

        assertThat(
            count(
                Tables.CONNECTOR,
                Tables.CONNECTOR.CONNECTOR_ID.eq(connectorId)
            )
        ).isEqualTo(0)
    }

    private fun count(table: TableLike<*>, condition: Condition): Int {
        return dsl.selectCount()
            .from(table)
            .where(condition)
            .fetchOne(0, Int::class.java) ?: 0
    }
}
