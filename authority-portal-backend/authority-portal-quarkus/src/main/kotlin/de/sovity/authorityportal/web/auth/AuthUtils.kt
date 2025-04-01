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

package de.sovity.authorityportal.web.auth

import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.utils.unauthorized
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class AuthUtils(
    val loggedInUser: LoggedInUser,
    val userService: UserService,
    val organizationService: OrganizationService,
    val connectorService: ConnectorService
) {
    fun requiresAuthenticated() {
        if (!loggedInUser.authenticated) {
            Log.error("User is not authenticated.")
            unauthorized()
        }
    }

    fun requiresUnauthenticated() {
        if (loggedInUser.authenticated) {
            Log.error("User is authenticated, this route requires being unauthenticated, though.")
            unauthorized()
        }
    }

    fun requiresRole(role: String) {
        val userRoles = loggedInUser.roles

        if (!userRoles.contains(role)) {
            Log.error("User is not authorized. userRoles=$userRoles, requiredRole=$role, userId=${loggedInUser.userId}.")
            unauthorized()
        }
    }

    fun requiresAnyRole(vararg roles: String) {
        val userRoles = loggedInUser.roles

        if (!userRoles.any { it in roles }) {
            Log.error("User is not authorized. userRoles=$userRoles, requiredRoles=${roles.toSet()}, userId=${loggedInUser.userId}.")
            unauthorized()
        }
    }

    fun requiresRegistrationStatus(status: UserRegistrationStatus) {
        requiresAuthenticated()
        val userRegistrationStatus = userService.getUserOrThrow(loggedInUser.userId).registrationStatus

        if (userRegistrationStatus != status) {
            Log.error("User registration status is invalid. userRegistrationStatus: $userRegistrationStatus, expectedRegistrationStatus: $status, userId=${loggedInUser.userId}.")
            unauthorized("User registration status is invalid. Expected: $status. Has: $userRegistrationStatus")
        }
    }

    fun requiresTargetRegistrationStatus(userId: String, status: UserRegistrationStatus) {
        val userRegistrationStatus = userService.getUserOrThrow(userId).registrationStatus

        if (userRegistrationStatus != status) {
            Log.error("User registration status is invalid. userRegistrationStatus: $userRegistrationStatus, expectedRegistrationStatus: $status, userId=$userId, adminUserId=${loggedInUser.userId}.")
            unauthorized("User registration status is invalid. Expected: $status. Has: $userRegistrationStatus")
        }
    }

    fun requiresMemberOfAnyOrganization() {
        requiresAuthenticated()
        if (loggedInUser.organizationId.isNullOrEmpty()) {
            Log.error("User is not associated with any organization. userId=${loggedInUser.userId}.")
            unauthorized("User is not associated with any organization")
        }
    }

    fun requiresMemberOfSameOrganizationAs(userId: String) {
        requiresMemberOfAnyOrganization()

        val user = userService.getUserOrThrow(userId)
        if (user.organizationId != loggedInUser.organizationId) {
            Log.error("User is not part of logged in/executing user's organization. userId=$userId, loggedInUserOrganizationId=${loggedInUser.organizationId}, loggedInUserId=${loggedInUser.userId}.")
            unauthorized("User is not part of logged in/executing user's organization")
        }
    }

    fun requiresTargetSelf(userId: String) {
        requiresAuthenticated()
        if (userId != loggedInUser.userId) {
            Log.error("User is not allowed to perform requested action on anyone but themselves. userId=$userId, loggedInUserId=${loggedInUser.userId}.")
            unauthorized("User is not allowed to perform requested action on anyone but themselves")
        }
    }

    fun requiresTargetNotSelf(userId: String) {
        requiresAuthenticated()
        if (userId == loggedInUser.userId) {
            Log.error("User is not allowed to perform requested action on self. userId=$userId, loggedInUserId=${loggedInUser.userId}.")
            unauthorized("User is not allowed to perform requested action on self")
        }
    }

    fun requires(authorized: Boolean, userId: String) {
        if (!authorized) {
            Log.error("User is not authorized. userId=$userId, loggedInUserOrganizationId=${loggedInUser.organizationId}, loggedInUserId=${loggedInUser.userId}.")
            unauthorized()
        }
    }

    fun requiresSelfOrRole(userId: String, role: String) {
        requiresAuthenticated()
        if (userId != loggedInUser.userId && !loggedInUser.roles.contains(role)) {
            Log.error("User can only perform the requested action on themself or needs the desired role. userRoles=${loggedInUser.roles}, requiredRole=$role, userId=$userId, loggedInUserId=${loggedInUser.userId}.")
            unauthorized("User can only perform the requested action on themself or needs the desired role")
        }
    }

    fun hasRole(role: String): Boolean {
        requiresAuthenticated()
        return loggedInUser.roles.contains(role);
    }

    fun isMemberOfSameOrganizationAs(userId: String): Boolean {
        requiresAuthenticated()
        if (loggedInUser.organizationId.isNullOrEmpty()) {
            return false;
        }

        val user = userService.getUserOrThrow(userId)
        return user.organizationId == loggedInUser.organizationId;
    }

    fun requiresOrganizationRegistrationStatus(status: OrganizationRegistrationStatus) {
        requiresMemberOfAnyOrganization()

        val organization = organizationService.getOrganizationOrThrow(loggedInUser.organizationId!!)
        if (organization.registrationStatus != status) {
            Log.error("User can only perform the action if their organization is in the onboarding phase. userId=${loggedInUser.userId}, organizationId=${organization.id}")
            unauthorized("User can only perform the action if their organization is in the onboarding phase")
        }
    }

    fun requiresMemberOfProviderOrganization(connectorId: String) {
        requiresMemberOfAnyOrganization()

        val connector = connectorService.getConnectorOrThrow(connectorId)
        if (connector.providerOrganizationId != loggedInUser.organizationId!!) {
            Log.error("User is not a member of the provider organization. userId=${loggedInUser.userId}, providerOrganizationId=${connector.providerOrganizationId}")
            unauthorized("User is not a member of the provider organization")
        }
    }

    fun requiresMemberOfOwningOrganization(connectorId: String) {
        requiresMemberOfAnyOrganization()

        val connector = connectorService.getConnectorOrThrow(connectorId)
        if (!connectorId.startsWith(loggedInUser.organizationId!!)) {
            Log.error("User is not a member of the owning organization. userId=${loggedInUser.userId}, organizationId=${connector.organizationId}")
            unauthorized("User is not a member of the owning organization")
        }
    }
}
