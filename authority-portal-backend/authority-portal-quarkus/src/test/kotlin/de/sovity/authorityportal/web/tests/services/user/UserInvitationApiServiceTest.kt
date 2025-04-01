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
import de.sovity.authorityportal.api.model.InviteParticipantUserRequest
import de.sovity.authorityportal.api.model.UserRoleDto
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.UserOnboardingType
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.seeds.utils.dummyDevOrganizationId
import de.sovity.authorityportal.seeds.utils.dummyDevUserUuid
import de.sovity.authorityportal.web.Roles
import de.sovity.authorityportal.web.tests.useDevUser
import de.sovity.authorityportal.web.tests.useMockNow
import de.sovity.authorityportal.web.tests.withOffsetDateTimeComparator
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import io.quarkus.test.InjectMock
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class UserInvitationApiServiceTest {

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
    fun `invite user with a valid request`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))
        useMockNow(now)

        whenever(keycloakService.createUser(
            eq("max.mustermann@test.sovity.io"),
            eq("Max"),
            eq("Mustermann"),
            isNull()
        )).thenReturn(dummyDevUserUuid(1))

        doNothing().whenever(keycloakService).joinOrganization(any(), any(), any())
        doNothing().whenever(keycloakService).sendInvitationEmail(any())

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            scenarioInstaller.install(this)
        }

        val payload = InviteParticipantUserRequest(
            email = "max.mustermann@test.sovity.io",
            firstName = "Max",
            lastName = "Mustermann",
            role = UserRoleDto.USER
        )

        // act
        val result = uiResource.inviteUser(payload)

        // assert
        val expected = dsl.newRecord(Tables.USER).also {
            it.id = dummyDevUserUuid(1)
            it.firstName = "Max"
            it.lastName = "Mustermann"
            it.email = "max.mustermann@test.sovity.io"
            it.organizationId = dummyDevOrganizationId(0)
            it.registrationStatus = UserRegistrationStatus.INVITED
            it.createdAt = now
            it.onboardingType = UserOnboardingType.INVITATION
            it.invitedBy = dummyDevUserUuid(0)
        }

        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevUserUuid(1))

        val actual = dsl.selectFrom(Tables.USER).where(Tables.USER.ID.eq(dummyDevUserUuid(1))).fetchOne()
        assertThat(actual).isNotNull()

        assertThat(actual!!.copy())
            .usingRecursiveComparison()
            .withOffsetDateTimeComparator()
            .isEqualTo(expected.copy())
    }
}
