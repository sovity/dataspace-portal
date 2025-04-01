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

import de.sovity.authorityportal.db.jooq.enums.UserOnboardingType
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.web.auth.LoggedInUserFactory.UserAndRoles
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.utils.unauthorized
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.Base64
import java.util.Optional

/**
 * Local E2E Development: Basic Auth via the Quarkus Elytron Security Property File Extension
 */
@ApplicationScoped
class ElytronPropertyFileAuthUtils {

    @Inject
    lateinit var context: SecurityContext

    @Inject
    lateinit var headers: HttpHeaders

    @Inject
    lateinit var userService: UserService

    @ConfigProperty(name = "quarkus.security.users.embedded.users")
    lateinit var devUserCredentials: Optional<Map<String, String>>

    @ConfigProperty(name = "quarkus.security.users.embedded.roles")
    lateinit var devRoles: Optional<Map<String, String>>

    fun getUserAndRoles(): UserAndRoles {
        val userId = getUserId()
        val roles = getRoles(userId)
        val user = userService.getUserOrCreate(userId, UserOnboardingType.SELF_REGISTRATION).also {
            it.registrationStatus = UserRegistrationStatus.ACTIVE
            it.update()
        }
        return UserAndRoles(user, roles)
    }


    private fun getUserFromBasicAuthHeader(): String? {
        val authHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION)
        val base64credentials = authHeader?.firstOrNull()?.substring("Basic ".length)?.trim()
        val credentials = String(Base64.getDecoder().decode(base64credentials)).split(":")

        val userId = credentials.firstOrNull()
        val password = credentials.lastOrNull()
        val devPassword = devUserCredentials.orElseThrow {
            error("Properties for the local dev dummy user must be set in application.properties")
        }[userId]

        if (password != devPassword) {
            unauthorized()
        }

        return userId
    }

    private fun getUserId() = getUserFromBasicAuthHeader() ?: unauthorized()

    private fun getRoles(userId: String) = (devRoles.orElseThrow {
        error("Properties for the local dev dummy user must be set in application.properties")
    }[userId] ?: "")
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toSet()
}
