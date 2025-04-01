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

package de.sovity.authorityportal.web.thirdparty.keycloak.model

import de.sovity.authorityportal.web.Roles

/**
 * Each user can have several roles in the application. These roles are mapped to Keycloak role groups.
 *
 * @param kcGroupName The name of the role group in Keycloak.
 * @param userRole The role of the user in the application.
 */
enum class ApplicationRole(val kcGroupName: String, val userRole: String) {
    OPERATOR_ADMIN("ROLE_OPERATOR_ADMIN", Roles.UserRoles.OPERATOR_ADMIN),
    SERVICE_PARTNER_ADMIN("ROLE_SERVICE_PARTNER_ADMIN", Roles.UserRoles.SERVICE_PARTNER_ADMIN),
    AUTHORITY_ADMIN("ROLE_AUTHORITY_ADMIN", Roles.UserRoles.AUTHORITY_ADMIN),
    AUTHORITY_USER("ROLE_AUTHORITY_USER", Roles.UserRoles.AUTHORITY_USER)
}
