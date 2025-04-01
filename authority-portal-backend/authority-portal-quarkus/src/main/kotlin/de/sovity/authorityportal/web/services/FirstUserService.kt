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

import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.thirdparty.keycloak.model.ApplicationRole
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext
import java.util.concurrent.atomic.AtomicBoolean

@ApplicationScoped
class FirstUserService(
    val dsl: DSLContext,
    val keycloakService: KeycloakService,
    val userService: UserService,
    val organizationService: OrganizationService,
) {

    val isFirstUserHandled = AtomicBoolean(false)

    fun setupFirstUserIfRequired(userId: String, organizationId: String) {
        if (isFirstUserHandled.compareAndSet(false, true) && isFirstUser(userId)) {
            setupFirstUser(userId, organizationId)
        }
    }

    private fun setupFirstUser(userId: String, organizationId: String) {
        keycloakService.setApplicationRoles(userId, listOf(ApplicationRole.AUTHORITY_ADMIN))
        userService.updateStatus(userId, UserRegistrationStatus.ACTIVE)
        organizationService.updateStatus(organizationId, OrganizationRegistrationStatus.ACTIVE)

        Log.info("First user was made ${ApplicationRole.AUTHORITY_ADMIN}. organizationId=$organizationId, userId=$userId")
    }

    private fun isFirstUser(userId: String): Boolean {
        val u = Tables.USER

        val count = dsl.fetchCount(dsl.selectFrom(u))
        if (count != 1) {
            return false
        }

        return dsl.fetchExists(dsl.selectFrom(u).where(u.ID.eq(userId)))
    }
}
