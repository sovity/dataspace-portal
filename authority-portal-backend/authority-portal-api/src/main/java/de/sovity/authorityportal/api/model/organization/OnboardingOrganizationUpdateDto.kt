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

package de.sovity.authorityportal.api.model.organization

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "Information about the organization provided while onboarding.")
data class OnboardingOrganizationUpdateDto(
    @field:NotBlank(message = "Name cannot be blank.")
    @field:Schema(description = "Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:Schema(description = "Organization description", requiredMode = Schema.RequiredMode.REQUIRED)
    val description: String,

    @field:NotBlank(message = "Website cannot be blank.")
    @field:Schema(description = "Website", requiredMode = Schema.RequiredMode.REQUIRED)
    val url: String,

    @field:Schema(description = "Business unit", requiredMode = Schema.RequiredMode.REQUIRED)
    val businessUnit: String,

    @field:Schema(description = "Industry", requiredMode = Schema.RequiredMode.REQUIRED)
    val industry: String,

    @field:NotBlank(message = "Address cannot be blank.")
    @field:Schema(description = "Address", requiredMode = Schema.RequiredMode.REQUIRED)
    val address: String,

    @field:NotBlank(message = "Billing address cannot be blank.")
    @field:Schema(description = "Billing address", requiredMode = Schema.RequiredMode.REQUIRED)
    val billingAddress: String,

    @NotNull(message = "Legal identification type cannot be null")
    @field:Schema(description = "Legal identification type", requiredMode = Schema.RequiredMode.REQUIRED)
    val legalIdType: OrganizationLegalIdTypeDto,

    @field:NotBlank(message = "Legal identification number cannot be blank")
    @field:Schema(description = "Legal identification number", requiredMode = Schema.RequiredMode.REQUIRED)
    val legalIdNumber: String,

    @field:Schema(description = "Commerce register location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val commerceRegisterLocation: String?,

    @field:NotBlank(message = "Main contact name cannot be blank")
    @field:Schema(description = "Main contact name", requiredMode = Schema.RequiredMode.REQUIRED)
    val mainContactName: String,

    @field:NotBlank(message = "Main contact email cannot be blank")
    @field:Schema(description = "Main contact email", requiredMode = Schema.RequiredMode.REQUIRED)
    val mainContactEmail: String,

    @field:NotBlank(message = "Main contact phone cannot be blank")
    @field:Schema(description = "Main contact phone", requiredMode = Schema.RequiredMode.REQUIRED)
    val mainContactPhone: String,

    @field:NotBlank(message = "Technical contact name cannot be blank")
    @field:Schema(description = "Technical contact name", requiredMode = Schema.RequiredMode.REQUIRED)
    val techContactName: String,

    @field:NotBlank(message = "Technical contact email cannot be blank")
    @field:Schema(description = "Technical contact email", requiredMode = Schema.RequiredMode.REQUIRED)
    val techContactEmail: String,

    @field:NotBlank(message = "Technical contact phone cannot be blank")
    @field:Schema(description = "Technical contact phone", requiredMode = Schema.RequiredMode.REQUIRED)
    val techContactPhone: String,
)
