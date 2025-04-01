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
import de.sovity.authorityportal.api.model.UserDeletionCheck
import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationDeletionService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserDeletionService
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.utils.TimeUtils
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserDeletionApiService(
    val keycloakService: KeycloakService,
    val userService: UserService,
    val organizationService: OrganizationService,
    val connectorService: ConnectorService,
    val timeUtils: TimeUtils,
    val userDeletionService: UserDeletionService,
    val organizationDeletionService: OrganizationDeletionService
) {

    fun checkUserDeletion(userId: String): UserDeletionCheck {
        return userDeletionService.checkUserDeletion(userId)
    }

    fun handleUserDeletion(userId: String, successorUserId: String?, adminUserId: String): IdResponse {
        val userDeletionCheck = userDeletionService.checkUserDeletion(userId)
        val user = userService.getUserOrThrow(userId)
        val organization = organizationService.getOrganizationOrThrow(user.organizationId)

        if (!userDeletionCheck.canBeDeleted) {
            Log.error("User can not be deleted. The reason could be, that they are the last Authority Admin. userId=$userId, adminUserId=$adminUserId.")
            error("User can not be deleted. The reason could be, that they are the last Authority Admin.")
        }

        if (userDeletionCheck.isLastParticipantAdmin) {
            organizationDeletionService.deleteOrganizationAndDependencies(organization.id, adminUserId)
        } else {
            userDeletionService.deleteUserAndHandleDependencies(userDeletionCheck, successorUserId, userId, adminUserId, organization)
        }

        return IdResponse(userId, timeUtils.now())
    }
}
