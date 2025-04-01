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

import de.sovity.authorityportal.api.model.organization.OrganizationLegalIdTypeDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "Information for registering a new user and organization.")
data class RegistrationRequestDto(
    @field:NotBlank(message = "User's Email cannot be blank")
    @field:Schema(description = "User's Email", requiredMode = Schema.RequiredMode.REQUIRED)
    val userEmail: String,

    @field:NotBlank(message = "User's First name cannot be blank")
    @field:Schema(description = "User's First name", requiredMode = Schema.RequiredMode.REQUIRED)
    val userFirstName: String,

    @field:NotBlank(message = "User's Last name cannot be blank")
    @field:Schema(description = "User's Last name", requiredMode = Schema.RequiredMode.REQUIRED)
    val userLastName: String,

    @field:NotBlank(message = "User's Job title cannot be blank")
    @field:Schema(description = "User's Job title", requiredMode = Schema.RequiredMode.REQUIRED)
    val userJobTitle: String,

    @field:NotBlank(message = "User's Phone number cannot be blank")
    @field:Schema(description = "User's Phone number", requiredMode = Schema.RequiredMode.REQUIRED)
    val userPhone: String,

    @field:NotBlank(message = "User's Password cannot be blank")
    @field:Schema(description = "User's Password", requiredMode = Schema.RequiredMode.REQUIRED)
    val userPassword: String,

    @field:NotBlank(message = "Organization's Legal name cannot be blank")
    @field:Schema(description = "Organization's Legal name", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationName: String,

    @field:NotBlank(message = "Organization's URL of the organization website cannot be blank")
    @field:Schema(description = "Organization's URL of the organization website", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationUrl: String,

    @field:Schema(description = "Organization's Business unit", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationBusinessUnit: String,

    @field:Schema(description = "Organization's Industry", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationIndustry: String,

    @field:NotBlank(message = "Organization's Address cannot be blank")
    @field:Schema(description = "Organization's Address", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationAddress: String,

    @field:NotBlank(message = "Organization's Billing Address cannot be blank")
    @field:Schema(description = "Organization's Billing Address", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationBillingAddress: String,

    @field:Schema(description = "Organization description", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationDescription: String,

    @NotNull(message = "Organization's ID type cannot be null")
    @field:Schema(description = "Organization's legal ID type", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationLegalIdType: OrganizationLegalIdTypeDto,

    @field:NotBlank(message = "Organization's legal ID number cannot be blank")
    @field:Schema(
        description = "Organization's legal ID number - either Tax ID or Commerce register number",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val organizationLegalIdNumber: String,

    @field:Schema(description = "Organization's Commerce Register Location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val organizationCommerceRegisterLocation: String?,

    @field:NotBlank(message = "Organization's Main Contact Name cannot be blank")
    @field:Schema(description = "Organization's Main Contact Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationMainContactName: String,

    @field:NotBlank(message = "Organization's Main Contact Email cannot be blank")
    @field:Schema(description = "Organization's Main Contact Email", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationMainContactEmail: String,

    @field:NotBlank(message = "Organization's Main Contact Phone cannot be blank")
    @field:Schema(description = "Organization's Main Contact Phone", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationMainContactPhone: String,

    @field:NotBlank(message = "Organization's Tech Contact Name cannot be blank")
    @field:Schema(description = "Organization's Tech Contact Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationTechContactName: String,

    @field:NotBlank(message = "Organization's Tech Contact Email cannot be blank")
    @field:Schema(description = "Organization's Tech Contact Email", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationTechContactEmail: String,

    @field:NotBlank(message = "Organization's Tech Contact Phone cannot be blank")
    @field:Schema(description = "Organization's Tech Contact Phone", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationTechContactPhone: String,
)
