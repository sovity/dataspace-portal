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

package de.sovity.authorityportal.web.pages.usermanagement

import de.sovity.authorityportal.api.model.IdResponse
import de.sovity.authorityportal.api.model.InviteParticipantUserRequest
import de.sovity.authorityportal.db.jooq.enums.UserOnboardingType
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.utils.TimeUtils
import de.sovity.authorityportal.web.utils.resourceAlreadyExists
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserInvitationApiService(
    val keycloakService: KeycloakService,
    val userService: UserService,
    val userRoleMapper: UserRoleMapper,
    val timeUtils: TimeUtils
) {

    fun inviteParticipantUser(
        userInformation: InviteParticipantUserRequest,
        organizationId: String,
        adminUserId: String
    ): IdResponse {
        if (userService.userExistsInDb(userInformation.email)) {
            resourceAlreadyExists("User with email ${userInformation.email} already exists.")
        }

        // DB is source of truth, so we delete a potentially existing user in Keycloak
        keycloakService.getUserIdByEmail(userInformation.email)?.let { keycloakService.deleteUser(it) }

        val userId =
            keycloakService.createUser(userInformation.email, userInformation.firstName, userInformation.lastName)
        keycloakService.sendInvitationEmailWithPasswordReset(userId)
        keycloakService.joinOrganization(
            userId = userId,
            organizationId = organizationId,
            role = userRoleMapper.toOrganizationRole(userInformation.role, userId, adminUserId)
        )

        userService.createUser(
            userId = userId,
            organizationId = organizationId,
            onboardingType = UserOnboardingType.INVITATION,
            invitedBy = adminUserId
        ).also {
            it.firstName = userInformation.firstName
            it.lastName = userInformation.lastName
            it.email = userInformation.email
            it.update()
        }

        Log.info("New participant account invited. userId=$userId, role=${userInformation.role}, organizationId=$organizationId, adminUserId=$adminUserId")

        return IdResponse(userId, timeUtils.now())
    }
}
