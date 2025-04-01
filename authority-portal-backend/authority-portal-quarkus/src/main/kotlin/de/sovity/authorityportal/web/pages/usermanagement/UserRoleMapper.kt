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

import de.sovity.authorityportal.api.model.UserRoleDto
import de.sovity.authorityportal.web.Roles
import de.sovity.authorityportal.web.thirdparty.keycloak.model.ApplicationRole
import de.sovity.authorityportal.web.thirdparty.keycloak.model.OrganizationRole
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRoleMapper {
    private val mapping = mapOf(
        Roles.UserRoles.OPERATOR_ADMIN to UserRoleDto.OPERATOR_ADMIN,
        Roles.UserRoles.SERVICE_PARTNER_ADMIN to UserRoleDto.SERVICE_PARTNER_ADMIN,
        Roles.UserRoles.AUTHORITY_ADMIN to UserRoleDto.AUTHORITY_ADMIN,
        Roles.UserRoles.AUTHORITY_USER to UserRoleDto.AUTHORITY_USER,
        Roles.UserRoles.PARTICIPANT_ADMIN to UserRoleDto.ADMIN,
        Roles.UserRoles.PARTICIPANT_CURATOR to UserRoleDto.KEY_USER,
        Roles.UserRoles.PARTICIPANT_USER to UserRoleDto.USER,
    )

    private val participantRoles = setOf(
        UserRoleDto.ADMIN,
        UserRoleDto.KEY_USER,
        UserRoleDto.USER
    )

    private val applicationRoles = setOf(
        UserRoleDto.AUTHORITY_ADMIN,
        UserRoleDto.AUTHORITY_USER
    )

    fun getUserRoles(roles: Set<String>): Set<UserRoleDto> {
        return roles.mapNotNull { mapping[it] }.toSet()
    }

    /**
     * Reduces roles to visible roles for the UI.
     */
    fun getHighestRoles(roles: Set<UserRoleDto>): List<UserRoleDto> {
        val highestRoles = mutableListOf<UserRoleDto>()
        getHighestApplicationRole(roles)?.let { highestRoles.add(it) }
        highestRoles.addAll(getRemainingRoles(roles).sorted())
        getHighestParticipantRole(roles)?.let { highestRoles.add(it) }

        return highestRoles
    }

    private fun getRemainingRoles(roles: Set<UserRoleDto>): Set<UserRoleDto> =
        roles - applicationRoles - participantRoles - UserRoleDto.UNAUTHENTICATED

    private fun getHighestParticipantRole(roles: Set<UserRoleDto>): UserRoleDto? =
        participantRoles.firstOrNull { roles.contains(it) }

    private fun getHighestApplicationRole(roles: Set<UserRoleDto>): UserRoleDto? =
        applicationRoles.firstOrNull { roles.contains(it) }

    fun toOrganizationRole(role: UserRoleDto, userId: String, adminUserId: String): OrganizationRole {
        return when (role) {
            UserRoleDto.ADMIN -> OrganizationRole.PARTICIPANT_ADMIN
            UserRoleDto.KEY_USER -> OrganizationRole.PARTICIPANT_CURATOR
            UserRoleDto.USER -> OrganizationRole.PARTICIPANT_USER
            else -> {
                error("Participant Admin can only change role to Participant Admin, Curator or User. role=$role, userId=$userId, adminUserId=$adminUserId.")
            }
        }
    }

    fun toApplicationRole(role: UserRoleDto, userId: String, adminUserId: String): ApplicationRole {
        return when (role) {
            UserRoleDto.AUTHORITY_ADMIN -> ApplicationRole.AUTHORITY_ADMIN
            UserRoleDto.AUTHORITY_USER -> ApplicationRole.AUTHORITY_USER
            UserRoleDto.OPERATOR_ADMIN -> ApplicationRole.OPERATOR_ADMIN
            UserRoleDto.SERVICE_PARTNER_ADMIN -> ApplicationRole.SERVICE_PARTNER_ADMIN
            else -> { error("Authority Admin can only change role to Authority Admin, Authority User, Operator Admin, Service Partner Admin or none. role=$role, userId=$userId, adminUserId=$adminUserId.") }
        }
    }
}
