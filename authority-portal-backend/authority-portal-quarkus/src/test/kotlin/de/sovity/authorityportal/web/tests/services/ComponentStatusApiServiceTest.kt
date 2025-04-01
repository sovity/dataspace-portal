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

package de.sovity.authorityportal.web.tests.services

import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.ComponentOnlineStatus
import de.sovity.authorityportal.db.jooq.enums.ComponentType
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.web.pages.ComponentStatusApiService
import de.sovity.authorityportal.web.tests.useMockNow
import de.sovity.authorityportal.web.thirdparty.uptimekuma.model.toDto
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import java.time.OffsetDateTime

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class ComponentStatusApiServiceTest {

    @Inject
    lateinit var dsl: DSLContext

    @Inject
    lateinit var componentStatusApiService: ComponentStatusApiService

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Test
    @TestTransaction
    fun getComponentStatusTest() {
        // arrange
        val now = OffsetDateTime.now()
        val env1 = "test"
        val env2 = "dev"
        val env3 = "env3"
        setupStatusHistory(now, env1, env2, env3)

        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            // Online connectors
            connector(0, 0, 0) {it.onlineStatus = ConnectorOnlineStatus.ONLINE}
            connector(1, 0, 0) {it.onlineStatus = ConnectorOnlineStatus.ONLINE}

            // Disturbed connectors
            connector(2, 0, 0) {
                it.onlineStatus = ConnectorOnlineStatus.OFFLINE
                it.lastSuccessfulRefreshAt = now.minus(Duration.ofMinutes(1))
            }
            connector(3, 0, 0) {
                it.onlineStatus = ConnectorOnlineStatus.OFFLINE
                it.lastSuccessfulRefreshAt = now.minus(Duration.ofMinutes(1))
            }
            connector(4, 0, 0) {
                it.onlineStatus = ConnectorOnlineStatus.OFFLINE
                it.lastSuccessfulRefreshAt = now.minus(Duration.ofMinutes(1))
            }

            // Offline connectors
            connector(5, 0, 0) {
                it.onlineStatus = ConnectorOnlineStatus.OFFLINE
                it.lastSuccessfulRefreshAt = now.minus(Duration.ofMinutes(3))
            }
            connector(6, 0, 0) {
                it.onlineStatus = ConnectorOnlineStatus.OFFLINE
                it.lastSuccessfulRefreshAt = now.minus(Duration.ofMinutes(3))
            }

            scenarioInstaller.install(this)
        }

        // act
        val resultEnv1 = componentStatusApiService.getComponentsStatus(env1)
        val resultEnv2 = componentStatusApiService.getComponentsStatus(env2)
        val resultEnv3 = componentStatusApiService.getComponentsStatus(env3)

        // assert
        assertThat(resultEnv1.dapsStatus?.componentStatus).isEqualTo(ComponentOnlineStatus.MAINTENANCE.toDto())
        assertThat(resultEnv1.dapsStatus?.uptimePercentage).isCloseTo(50.00, Offset.offset(0.1))
        assertThat(resultEnv1.dapsStatus?.timeSpanSeconds).isEqualTo(Duration.ofDays(30).toSeconds())
        assertThat(resultEnv1.dapsStatus?.upSinceSeconds).isEqualTo(Duration.ZERO.toSeconds())
        assertThat(resultEnv1.loggingHouseStatus?.componentStatus).isEqualTo(ComponentOnlineStatus.PENDING.toDto())
        assertThat(resultEnv1.loggingHouseStatus?.uptimePercentage).isEqualTo(0.00)
        assertThat(resultEnv1.loggingHouseStatus?.timeSpanSeconds).isEqualTo(Duration.ofDays(30).toSeconds())
        assertThat(resultEnv1.loggingHouseStatus?.upSinceSeconds).isEqualTo(Duration.ZERO.toSeconds())
        assertThat(resultEnv1.onlineConnectors).isEqualTo(2)
        assertThat(resultEnv1.disturbedConnectors).isEqualTo(3)
        assertThat(resultEnv1.offlineConnectors).isEqualTo(2)

        assertThat(resultEnv2.dapsStatus?.componentStatus).isEqualTo(ComponentOnlineStatus.DOWN.toDto())
        assertThat(resultEnv2.dapsStatus?.uptimePercentage).isEqualTo(0.00)
        assertThat(resultEnv2.dapsStatus?.timeSpanSeconds).isEqualTo(Duration.ofDays(30).toSeconds())
        assertThat(resultEnv2.dapsStatus?.upSinceSeconds).isEqualTo(Duration.ZERO.toSeconds())
        assertThat(resultEnv2.loggingHouseStatus).isNull()

        assertThat(resultEnv3.loggingHouseStatus?.componentStatus).isEqualTo(ComponentOnlineStatus.DOWN.toDto())
        assertThat(resultEnv3.loggingHouseStatus?.uptimePercentage).isCloseTo(50.0, Offset.offset(0.1))
        assertThat(resultEnv3.loggingHouseStatus?.timeSpanSeconds).isEqualTo(Duration.ofDays(30).toSeconds())
        assertThat(resultEnv3.loggingHouseStatus?.upSinceSeconds).isEqualTo(Duration.ZERO.toSeconds())

        assertThat(resultEnv3.dapsStatus?.componentStatus).isEqualTo(ComponentOnlineStatus.UP.toDto())
        assertThat(resultEnv3.dapsStatus?.uptimePercentage).isCloseTo(100.0, Offset.offset(0.1))
        assertThat(resultEnv3.dapsStatus?.timeSpanSeconds).isEqualTo(Duration.ofDays(30).toSeconds())
        assertThat(resultEnv3.dapsStatus?.upSinceSeconds).isCloseTo(Duration.ofDays(15).toSeconds(), Offset.offset(1L))
    }

    private fun setupStatusHistory(now: OffsetDateTime, environment1: String, environment2: String, environment3: String) {
        val c = Tables.COMPONENT_DOWNTIMES

        dsl.insertInto(c)
            .columns(c.COMPONENT, c.STATUS, c.ENVIRONMENT, c.TIME_STAMP)
            // Environment 1
            // DAPS: First status is "younger" that 30 days (UP: ~50%)
            .values(ComponentType.DAPS, ComponentOnlineStatus.DOWN, environment1, now.minus(Duration.ofDays(16)))
            .values(ComponentType.DAPS, ComponentOnlineStatus.UP, environment1, now.minus(Duration.ofDays(12)))
            .values(ComponentType.DAPS, ComponentOnlineStatus.PENDING, environment1, now.minus(Duration.ofDays(8)))
            .values(ComponentType.DAPS, ComponentOnlineStatus.UP, environment1, now.minus(Duration.ofDays(4)))
            .values(ComponentType.DAPS, ComponentOnlineStatus.MAINTENANCE, environment1, now.minus(Duration.ofMinutes(2)))
            // LH: Never been UP (UP: 0%)
            .values(ComponentType.LOGGING_HOUSE, ComponentOnlineStatus.DOWN, environment1, now.minus(Duration.ofDays(31)))
            .values(ComponentType.LOGGING_HOUSE, ComponentOnlineStatus.MAINTENANCE, environment1, now.minus(Duration.ofDays(20)))
            .values(ComponentType.LOGGING_HOUSE, ComponentOnlineStatus.PENDING, environment1, now.minus(Duration.ofMinutes(5)))
            // Environment 2
            // DAPS: Only "DOWN" status, older than 30 days
            .values(ComponentType.DAPS, ComponentOnlineStatus.DOWN, environment2, now.minus(Duration.ofDays(31)))
            // LH: Empty
            // Environment 3
            // LH: Up
            .values(ComponentType.LOGGING_HOUSE, ComponentOnlineStatus.UP, environment3, now.minus(Duration.ofDays(10)))
            .values(ComponentType.LOGGING_HOUSE, ComponentOnlineStatus.PENDING, environment3, now.minus(Duration.ofDays(5)))
            .values(ComponentType.LOGGING_HOUSE, ComponentOnlineStatus.DOWN, environment3, now.minus(Duration.ofSeconds(1)))
            // DAPS: Only "UP" status
            .values(ComponentType.DAPS, ComponentOnlineStatus.UP, environment3, now.minus(Duration.ofDays(15)))
            .execute()
    }
}
