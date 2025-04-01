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
 * Each organization gets a user group in Keycloak. Each organization user group has several subgroups
 * with the following names, giving their users the following user role.
 *
 * @param kcSubGroupName The name of the subgroup in Keycloak.
 * @param userRole The role of the user in the organization.
 */
enum class OrganizationRole(val kcSubGroupName: String, val userRole: String) {
    PARTICIPANT_ADMIN("Participant Admin", Roles.UserRoles.PARTICIPANT_ADMIN),
    PARTICIPANT_CURATOR("Participant Curator", Roles.UserRoles.PARTICIPANT_CURATOR),
    PARTICIPANT_USER("Participant User", Roles.UserRoles.PARTICIPANT_USER)
}
