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
import de.sovity.authorityportal.api.model.organization.OrganizationDeletionCheck
import de.sovity.authorityportal.web.services.OrganizationDeletionService
import de.sovity.authorityportal.web.utils.TimeUtils
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrganizationDeletionApiService(
    var organizationDeletionService: OrganizationDeletionService,
    var timeUtils: TimeUtils
) {

    fun checkOrganizationDeletion(organizationId: String): OrganizationDeletionCheck {
        return organizationDeletionService.checkOrganizationDeletion(organizationId)
    }

    fun deleteOrganizationAndDependencies(organizationId: String, adminUserId: String): IdResponse {
        organizationDeletionService.deleteOrganizationAndDependencies(organizationId, adminUserId)
        return IdResponse(organizationId, timeUtils.now())
    }
}
