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

package de.sovity.authorityportal.web.pages.registration

import de.sovity.authorityportal.api.model.IdResponse
import de.sovity.authorityportal.api.model.RegistrationRequestDto
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.enums.UserOnboardingType
import de.sovity.authorityportal.web.model.CreateOrganizationData
import de.sovity.authorityportal.web.model.CreateUserData
import de.sovity.authorityportal.web.pages.organizationmanagement.toDb
import de.sovity.authorityportal.web.services.FirstUserService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.thirdparty.keycloak.model.OrganizationRole
import de.sovity.authorityportal.web.utils.TimeUtils
import de.sovity.authorityportal.web.utils.idmanagement.OrganizationIdUtils
import de.sovity.authorityportal.web.utils.resourceAlreadyExists
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class RegistrationApiService(
    val keycloakService: KeycloakService,
    val organizationService: OrganizationService,
    val userService: UserService,
    val organizationIdUtils: OrganizationIdUtils,
    val firstUserService: FirstUserService,
    val timeUtils: TimeUtils
) {

    fun registerUserAndOrganization(registrationRequest: RegistrationRequestDto): IdResponse {
        if (userService.userExistsInDb(registrationRequest.userEmail)) {
            resourceAlreadyExists("User with email ${registrationRequest.userEmail} already exists.")
        }

        val organizationId = organizationIdUtils.generateOrganizationId()
        val userId = keycloakService.createKeycloakUserAndOrganization(
            organizationId = organizationId,
            userEmail = registrationRequest.userEmail,
            userFirstName = registrationRequest.userFirstName,
            userLastName = registrationRequest.userLastName,
            userOrganizationRole = OrganizationRole.PARTICIPANT_ADMIN,
            userPassword = registrationRequest.userPassword,
        )

        createDbUserAndOrganization(userId, organizationId, registrationRequest)
        keycloakService.sendInvitationEmail(userId)
        firstUserService.setupFirstUserIfRequired(userId, organizationId)

        Log.info("Register organization and User. organizationId=$organizationId, userId=$userId")

        return IdResponse(userId, timeUtils.now())
    }

    private fun createDbUserAndOrganization(
        userId: String,
        organizationId: String,
        registrationRequest: RegistrationRequestDto
    ) {
        val user = userService.createUser(
            userId = userId,
            userData = buildUserData(registrationRequest),
            onboardingType = UserOnboardingType.SELF_REGISTRATION
        )
        organizationService.createOrganization(
            userId = userId,
            organizationId = organizationId,
            organizationData = buildOrganizationData(registrationRequest),
            registrationStatus = OrganizationRegistrationStatus.PENDING
        )
        user.organizationId = organizationId
        user.update()
    }

    private fun buildUserData(registrationRequest: RegistrationRequestDto): CreateUserData {
        return CreateUserData().apply {
            email = registrationRequest.userEmail
            firstName = registrationRequest.userFirstName
            lastName = registrationRequest.userLastName
            jobTitle = registrationRequest.userJobTitle
            phone = registrationRequest.userPhone
        }
    }

    private fun buildOrganizationData(registrationRequest: RegistrationRequestDto): CreateOrganizationData {
        return CreateOrganizationData().apply {
            name = registrationRequest.organizationName
            url = registrationRequest.organizationUrl
            businessUnit = registrationRequest.organizationBusinessUnit
            industry = registrationRequest.organizationIndustry
            address = registrationRequest.organizationAddress
            billingAddress = registrationRequest.organizationBillingAddress
            description = registrationRequest.organizationDescription
            legalIdType = registrationRequest.organizationLegalIdType.toDb()
            legalIdNumber = registrationRequest.organizationLegalIdNumber
            commerceRegisterLocation = registrationRequest.organizationCommerceRegisterLocation
            mainContactName = registrationRequest.organizationMainContactName
            mainContactEmail = registrationRequest.organizationMainContactEmail
            mainContactPhone = registrationRequest.organizationMainContactPhone
            techContactName = registrationRequest.organizationTechContactName
            techContactEmail = registrationRequest.organizationTechContactEmail
            techContactPhone = registrationRequest.organizationTechContactPhone
        }
    }
}
