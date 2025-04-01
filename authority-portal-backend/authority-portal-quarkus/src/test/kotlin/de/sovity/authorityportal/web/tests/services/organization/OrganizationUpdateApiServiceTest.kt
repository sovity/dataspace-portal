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
import de.sovity.authorityportal.api.model.UpdateOrganizationDto
import de.sovity.authorityportal.api.model.UpdateOwnOrganizationDto
import de.sovity.authorityportal.api.model.organization.OnboardingOrganizationUpdateDto
import de.sovity.authorityportal.api.model.organization.OrganizationLegalIdTypeDto
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.seeds.utils.dummyDevOrganizationId
import de.sovity.authorityportal.web.Roles
import de.sovity.authorityportal.web.pages.organizationmanagement.toDb
import de.sovity.authorityportal.web.tests.useDevUser
import de.sovity.authorityportal.web.tests.useMockNow
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
import java.time.OffsetDateTime

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class OrganizationUpdateApiServiceTest {

    @Inject
    lateinit var uiResource: UiResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Inject
    lateinit var dsl: DSLContext

    @Test
    @TestTransaction
    fun `update onboarding organization fails because organization is not in onboarding state`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))

        ScenarioData().apply {
            organization(0, 0) {
                it.registrationStatus = OrganizationRegistrationStatus.ACTIVE
            }
            user(0, 0)
            scenarioInstaller.install(this)
        }

        val request = OnboardingOrganizationUpdateDto(
            name = "Max's Organization",
            description = "This is a test organization",
            url = "https://www.test-organization.com",
            businessUnit = "IT",
            industry = "Software",
            address = "Test Street, 12345, Test City, Test Country",
            billingAddress = "Test Street, 12345, Test City, Test Country",
            legalIdType = OrganizationLegalIdTypeDto.COMMERCE_REGISTER_INFO,
            legalIdNumber = "HRB123456",
            commerceRegisterLocation = "Test City",
            mainContactName = "Test Contact",
            mainContactEmail = "contact@example.com",
            mainContactPhone = "+1234567890",
            techContactName = "Test Tech Contact",
            techContactEmail = "techcontact@example.com",
            techContactPhone = "+1234567890"
        )

        // act & assert
        assertThatThrownBy {
            uiResource.updateOnboardingOrganization(request)
        }.isInstanceOf(NotAuthorizedException::class.java)
    }

    @Test
    @TestTransaction
    fun `update onboarding organization changes the organization data appropriately`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0) {
                it.name = "Test organization"
                it.registrationStatus = OrganizationRegistrationStatus.ONBOARDING
            }
            user(0, 0)
            scenarioInstaller.install(this)
        }

        val request = OnboardingOrganizationUpdateDto(
            name = "Max's Organization",
            description = "This is a test organization",
            url = "https://www.test-organization.com",
            businessUnit = "IT",
            industry = "Software",
            address = "Test Street, 12345, Test City, Test Country",
            billingAddress = "Test Street, 12345, Test City, Test Country",
            legalIdType = OrganizationLegalIdTypeDto.COMMERCE_REGISTER_INFO,
            legalIdNumber = "HRB123456",
            commerceRegisterLocation = "Test City",
            mainContactName = "Test Contact",
            mainContactEmail = "contact@example.com",
            mainContactPhone = "+1234567890",
            techContactName = "Test Tech Contact",
            techContactEmail = "techcontact@example.com",
            techContactPhone = "+1234567890"
        )

        // act
        val result = uiResource.updateOnboardingOrganization(request)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevOrganizationId(0))

        val actual = dsl.selectFrom(Tables.ORGANIZATION).where(Tables.ORGANIZATION.ID.eq(dummyDevOrganizationId(0))).fetchOne()
        assertThat(actual).isNotNull
        assertThat(actual!!.registrationStatus).isEqualTo(OrganizationRegistrationStatus.ACTIVE)
        assertThat(actual.name).isEqualTo("Max's Organization")
        assertThat(actual.description).isEqualTo("This is a test organization")
        assertThat(actual.url).isEqualTo("https://www.test-organization.com")
        assertThat(actual.businessUnit).isEqualTo("IT")
        assertThat(actual.industry).isEqualTo("Software")
        assertThat(actual.address).isEqualTo("Test Street, 12345, Test City, Test Country")
        assertThat(actual.billingAddress).isEqualTo("Test Street, 12345, Test City, Test Country")
        assertThat(actual.legalIdType).isEqualTo(OrganizationLegalIdTypeDto.COMMERCE_REGISTER_INFO.toDb())
        assertThat(actual.commerceRegisterNumber).isEqualTo("HRB123456")
        assertThat(actual.taxId).isNull()
        assertThat(actual.commerceRegisterLocation).isEqualTo("Test City")
        assertThat(actual.mainContactName).isEqualTo("Test Contact")
        assertThat(actual.mainContactEmail).isEqualTo("contact@example.com")
        assertThat(actual.mainContactPhone).isEqualTo("+1234567890")
        assertThat(actual.techContactName).isEqualTo("Test Tech Contact")
        assertThat(actual.techContactEmail).isEqualTo("techcontact@example.com")
        assertThat(actual.techContactPhone).isEqualTo("+1234567890")
    }

    @Test
    @TestTransaction
    fun `update own organization fails because user is not admin of that organization`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_USER))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            scenarioInstaller.install(this)
        }

        val request = UpdateOwnOrganizationDto(
            description = "This is a test organization",
            url = "https://www.test-organization.com",
            businessUnit = "IT",
            industry = "Software",
            address = "Test Street, 12345, Test City, Test Country",
            billingAddress = "Test Street, 12345, Test City, Test Country",
            mainContactName = "Test Contact",
            mainContactEmail = "contact@example.com",
            mainContactPhone = "+1234567890",
            techContactName = "Test Tech Contact",
            techContactEmail = "techcontact@example.com",
            techContactPhone = "+1234567890"
        )

        // act & assert
        assertThatThrownBy {
            uiResource.updateOwnOrganizationDetails(request)
        }.isInstanceOf(NotAuthorizedException::class.java)
    }

    @Test
    @TestTransaction
    fun `update organization fails because user is not authority admin`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.AUTHORITY_USER))

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            scenarioInstaller.install(this)
        }

        val request = UpdateOrganizationDto(
            name = "Max's Organization",
            description = "This is a test organization",
            url = "https://www.test-organization.com",
            businessUnit = "IT",
            industry = "Software",
            address = "Test Street, 12345, Test City, Test Country",
            billingAddress = "Test Street, 12345, Test City, Test Country",
            legalIdType = OrganizationLegalIdTypeDto.COMMERCE_REGISTER_INFO,
            legalIdNumber = "HRB123456",
            commerceRegisterLocation = "Test City",
            mainContactName = "Test Contact",
            mainContactEmail = "contact@example.com",
            mainContactPhone = "+1234567890",
            techContactName = "Test Tech Contact",
            techContactEmail = "techcontact@example.com",
            techContactPhone = "+1234567890"
        )

        // act & assert
        assertThatThrownBy {
            uiResource.updateOrganizationDetails(dummyDevOrganizationId(0), request)
        }.isInstanceOf(NotAuthorizedException::class.java)
    }

    @Test
    @TestTransaction
    fun `update own organization as participant admin appropriately`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_ADMIN))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            scenarioInstaller.install(this)
        }

        val request = UpdateOwnOrganizationDto(
            description = "This is a test organization",
            url = "https://www.test-organization.com",
            businessUnit = "IT",
            industry = "Software",
            address = "Test Street, 12345, Test City, Test Country",
            billingAddress = "Test Street, 12345, Test City, Test Country",
            mainContactName = "Test Contact",
            mainContactEmail = "contact@example.com",
            mainContactPhone = "+1234567890",
            techContactName = "Test Tech Contact",
            techContactEmail = "techcontact@example.com",
            techContactPhone = "+1234567890"
        )

        // act
        val result = uiResource.updateOwnOrganizationDetails(request)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevOrganizationId(0))

        val actual = dsl.selectFrom(Tables.ORGANIZATION).where(Tables.ORGANIZATION.ID.eq(dummyDevOrganizationId(0))).fetchOne()
        assertThat(actual).isNotNull
        assertThat(actual!!.registrationStatus).isEqualTo(OrganizationRegistrationStatus.ACTIVE)
        assertThat(actual.description).isEqualTo("This is a test organization")
        assertThat(actual.url).isEqualTo("https://www.test-organization.com")
        assertThat(actual.businessUnit).isEqualTo("IT")
        assertThat(actual.industry).isEqualTo("Software")
        assertThat(actual.address).isEqualTo("Test Street, 12345, Test City, Test Country")
        assertThat(actual.billingAddress).isEqualTo("Test Street, 12345, Test City, Test Country")
        assertThat(actual.mainContactName).isEqualTo("Test Contact")
        assertThat(actual.mainContactEmail).isEqualTo("contact@example.com")
        assertThat(actual.mainContactPhone).isEqualTo("+1234567890")
        assertThat(actual.techContactName).isEqualTo("Test Tech Contact")
        assertThat(actual.techContactEmail).isEqualTo("techcontact@example.com")
        assertThat(actual.techContactPhone).isEqualTo("+1234567890")
    }

    @Test
    @TestTransaction
    fun `update organization as authority admin appropriately`() {
        // arrange
        val now = OffsetDateTime.now()

        useDevUser(0, 0, setOf(Roles.UserRoles.AUTHORITY_ADMIN))
        useMockNow(now)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            scenarioInstaller.install(this)
        }

        val request = UpdateOrganizationDto(
            name = "Max's Organization",
            description = "This is a test organization",
            url = "https://www.test-organization.com",
            businessUnit = "IT",
            industry = "Software",
            address = "Test Street, 12345, Test City, Test Country",
            billingAddress = "Test Street, 12345, Test City, Test Country",
            legalIdType = OrganizationLegalIdTypeDto.COMMERCE_REGISTER_INFO,
            legalIdNumber = "HRB123456",
            commerceRegisterLocation = "Test City",
            mainContactName = "Test Contact",
            mainContactEmail = "contact@example.com",
            mainContactPhone = "+1234567890",
            techContactName = "Test Tech Contact",
            techContactEmail = "techcontact@example.com",
            techContactPhone = "+1234567890"
        )

        // act
        val result = uiResource.updateOrganizationDetails(dummyDevOrganizationId(0), request)

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevOrganizationId(0))

        val actual = dsl.selectFrom(Tables.ORGANIZATION).where(Tables.ORGANIZATION.ID.eq(dummyDevOrganizationId(0))).fetchOne()
        assertThat(actual).isNotNull
        assertThat(actual!!.registrationStatus).isEqualTo(OrganizationRegistrationStatus.ACTIVE)
        assertThat(actual.name).isEqualTo("Max's Organization")
        assertThat(actual.description).isEqualTo("This is a test organization")
        assertThat(actual.url).isEqualTo("https://www.test-organization.com")
        assertThat(actual.businessUnit).isEqualTo("IT")
        assertThat(actual.industry).isEqualTo("Software")
        assertThat(actual.address).isEqualTo("Test Street, 12345, Test City, Test Country")
        assertThat(actual.billingAddress).isEqualTo("Test Street, 12345, Test City, Test Country")
        assertThat(actual.legalIdType).isEqualTo(OrganizationLegalIdTypeDto.COMMERCE_REGISTER_INFO.toDb())
        assertThat(actual.commerceRegisterNumber).isEqualTo("HRB123456")
        assertThat(actual.taxId).isNull()
        assertThat(actual.commerceRegisterLocation).isEqualTo("Test City")
        assertThat(actual.mainContactName).isEqualTo("Test Contact")
        assertThat(actual.mainContactEmail).isEqualTo("contact@example.com")
        assertThat(actual.mainContactPhone).isEqualTo("+1234567890")
        assertThat(actual.techContactName).isEqualTo("Test Tech Contact")
        assertThat(actual.techContactEmail).isEqualTo("techcontact@example.com")
        assertThat(actual.techContactPhone).isEqualTo("+1234567890")
    }
}
