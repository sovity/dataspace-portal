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
import de.sovity.authorityportal.api.model.CreateCaasRequest
import de.sovity.authorityportal.api.model.CreateConnectorStatusDto
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.CaasStatus
import de.sovity.authorityportal.db.jooq.enums.ConnectorContractOffersExceeded
import de.sovity.authorityportal.db.jooq.enums.ConnectorDataOffersExceeded
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus
import de.sovity.authorityportal.db.jooq.enums.ConnectorType
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.seeds.utils.dummyDevConnectorId
import de.sovity.authorityportal.seeds.utils.dummyDevOrganizationId
import de.sovity.authorityportal.seeds.utils.dummyDevUserUuid
import de.sovity.authorityportal.web.Roles
import de.sovity.authorityportal.web.tests.useDevUser
import de.sovity.authorityportal.web.tests.useMockNow
import de.sovity.authorityportal.web.tests.withOffsetDateTimeComparator
import de.sovity.authorityportal.web.thirdparty.caas.CaasClient
import de.sovity.authorityportal.web.thirdparty.caas.model.CaasDetails
import de.sovity.authorityportal.web.thirdparty.caas.model.CaasPortalResponse
import de.sovity.authorityportal.web.utils.idmanagement.ClientIdUtils
import io.quarkus.test.InjectMock
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class CaasManagementApiServiceTest {

    @Inject
    lateinit var uiResource: UiResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Inject
    lateinit var dsl: DSLContext

    @Inject
    lateinit var clientIdUtils: ClientIdUtils

    @Inject
    lateinit var executorService: ExecutorService

    @InjectMock
    lateinit var caasClient: CaasClient

    @AfterEach
    fun cleanup() {
        ScenarioData.uninstall(dsl)
    }

    @Test
    fun `create caas creates connector with caas configuration`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_CURATOR))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            scenarioInstaller.install(this)
        }

        whenever(caasClient.validateSubdomain(eq("test-caas-1"))).thenReturn(true)
        whenever(caasClient.requestCaas(any())).thenReturn(
            CaasPortalResponse().apply {
                value = CaasDetails(connectorId = dummyDevConnectorId(0, 0))
            }
        )

        val request = CreateCaasRequest(
            connectorSubdomain = "test-caas-1",
            connectorTitle = "Test CaaS",
            connectorDescription = "Connector-as-a-service for testing purposes"
        )

        // act
        val result = uiResource.createCaas("test", request)

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
            it.connectorId = actual!!.connectorId
            it.organizationId = dummyDevOrganizationId(0)
            it.providerOrganizationId = null
            it.type = ConnectorType.CAAS
            it.environment = "test"
            it.clientId = clientIdUtils.generateFromConnectorId(it.connectorId)
            it.name = "Test CaaS"
            it.location = "CaaS"
            it.frontendUrl = null
            it.endpointUrl = null
            it.managementUrl = null
            it.createdBy = dummyDevUserUuid(0)
            it.createdAt = now
            it.jwksUrl = null
            it.caasStatus = CaasStatus.PROVISIONING
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
    fun `check free caas slots returns the correct amount`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_CURATOR))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            connector(0, 0, 0) { it.type = ConnectorType.CAAS }
            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.checkFreeCaasUsage("test")

        // assert
        assertThat(result).isNotNull

        // This will always return 0/0 because the function checks if the OIDC Client is enabled.
        // Unfortunately, the OIDC Client is not enabled in the test environment because Quarkus would refuse to start.
        assertThat(result.limit).isEqualTo(0)
        assertThat(result.current).isEqualTo(0)
    }

    @Test
    @TestSecurity(authorizationEnabled = false)
    fun `create caas endpoint should lock correctly`() {
        // arrange
        val now = OffsetDateTime.now()
        val i = AtomicInteger(0)

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_CURATOR))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            scenarioInstaller.install(this)
        }

        whenever(caasClient.validateSubdomain(any())).thenReturn(true)
        whenever(caasClient.requestCaas(any())).thenReturn(
            CaasPortalResponse().apply {
                value = CaasDetails(connectorId = dummyDevConnectorId(0, 0))
            }
        )

        // act
        val count = 10
        val latch = CountDownLatch(count)

        repeat(count) {
            executorService.execute {
                uiResource.createCaas(
                    "test", CreateCaasRequest(
                        connectorSubdomain = "test-caas-${i.addAndGet(1)}",
                        connectorTitle = "Test CaaS-${i.addAndGet(1)}",
                        connectorDescription = "Connector-as-a-service for testing purposes"
                    )
                )
                latch.countDown()
            }
        }
        latch.await(20, TimeUnit.SECONDS)

        // assert
        val connectors = dsl.selectFrom(Tables.CONNECTOR)
            .where(Tables.CONNECTOR.TYPE.eq(ConnectorType.CAAS))
            .fetch()

        assertThat(connectors).hasSize(1)
    }
}
