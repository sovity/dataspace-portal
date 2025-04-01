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

@Schema(description = "Information for inviting a new organization.")
data class InviteOrganizationRequest(
    @field:NotBlank(message = "User email cannot be blank")
    @field:Schema(description = "User: Email address", requiredMode = Schema.RequiredMode.REQUIRED)
    val userEmail: String,

    @field:NotBlank(message = "User first name cannot be blank")
    @field:Schema(description = "User: First name", requiredMode = Schema.RequiredMode.REQUIRED)
    val userFirstName: String,

    @field:NotBlank(message = "User last name cannot be blank")
    @field:Schema(description = "User: Last name", requiredMode = Schema.RequiredMode.REQUIRED)
    val userLastName: String,

    @field:NotBlank(message = "Organization name cannot be blank")
    @field:Schema(description = "Organization: Legal name", requiredMode = Schema.RequiredMode.REQUIRED)
    val orgName: String,

    @field:Schema(description = "User job title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val userJobTitle: String?,

    @field:Schema(description = "User phone number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val userPhoneNumber: String?,
)
