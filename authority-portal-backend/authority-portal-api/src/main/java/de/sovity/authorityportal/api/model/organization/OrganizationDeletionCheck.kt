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
package de.sovity.authorityportal.api.model.organization

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Result of the organization deletion check.")
data class OrganizationDeletionCheck(
    @field:Schema(description = "Organization's ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationId: String,

    @field:Schema(description = "Indicator if the organization can be deleted", requiredMode = Schema.RequiredMode.REQUIRED)
    val canBeDeleted: Boolean,
)
