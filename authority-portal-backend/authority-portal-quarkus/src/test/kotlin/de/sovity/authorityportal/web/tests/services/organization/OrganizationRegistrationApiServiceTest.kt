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

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class OrganizationRegistrationApiServiceTest {

    @Inject
    lateinit var uiResource: UiResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Inject
    lateinit var dsl: DSLContext

    @Test
    @TestTransaction
    fun `approve organization changes organization onboarding status`() {
        // assert
        useDevUser(0, 0, setOf(Roles.UserRoles.AUTHORITY_USER))

        ScenarioData().apply {
            organization(1, 1) {
                it.registrationStatus = OrganizationRegistrationStatus.PENDING
            }
            user(1, 1) {
                it.registrationStatus = UserRegistrationStatus.PENDING
                it.onboardingType = UserOnboardingType.SELF_REGISTRATION
            }
            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.approveOrganization(dummyDevOrganizationId(1))

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevOrganizationId(1))

        val organizationRecord = dsl.selectFrom(Tables.ORGANIZATION)
            .where(Tables.ORGANIZATION.ID.eq(dummyDevOrganizationId(1)))
            .fetchOne()

        assertThat(organizationRecord).isNotNull
        assertThat(organizationRecord!!.id).isEqualTo(dummyDevOrganizationId(1))
        assertThat(organizationRecord.registrationStatus).isEqualTo(OrganizationRegistrationStatus.ACTIVE)
        assertThat(organizationRecord.createdBy).isEqualTo(dummyDevUserUuid(1))

        val userRecord = dsl.selectFrom(Tables.USER)
            .where(Tables.USER.ID.eq(dummyDevUserUuid(1)))
            .fetchOne()

        assertThat(userRecord).isNotNull
        assertThat(userRecord!!.id).isEqualTo(dummyDevUserUuid(1))
        assertThat(userRecord.organizationId).isEqualTo(dummyDevOrganizationId(1))
        assertThat(userRecord.registrationStatus).isEqualTo(UserRegistrationStatus.ACTIVE)
        assertThat(userRecord.onboardingType).isEqualTo(UserOnboardingType.SELF_REGISTRATION)
    }

    @Test
    @TestTransaction
    fun `reject organization changes organization onboarding status`() {
        // assert
        useDevUser(0, 0, setOf(Roles.UserRoles.AUTHORITY_USER))

        ScenarioData().apply {
            organization(1, 1) {
                it.registrationStatus = OrganizationRegistrationStatus.PENDING
            }
            user(1, 1) {
                it.registrationStatus = UserRegistrationStatus.PENDING
                it.onboardingType = UserOnboardingType.SELF_REGISTRATION
            }
            scenarioInstaller.install(this)
        }

        // act
        val result = uiResource.rejectOrganization(dummyDevOrganizationId(1))

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevOrganizationId(1))

        val organizationRecord = dsl.selectFrom(Tables.ORGANIZATION)
            .where(Tables.ORGANIZATION.ID.eq(dummyDevOrganizationId(1)))
            .fetchOne()

        assertThat(organizationRecord).isNotNull
        assertThat(organizationRecord!!.id).isEqualTo(dummyDevOrganizationId(1))
        assertThat(organizationRecord.registrationStatus).isEqualTo(OrganizationRegistrationStatus.REJECTED)
        assertThat(organizationRecord.createdBy).isEqualTo(dummyDevUserUuid(1))

        val userRecord = dsl.selectFrom(Tables.USER)
            .where(Tables.USER.ID.eq(dummyDevUserUuid(1)))
            .fetchOne()

        assertThat(userRecord).isNotNull
        assertThat(userRecord!!.id).isEqualTo(dummyDevUserUuid(1))
        assertThat(userRecord.organizationId).isEqualTo(dummyDevOrganizationId(1))
        assertThat(userRecord.registrationStatus).isEqualTo(UserRegistrationStatus.REJECTED)
        assertThat(userRecord.onboardingType).isEqualTo(UserOnboardingType.SELF_REGISTRATION)
    }

    @Test
    @TestTransaction
    fun `approve and reject organization fails because organization is not pending`() {
        // assert
        useDevUser(0, 0, setOf(Roles.UserRoles.AUTHORITY_USER))

        ScenarioData().apply {
            organization(1, 1) {
                it.registrationStatus = OrganizationRegistrationStatus.ACTIVE
            }
            user(1, 1) {
                it.registrationStatus = UserRegistrationStatus.ACTIVE
                it.onboardingType = UserOnboardingType.SELF_REGISTRATION
            }
            scenarioInstaller.install(this)
        }

        // act & assert exceptions
        assertThatThrownBy { uiResource.approveOrganization(dummyDevOrganizationId(1)) }
            .isInstanceOf(NotAuthorizedException::class.java)
            .hasMessage("Access denied. Organization ${dummyDevOrganizationId(1)} is not in status PENDING")

        assertThatThrownBy { uiResource.rejectOrganization(dummyDevOrganizationId(1)) }
            .isInstanceOf(NotAuthorizedException::class.java)
            .hasMessage("Access denied. Organization ${dummyDevOrganizationId(1)} is not in status PENDING")

        // assert database untouched
        val organizationRecord = dsl.selectFrom(Tables.ORGANIZATION)
            .where(Tables.ORGANIZATION.ID.eq(dummyDevOrganizationId(1)))
            .fetchOne()

        assertThat(organizationRecord).isNotNull
        assertThat(organizationRecord!!.id).isEqualTo(dummyDevOrganizationId(1))
        assertThat(organizationRecord.registrationStatus).isEqualTo(OrganizationRegistrationStatus.ACTIVE)
        assertThat(organizationRecord.createdBy).isEqualTo(dummyDevUserUuid(1))

        val userRecord = dsl.selectFrom(Tables.USER)
            .where(Tables.USER.ID.eq(dummyDevUserUuid(1)))
            .fetchOne()

        assertThat(userRecord).isNotNull
        assertThat(userRecord!!.id).isEqualTo(dummyDevUserUuid(1))
        assertThat(userRecord.organizationId).isEqualTo(dummyDevOrganizationId(1))
        assertThat(userRecord.registrationStatus).isEqualTo(UserRegistrationStatus.ACTIVE)
        assertThat(userRecord.onboardingType).isEqualTo(UserOnboardingType.SELF_REGISTRATION)
    }
}
