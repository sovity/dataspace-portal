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
import de.sovity.authorityportal.api.model.UserRoleDto
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.utils.TimeUtils
import de.sovity.authorityportal.web.utils.unauthorized
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRoleApiService(
    val keycloakService: KeycloakService,
    val userRoleMapper: UserRoleMapper,
    val timeUtils: TimeUtils
){

    fun changeParticipantRole(userId: String, roleDto: UserRoleDto, organizationId: String, adminUserId: String): IdResponse {
        val role = userRoleMapper.toOrganizationRole(roleDto, userId, adminUserId)

        keycloakService.joinOrganization(userId, organizationId, role)
        keycloakService.forceLogout(userId)

        Log.info("Participant role changed. role=$role, userId=$userId, adminUserId=$adminUserId.")

        return IdResponse(userId, timeUtils.now())
    }

    fun clearApplicationRoles(userId: String, adminUserId: String): IdResponse {
        keycloakService.clearApplicationRoles(userId)
        keycloakService.forceLogout(userId)

        Log.info("Application role cleared. userId=$userId, adminUserId=$adminUserId.")

        return IdResponse(userId, timeUtils.now())
    }

    fun changeApplicationRoles(
        userId: String,
        roleDtos: List<UserRoleDto>,
        adminUserId: String,
        userRoles: Set<String>
    ): IdResponse {
        validateUserRoles(userRoles, roleDtos, adminUserId)

        val roles = roleDtos.map { userRoleMapper.toApplicationRole(it, userId, adminUserId) }

        keycloakService.setApplicationRoles(userId, roles)
        keycloakService.forceLogout(userId)

        Log.info("Application roles changed. roles=${roles.joinToString(",")}, userId=$userId, adminUserId=$adminUserId.")

        return IdResponse(userId, timeUtils.now())
    }

    private fun validateUserRoles(userRoles: Set<String>, roleDtos: List<UserRoleDto>, userId: String) {
        val userRolesDto = userRoleMapper.getUserRoles(userRoles)
        val isAuthorityAdmin = UserRoleDto.AUTHORITY_ADMIN in userRolesDto

        roleDtos.forEach {
            val hasRequestedRole = it in userRolesDto
            if (!isAuthorityAdmin && !hasRequestedRole) {
                unauthorized("User with ID $userId does not have permission to change role to $it")
            }
        }
    }
}
