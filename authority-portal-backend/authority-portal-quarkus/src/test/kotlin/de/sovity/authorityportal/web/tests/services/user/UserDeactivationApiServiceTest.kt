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

package de.sovity.authorityportal.web.tests.services.user

import de.sovity.authorityportal.api.UiResource
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.seeds.utils.dummyDevUserUuid
import de.sovity.authorityportal.web.Roles
import de.sovity.authorityportal.web.tests.useDevUser
import de.sovity.authorityportal.web.tests.useMockNow
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import io.quarkus.test.InjectMock
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.NotAuthorizedException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class UserDeactivationApiServiceTest {

    @Inject
    lateinit var uiResource: UiResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Inject
    lateinit var dsl: DSLContext

    @InjectMock
    lateinit var keycloakService: KeycloakService

    @Test
    @TestTransaction
    fun `deactivate participant user with a valid request`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))
        useMockNow(now)

        doNothing().whenever(keycloakService).deactivateUser(any())

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            user(1, 0) {
                it.registrationStatus = UserRegistrationStatus.ACTIVE
            }
            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.deactivateParticipantUser(dummyDevUserUuid(1))

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevUserUuid(1))

        val actual = dsl.selectFrom(Tables.USER).where(Tables.USER.ID.eq(dummyDevUserUuid(1))).fetchOne()
        assertThat(actual).isNotNull()
        assertThat(actual!!.registrationStatus).isEqualTo(UserRegistrationStatus.DEACTIVATED)
    }

    @Test
    @TestTransaction
    fun `deactivate participant user fails because target registration status not active`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            user(1, 0) {
                it.registrationStatus = UserRegistrationStatus.PENDING
            }
            scenarioInstaller.install(this)
        }

        // act
        assertThatThrownBy {
            uiResource.deactivateParticipantUser(dummyDevUserUuid(1))
        }.isInstanceOf(NotAuthorizedException::class.java)

        // assert
        val actual = dsl.selectFrom(Tables.USER).where(Tables.USER.ID.eq(dummyDevUserUuid(1))).fetchOne()
        assertThat(actual).isNotNull()
        assertThat(actual!!.registrationStatus).isEqualTo(UserRegistrationStatus.PENDING)
    }

    @Test
    @TestTransaction
    fun `reactivate participant user with a valid request`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))
        useMockNow(now)

        doNothing().whenever(keycloakService).deactivateUser(any())

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            user(1, 0) {
                it.registrationStatus = UserRegistrationStatus.DEACTIVATED
            }
            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.reactivateParticipantUser(dummyDevUserUuid(1))

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevUserUuid(1))

        val actual = dsl.selectFrom(Tables.USER).where(Tables.USER.ID.eq(dummyDevUserUuid(1))).fetchOne()
        assertThat(actual).isNotNull()
        assertThat(actual!!.registrationStatus).isEqualTo(UserRegistrationStatus.ACTIVE)
    }

    @Test
    @TestTransaction
    fun `reactivate participant user fails because target registration status not deactivated`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            user(1, 0) {
                it.registrationStatus = UserRegistrationStatus.PENDING
            }
            scenarioInstaller.install(this)
        }

        // act
        assertThatThrownBy {
            uiResource.reactivateParticipantUser(dummyDevUserUuid(1))
        }.isInstanceOf(NotAuthorizedException::class.java)

        // assert
        val actual = dsl.selectFrom(Tables.USER).where(Tables.USER.ID.eq(dummyDevUserUuid(1))).fetchOne()
        assertThat(actual).isNotNull()
        assertThat(actual!!.registrationStatus).isEqualTo(UserRegistrationStatus.PENDING)
    }
}
