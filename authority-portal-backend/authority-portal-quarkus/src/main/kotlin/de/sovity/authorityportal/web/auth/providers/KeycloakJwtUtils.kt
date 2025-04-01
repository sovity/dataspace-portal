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

package de.sovity.authorityportal.web.auth.providers

import de.sovity.authorityportal.web.auth.LoggedInUserFactory.UserAndRoles
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.utils.unauthorized
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import jakarta.inject.Inject
import jakarta.json.JsonObject
import jakarta.json.JsonString
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.JsonWebToken

/**
 * Production: Keycloak with OAuth2
 */
@ApplicationScoped
class KeycloakJwtUtils {

    @Inject
    lateinit var jwtInstance: Instance<JsonWebToken>

    @Inject
    lateinit var context: SecurityContext

    @Inject
    lateinit var userService: UserService

    fun getUserAndRoles(): UserAndRoles {
        val jwt = jsonWebToken()
        val userId = getUserId(jwt)
        val roles = getRoles(jwt)
        val user = userService.getUserOrThrow(userId)
        return UserAndRoles(user, roles)
    }

    private fun getUserId(jwt: JsonWebToken): String =
        jwt.claim<String>("sub").orElseGet { unauthorized() }

    private fun getRoles(jwt: JsonWebToken) = jwt.claim<JsonObject>("realm_access")
        .orElseGet { unauthorized() }
        .getJsonArray("roles")
        .filterIsInstance<JsonString>()
        .filter { it.string.startsWith("UR_") || it.string.startsWith("AR_") }
        .map { it.string }
        .toSet()

    private fun jsonWebToken() = jwtInstance.toList().firstOrNull() ?: unauthorized()
}
