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
import de.sovity.authorityportal.api.model.UpdateOrganizationDto
import de.sovity.authorityportal.api.model.UpdateOwnOrganizationDto
import de.sovity.authorityportal.api.model.organization.OnboardingOrganizationUpdateDto
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.utils.TimeUtils
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrganizationUpdateApiService(
    val organizationService: OrganizationService,
    val timeUtils: TimeUtils
) {
    fun updateOwnOrganization(organizationId: String, dto: UpdateOwnOrganizationDto): IdResponse {
        organizationService.updateOwnOrganization(organizationId, dto)
        return IdResponse(organizationId, timeUtils.now())
    }

    fun updateOrganization(organizationId: String, dto: UpdateOrganizationDto): IdResponse {
        organizationService.updateOrganization(organizationId, dto)
        return IdResponse(organizationId, timeUtils.now())
    }

    fun onboardOrganization(organizationId: String, dto: OnboardingOrganizationUpdateDto): IdResponse {
        organizationService.onboardOrganization(organizationId, dto)
        return IdResponse(organizationId, timeUtils.now())
    }
}
