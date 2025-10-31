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

import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.utils.TimeUtils
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class UnconfirmedUserDeletionService(
    val userService: UserService,
    val keycloakService: KeycloakService,
    val organizationService: OrganizationService,
    val timeUtils: TimeUtils,
    @ConfigProperty(name = "authority-portal.invitation.expiration") val inviteExpirationTime: String
) {

    @Transactional
    @Scheduled(every = "15m")
    fun deleteUnconfirmedUsersAndOrganizations() {
        val expirationCutoffTime = timeUtils.now().minusSeconds(inviteExpirationTime.toLong())

        userService.removeOrganizationIdFromUnconfirmedUsers(expirationCutoffTime)

        val unconfirmedOrganizationIds = organizationService.getUnconfirmedOrganizationOrganizationIds(expirationCutoffTime)
        val deletedOrgsAmount = organizationService.deleteUnconfirmedOrganizations(unconfirmedOrganizationIds)
        Log.info("Deleted unconfirmed organizations in DB. amount=$deletedOrgsAmount.")
        unconfirmedOrganizationIds.forEach { organizationId ->
            keycloakService.deleteOrganization(organizationId)
            Log.info("Deleted unconfirmed organization in Keycloak. organizationId=$organizationId.")
        }

        val unconfirmedUserIds = userService.getUnconfirmedUserIds(expirationCutoffTime)
        val deletedUsersAmount = userService.deleteUnconfirmedUsers(unconfirmedUserIds)
        Log.info("Deleted unconfirmed users in DB. amount=$deletedUsersAmount.")
        unconfirmedUserIds.forEach { userId ->
            keycloakService.deleteUserSafely(userId)
            Log.info("Deleted unconfirmed user in Keycloak. userId=$userId.")
        }
    }
}
