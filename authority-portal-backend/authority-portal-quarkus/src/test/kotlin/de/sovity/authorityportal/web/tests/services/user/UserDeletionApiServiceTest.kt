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
import de.sovity.authorityportal.api.model.PossibleCreatorSuccessor
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.seeds.utils.dummyDevOrganizationId
import de.sovity.authorityportal.seeds.utils.dummyDevUserUuid
import de.sovity.authorityportal.web.Roles
import de.sovity.authorityportal.web.tests.useDevUser
import de.sovity.authorityportal.web.tests.useMockNow
import de.sovity.authorityportal.web.thirdparty.daps.DapsClient
import de.sovity.authorityportal.web.thirdparty.daps.DapsClientService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.thirdparty.keycloak.model.KeycloakUserDto
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class UserDeletionApiServiceTest {

    @Inject
    lateinit var uiResource: UiResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Inject
    lateinit var dsl: DSLContext

    @InjectMock
    lateinit var keycloakService: KeycloakService

    @InjectMock
    lateinit var dapsClientService: DapsClientService

    @Test
    fun `checkUserDeletion fails because user is not admin and target is not self`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_USER))

        // act & assert
        assertThatThrownBy {
            uiResource.checkUserDeletion(dummyDevUserUuid(1))
        }.isInstanceOf(NotAuthorizedException::class.java)
    }

    @Test
    @TestTransaction
    fun `checkUserDeletion fails because user is not authority and target is in different organization`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))

        ScenarioData().apply {
            organization(0, 0)
            organization(1, 1)
            user(0, 0)
            user(1, 1)
            scenarioInstaller.install(this)
        }

        // act & assert
        assertThatThrownBy {
            uiResource.checkUserDeletion(dummyDevUserUuid(1))
        }.isInstanceOf(NotAuthorizedException::class.java)
    }

    @Test
    @TestTransaction
    fun `checkUserDeletion as last participant admin and organization creator`() {
        // arrange
        val lastParticipantAdmin = KeycloakUserDto(dummyDevUserUuid(0), "Test", "User", "test.user@test.sovity.io")
        val lastAuthorityAdmin = KeycloakUserDto(dummyDevUserUuid(99), "Authority", "Admin", "authority@test.sovity.io")

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))

        whenever(keycloakService.getAuthorityAdmins()).thenReturn(listOf(lastAuthorityAdmin))
        whenever(keycloakService.getParticipantAdmins(eq(dummyDevOrganizationId(0)))).thenReturn(listOf(lastParticipantAdmin))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            user(1, 0)
            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.checkUserDeletion(dummyDevUserUuid(0))

        // assert
        assertThat(result).isNotNull
        assertThat(result.userId).isEqualTo(dummyDevUserUuid(0))
        assertThat(result.canBeDeleted).isTrue()
        assertThat(result.isLastParticipantAdmin).isTrue()
        assertThat(result.isOrganizationCreator).isTrue()
        assertThat(result.possibleCreatorSuccessors.size).isEqualTo(0)
    }

    @Test
    @TestTransaction
    fun `checkUserDeletion as organization creator with possible successor`() {
        // arrange
        val keycloakUser = KeycloakUserDto(dummyDevUserUuid(0), "Test", "User", "test.user@test.sovity.io")
        val possibleSuccessor = KeycloakUserDto(dummyDevUserUuid(1), "Max", "Mustermann", "max.mustermann@test.sovity.io")
        val lastAuthorityAdmin = KeycloakUserDto(dummyDevUserUuid(99), "Authority", "Admin", "authority@test.sovity.io")

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))

        whenever(keycloakService.getAuthorityAdmins()).thenReturn(listOf(lastAuthorityAdmin))
        whenever(keycloakService.getParticipantAdmins(eq(dummyDevOrganizationId(0)))).thenReturn(listOf(keycloakUser, possibleSuccessor))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            user(1, 0) {
                it.firstName = "Max"
                it.lastName = "Mustermann"
                it.email = "max.mustermann@test.sovity.io"
            }
            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.checkUserDeletion(dummyDevUserUuid(0))

        // assert
        assertThat(result).isNotNull
        assertThat(result.userId).isEqualTo(dummyDevUserUuid(0))
        assertThat(result.canBeDeleted).isTrue()
        assertThat(result.isLastParticipantAdmin).isFalse()
        assertThat(result.isOrganizationCreator).isTrue()
        assertThat(result.possibleCreatorSuccessors.size).isEqualTo(1)

        val expectedSuccessor = PossibleCreatorSuccessor(
            userId = dummyDevUserUuid(1),
            firstName = "Max",
            lastName = "Mustermann"
        )

        assertThat(result.possibleCreatorSuccessors).containsExactly(expectedSuccessor)
    }

    @Test
    @TestTransaction
    fun `deleteUser with target self and valid successor`() {
        // arrange
        val now = OffsetDateTime.now()

        val keycloakUser = KeycloakUserDto(dummyDevUserUuid(0), "Test", "User", "test.user@test.sovity.io")
        val possibleSuccessor = KeycloakUserDto(dummyDevUserUuid(1), "Max", "Mustermann", "max.mustermann@test.sovity.io")
        val lastAuthorityAdmin = KeycloakUserDto(dummyDevUserUuid(99), "Authority", "Admin", "authority@test.sovity.io")

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))
        useMockNow(now)

        whenever(keycloakService.getAuthorityAdmins()).thenReturn(listOf(lastAuthorityAdmin))
        whenever(keycloakService.getParticipantAdmins(eq(dummyDevOrganizationId(0)))).thenReturn(listOf(keycloakUser, possibleSuccessor))
        doNothing().whenever(keycloakService).deleteUserSafely(eq(dummyDevUserUuid(0)))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            user(1, 0) {
                it.firstName = "Max"
                it.lastName = "Mustermann"
                it.email = "max.mustermann@test.sovity.io"
            }
            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.deleteUser(userId = dummyDevUserUuid(0), successorUserId = dummyDevUserUuid(1))

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevUserUuid(0))

        val dbQueryResult = dsl.selectCount()
            .from(Tables.USER)
            .where(Tables.USER.ID.eq(dummyDevUserUuid(0)))
            .fetchOne(0, Int::class.java)

        assertThat(dbQueryResult).isEqualTo(0)
    }

    @Test
    @TestTransaction
    fun `deleteUser with target self as last participant admin`() {
        // arrange
        val now = OffsetDateTime.now()

        val keycloakUser = KeycloakUserDto(dummyDevUserUuid(0), "Test", "User", "test.user@test.sovity.io")
        val lastAuthorityAdmin = KeycloakUserDto(dummyDevUserUuid(99), "Authority", "Admin", "authority@test.sovity.io")

        val dapsClient = mock<DapsClient>()

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))
        useMockNow(now)

        whenever(keycloakService.getAuthorityAdmins()).thenReturn(listOf(lastAuthorityAdmin))
        whenever(keycloakService.getParticipantAdmins(eq(dummyDevOrganizationId(0)))).thenReturn(listOf(keycloakUser))
        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)
        doNothing().whenever(keycloakService).deleteUserSafely(eq(dummyDevUserUuid(0)))
        doNothing().whenever(keycloakService).deleteOrganization(eq(dummyDevOrganizationId(0)))
        doNothing().whenever(dapsClient).deleteClient(any())

        ScenarioData().apply {
            organization(0, 0)
            organization(1, 99)
            user(0, 0)
            user(1, 0)
            user(99, 1)
            connector(0, 0, 0)
            component(0, 0, 0)
            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.deleteUser(userId = dummyDevUserUuid(0), successorUserId = null)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevUserUuid(0))

        val userDbQueryResult = dsl.selectCount()
            .from(Tables.USER)
            .where(Tables.USER.ID.eq(dummyDevUserUuid(0)))
            .or(Tables.USER.ID.eq(dummyDevUserUuid(1)))
            .fetchOne(0, Int::class.java)

        assertThat(userDbQueryResult).isEqualTo(0)

        val connectorDbQueryResult = dsl.selectCount()
            .from(Tables.CONNECTOR)
            .where(Tables.CONNECTOR.ORGANIZATION_ID.eq(dummyDevOrganizationId(0)))
            .fetchOne(0, Int::class.java)

        assertThat(connectorDbQueryResult).isEqualTo(0)

        val componentDbQueryResult = dsl.selectCount()
            .from(Tables.COMPONENT)
            .where(Tables.COMPONENT.ORGANIZATION_ID.eq(dummyDevOrganizationId(0)))
            .fetchOne(0, Int::class.java)

        assertThat(componentDbQueryResult).isEqualTo(0)
    }
}
