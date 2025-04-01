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

package de.sovity.authorityportal.web.pages.organizationmanagement

import de.sovity.authorityportal.api.model.IdResponse
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.utils.TimeUtils
import de.sovity.authorityportal.web.utils.unauthorized
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrganizationRegistrationApiService(
    val organizationService: OrganizationService,
    val userService: UserService,
    val timeUtils: TimeUtils
) {

    fun approveOrganization(organizationId: String, userId: String): IdResponse {
        requirePending(organizationId, userId)

        val org = organizationService.getOrganizationOrThrow(organizationId)
        org.registrationStatus = OrganizationRegistrationStatus.ACTIVE
        org.update()

        val user = userService.getUserOrThrow(org.createdBy)
        user.registrationStatus = UserRegistrationStatus.ACTIVE
        user.update()

        Log.info("Approved organization and user. organizationId=$organizationId, userId=$userId.")

        return IdResponse(organizationId, timeUtils.now())
    }

    fun rejectOrganization(organizationId: String, userId: String): IdResponse {
        requirePending(organizationId, userId)

        val org = organizationService.getOrganizationOrThrow(organizationId)
        org.registrationStatus = OrganizationRegistrationStatus.REJECTED
        org.update()

        val user = userService.getUserOrThrow(org.createdBy)
        user.registrationStatus = UserRegistrationStatus.REJECTED
        user.update()

        Log.info("Rejected organization and user. organizationId=$organizationId, userId=$userId.")

        return IdResponse(organizationId, timeUtils.now())
    }

    private fun requirePending(organizationId: String, userId: String) {
        val org = organizationService.getOrganizationOrThrow(organizationId)
        val orgRegistrationStatus = org.registrationStatus
        val expectedRegistrationStatus = OrganizationRegistrationStatus.PENDING

        if (orgRegistrationStatus != expectedRegistrationStatus) {
            Log.error("Organization can not be approved/rejected. organizationId=$organizationId, orgRegistrationStatus=$orgRegistrationStatus, expectedRegistrationStatus=$expectedRegistrationStatus, userId=$userId.")
            unauthorized("Organization $organizationId is not in status PENDING")
        }
    }
}
