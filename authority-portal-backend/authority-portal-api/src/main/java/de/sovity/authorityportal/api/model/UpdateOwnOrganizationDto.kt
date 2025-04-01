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

package de.sovity.authorityportal.api.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Information about the own Organization.")
data class UpdateOwnOrganizationDto(
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
