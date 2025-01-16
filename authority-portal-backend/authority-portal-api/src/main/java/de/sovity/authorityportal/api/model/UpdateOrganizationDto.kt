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

package de.sovity.authorityportal.api.model

import de.sovity.authorityportal.api.model.organization.OrganizationLegalIdTypeDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "Information about a Organization.")
data class UpdateOrganizationDto(
    @field:NotBlank(message = "Organization's name cannot be blank")
    @field:Schema(description = "Organization's Legal name", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:NotBlank(message = "Organization's URL of the organization website cannot be blank")
    @field:Schema(description = "Organization's URL of the organization website", requiredMode = Schema.RequiredMode.REQUIRED)
    val url: String,

    @field:Schema(description = "Organization description", requiredMode = Schema.RequiredMode.REQUIRED)
    val description: String,

    @field:Schema(description = "Organization's Business unit", requiredMode = Schema.RequiredMode.REQUIRED)
    val businessUnit: String,

    @field:Schema(description = "Organization's Industry", requiredMode = Schema.RequiredMode.REQUIRED)
    val industry: String,

    @field:NotBlank(message = "Organization's Address cannot be blank")
    @field:Schema(description = "Organization's Address", requiredMode = Schema.RequiredMode.REQUIRED)
    val address: String,

    @field:NotBlank(message = "Organization's Billing Address cannot be blank")
    @field:Schema(description = "Organization's Billing Address", requiredMode = Schema.RequiredMode.REQUIRED)
    val billingAddress: String,

    @NotNull(message = "Organization's ID type cannot be null")
    @field:Schema(description = "Organization's legal ID type", requiredMode = Schema.RequiredMode.REQUIRED)
    val legalIdType: OrganizationLegalIdTypeDto,

    @field:NotBlank(message = "Organization's legal ID number cannot be blank")
    @field:Schema(
        description = "Organization's legal ID number - either Tax ID or Commerce register number",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val legalIdNumber: String,

    @field:Schema(description = "Organization's Commerce Register Location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val commerceRegisterLocation: String?,

    @field:NotBlank(message = "Organization's Main Contact Name cannot be blank")
    @field:Schema(description = "Organization's Main Contact Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val mainContactName: String,

    @field:NotBlank(message = "Organization's Main Contact Email cannot be blank")
    @field:Schema(description = "Organization's Main Contact Email", requiredMode = Schema.RequiredMode.REQUIRED)
    val mainContactEmail: String,

    @field:NotBlank(message = "Organization's Main Contact Phone cannot be blank")
    @field:Schema(description = "Organization's Main Contact Phone", requiredMode = Schema.RequiredMode.REQUIRED)
    val mainContactPhone: String,

    @field:NotBlank(message = "Organization's Tech Contact Name cannot be blank")
    @field:Schema(description = "Organization's Tech Contact Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val techContactName: String,

    @field:NotBlank(message = "Organization's Tech Contact Email cannot be blank")
    @field:Schema(description = "Organization's Tech Contact Email", requiredMode = Schema.RequiredMode.REQUIRED)
    val techContactEmail: String,

    @field:NotBlank(message = "Organization's Tech Contact Phone cannot be blank")
    @field:Schema(description = "Organization's Tech Contact Phone", requiredMode = Schema.RequiredMode.REQUIRED)
    val techContactPhone: String,
)
