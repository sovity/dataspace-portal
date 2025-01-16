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
