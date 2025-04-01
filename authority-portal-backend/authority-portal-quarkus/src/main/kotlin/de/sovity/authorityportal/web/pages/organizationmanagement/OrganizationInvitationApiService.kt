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

package de.sovity.authorityportal.web.pages.organizationmanagement

import de.sovity.authorityportal.api.model.IdResponse
import de.sovity.authorityportal.api.model.InviteOrganizationRequest
import de.sovity.authorityportal.db.jooq.enums.UserOnboardingType
import de.sovity.authorityportal.web.model.CreateUserData
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
class OrganizationInvitationApiService(
    val keycloakService: KeycloakService,
    val organizationService: OrganizationService,
    val userService: UserService,
    val organizationIdUtils: OrganizationIdUtils,
    val timeUtils: TimeUtils
) {

    fun inviteOrganization(invitationInformation: InviteOrganizationRequest, adminUserId: String): IdResponse {
        if (userService.userExistsInDb(invitationInformation.userEmail)) {
            resourceAlreadyExists("User with email ${invitationInformation.userEmail} already exists.")
        }

        val organizationId = organizationIdUtils.generateOrganizationId()
        val userId = keycloakService.createKeycloakUserAndOrganization(
            organizationId = organizationId,
            userEmail = invitationInformation.userEmail,
            userFirstName = invitationInformation.userFirstName,
            userLastName = invitationInformation.userLastName,
            userOrganizationRole = OrganizationRole.PARTICIPANT_ADMIN,
            userPassword = null
        )

        createDbUserAndOrganization(userId, organizationId, invitationInformation)
        keycloakService.sendInvitationEmailWithPasswordReset(userId)

        Log.info("Invited organization and corresponding initial Participant Admin. organizationId=$organizationId, userId=$userId, adminUserId=$adminUserId.")

        return IdResponse(organizationId, timeUtils.now())
    }

    private fun createDbUserAndOrganization(
        userId: String,
        organizationId: String,
        invitationInformation: InviteOrganizationRequest
    ) {
        val user = userService.createUser(
            userId = userId,
            userData = buildUserData(invitationInformation),
            onboardingType = UserOnboardingType.INVITATION
        )
        organizationService.createInvitedOrganization(
            userId,
            organizationId,
            invitationInformation.orgName
        )
        user.organizationId = organizationId
        user.update()
    }

    private fun buildUserData(invitation: InviteOrganizationRequest): CreateUserData {
        return CreateUserData().apply {
            email = invitation.userEmail
            firstName = invitation.userFirstName
            lastName = invitation.userLastName
            jobTitle = invitation.userJobTitle
            phone = invitation.userPhoneNumber
        }
    }
}
