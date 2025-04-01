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

package de.sovity.authorityportal.web.services

import de.sovity.authorityportal.api.model.MemberInfo
import de.sovity.authorityportal.api.model.UserRoleDto
import de.sovity.authorityportal.db.jooq.tables.records.UserRecord
import de.sovity.authorityportal.web.pages.usermanagement.UserRoleMapper
import de.sovity.authorityportal.web.pages.userregistration.toDto
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.thirdparty.keycloak.model.KeycloakUserDto
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class UserDetailService {

    @Inject
    lateinit var keycloakService: KeycloakService

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var userRoleMapper: UserRoleMapper

    fun getUserData(userId: String): UserDetail {
        val dbUser = userService.getUserOrThrow(userId)
        return buildUserDetail(dbUser)
    }

    fun getAllUserDetails(): List<UserDetail> {
        val dbUsers = userService.getAllUsers()
        return dbUsers.map { dbUser -> buildUserDetail(dbUser) }
    }

    private fun buildUserDetail(dbUser: UserRecord) = UserDetail(
        userId = dbUser.id,
        firstName = dbUser.firstName ?: "",
        lastName = dbUser.lastName ?: "",
        email = dbUser.email ?: "",
        position = dbUser.jobTitle ?: "",
        phoneNumber = dbUser.phone ?: "",
        organizationId = dbUser.organizationId,
        registrationStatus = dbUser.registrationStatus,
        createdAt = dbUser.createdAt,
        roles = keycloakService.getUserRoles(dbUser.id),
        onboardingType = dbUser.onboardingType,
        invitedBy = dbUser.invitedBy
    )

    fun getOrganizationMembers(organizationId: String): List<MemberInfo> {
        val members = keycloakService.getOrganizationMembers(organizationId)
        return members.map {
            val dbUser = userService.getUserOrThrow(it.userId)
            MemberInfo(
                it.userId,
                it.firstName,
                it.lastName,
                getHighestUserRoles(it),
                dbUser.registrationStatus.toDto(),
            )
        }.sortedWith(compareBy({ it.roles.first() }, { it.roles.last() }, { it.lastName }))
    }

    private fun getHighestUserRoles(user: KeycloakUserDto): List<UserRoleDto> {
        // TODO: n + 1 calls to Keycloak => improve
        val keycloakRoles = keycloakService.getUserRoles(user.userId)
        val roles = userRoleMapper.getUserRoles(keycloakRoles)
        return userRoleMapper.getHighestRoles(roles)
    }
}
