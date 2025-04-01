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

package de.sovity.authorityportal.web.tests.services.organization

import de.sovity.authorityportal.api.UiResource
import de.sovity.authorityportal.api.model.InviteOrganizationRequest
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
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
import de.sovity.authorityportal.web.utils.idmanagement.OrganizationIdUtils
import io.quarkus.test.InjectMock
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
class OrganizationInvitationApiServiceTest {

    @Inject
    lateinit var uiResource: UiResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Inject
    lateinit var dsl: DSLContext

    @InjectMock
    lateinit var keycloakService: KeycloakService

    @InjectMock
    lateinit var organizationIdUtils: OrganizationIdUtils

    @Test
    @TestTransaction
    fun `invite organization fails because of invalid request body`() {
        // arrange
        useDevUser(0,0)

        val request = InviteOrganizationRequest(
            userEmail = "",
            userFirstName = "New",
            userLastName = "User",
            orgName = "New Organization",
            userJobTitle = null,
            userPhoneNumber = null
        )

        // act
        assertThatThrownBy {
            uiResource.inviteOrganization(request)
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

    @Test
    @TestTransaction
    fun `invite organization creates a new organization and user`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0,0, setOf(Roles.UserRoles.AUTHORITY_USER))
        useMockNow(now)

        whenever(organizationIdUtils.generateOrganizationId()).thenReturn(dummyDevOrganizationId(1))
        whenever(keycloakService.createKeycloakUserAndOrganization(
            any(), eq("new.user@test.sovity.io"), eq("New"), eq("User"), any(), eq(null)
        )).thenReturn(dummyDevUserUuid(1))

        ScenarioData().apply {
            user(0, 0)
            organization(0, 0)

            scenarioInstaller.install(this)
        }

        val request = InviteOrganizationRequest(
            userEmail = "new.user@test.sovity.io",
            userFirstName = "New",
            userLastName = "User",
            orgName = "New Organization",
            userJobTitle = null,
            userPhoneNumber = null,
        )

        // act
        val result = uiResource.inviteOrganization(request)

        // assert
        assertThat(result).isNotNull

        val expectedOrganization = dsl.newRecord(Tables.ORGANIZATION).also {
            it.id = dummyDevOrganizationId(1)
            it.name = "New Organization"
            it.address = null
            it.url = null
            it.createdBy = dummyDevUserUuid(1)
            it.registrationStatus = OrganizationRegistrationStatus.INVITED
            it.businessUnit = null
            it.billingAddress = null
            it.taxId = null
            it.commerceRegisterNumber = null
            it.commerceRegisterLocation = null
            it.mainContactName = null
            it.mainContactEmail = null
            it.mainContactPhone = null
            it.techContactName = null
            it.techContactEmail = null
            it.techContactPhone = null
            it.legalIdType = null
            it.description = null
            it.industry = null
            it.createdAt = now
        }

        val actualOrganization = dsl.selectFrom(Tables.ORGANIZATION).where(Tables.ORGANIZATION.ID.eq(dummyDevOrganizationId(1))).fetchOne()

        val expectedUser = dsl.newRecord(Tables.USER).also {
            it.id = dummyDevUserUuid(1)
            it.organizationId = dummyDevOrganizationId(1)
            it.registrationStatus = UserRegistrationStatus.INVITED
            it.email = "new.user@test.sovity.io"
            it.firstName = "New"
            it.lastName = "User"
            it.jobTitle = null
            it.phone = null
            it.onboardingType = UserOnboardingType.INVITATION
            it.invitedBy = null
            it.createdAt = now
        }

        val actualUser = dsl.selectFrom(Tables.USER).where(Tables.USER.ID.eq(dummyDevUserUuid(1))).fetchOne()

        assertThat(actualOrganization!!.copy())
            .usingRecursiveComparison()
            .withOffsetDateTimeComparator()
            .withStrictTypeChecking()
            .isEqualTo(expectedOrganization.copy())

        assertThat(actualUser!!.copy())
            .usingRecursiveComparison()
            .withOffsetDateTimeComparator()
            .withStrictTypeChecking()
            .isEqualTo(expectedUser.copy())
    }
}
