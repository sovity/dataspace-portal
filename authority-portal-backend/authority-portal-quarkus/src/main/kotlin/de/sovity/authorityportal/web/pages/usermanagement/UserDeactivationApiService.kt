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
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.utils.TimeUtils
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserDeactivationApiService(
    val keycloakService: KeycloakService,
    val userService: UserService,
    val timeUtils: TimeUtils
) {

    fun deactivateUser(userId: String, adminUserId: String): IdResponse {
        keycloakService.deactivateUser(userId)
        setUserActivationStatus(userId, UserRegistrationStatus.DEACTIVATED)

        keycloakService.forceLogout(userId)

        Log.info("User deactivated. userId=$userId, adminUserId=$adminUserId.")

        return IdResponse(userId, timeUtils.now())
    }

    fun reactivateUser(userId: String, adminUserId: String): IdResponse {
        keycloakService.reactivateUser(userId)
        setUserActivationStatus(userId, UserRegistrationStatus.ACTIVE)

        keycloakService.forceLogout(userId)

        Log.info("User reactivated. userId=$userId, adminUserId=$adminUserId.")

        return IdResponse(userId, timeUtils.now())
    }

    private fun setUserActivationStatus(userId: String, status: UserRegistrationStatus) {
        val user = userService.getUserOrThrow(userId)
        user.registrationStatus = status
        user.update()
    }
}
