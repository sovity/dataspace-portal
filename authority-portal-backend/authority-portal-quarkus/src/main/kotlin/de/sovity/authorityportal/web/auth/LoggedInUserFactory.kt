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

import de.sovity.authorityportal.api.model.UserRoleDto
import de.sovity.authorityportal.db.jooq.tables.records.UserRecord
import de.sovity.authorityportal.web.auth.providers.ElytronPropertyFileAuthUtils
import de.sovity.authorityportal.web.auth.providers.KeycloakJwtUtils
import de.sovity.authorityportal.web.services.FirstLoginService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.inject.Produces
import jakarta.ws.rs.core.SecurityContext

@ApplicationScoped
class LoggedInUserFactory(
    val context: SecurityContext,
    val keycloakJwtUtils: KeycloakJwtUtils,
    val elytronPropertyFileAuthUtils: ElytronPropertyFileAuthUtils,
    val firstLoginService: FirstLoginService
) {

    @Produces
    @RequestScoped
    fun getLoggedInUser(): LoggedInUser {
        if (context.userPrincipal == null) {
            return unauthenticatedLoggedInUser()
        }

        val userAndRoles = when (context.authenticationScheme) {
            "Basic" -> elytronPropertyFileAuthUtils.getUserAndRoles()
            else -> keycloakJwtUtils.getUserAndRoles()
        }

        // With the first successful login invited users will be approved
        // At this point all Keycloak required actions should be done
        // e.g. confirm E-Mail, setup 2FA
        firstLoginService.approveIfInvited(userAndRoles.user)

        return authenticatedLoggedInUser(userAndRoles)
    }

    private fun authenticatedLoggedInUser(userAndRoles: UserAndRoles) = LoggedInUser(
        true,
        userAndRoles.user.id,
        userAndRoles.user.organizationId,
        userAndRoles.roles
    )

    private fun unauthenticatedLoggedInUser() = LoggedInUser(
        false,
        "",
        null,
        setOf(UserRoleDto.UNAUTHENTICATED.name)
    )

    data class UserAndRoles(val user: UserRecord, val roles: Set<String>)
}
