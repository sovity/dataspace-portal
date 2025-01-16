/*
 * Copyright (c) 2024 sovity GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      sovity GmbH - initial implementation
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
