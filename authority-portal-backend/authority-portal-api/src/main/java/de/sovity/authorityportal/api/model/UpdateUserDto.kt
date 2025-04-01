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

@Schema(description = "Information about the user.")
data class UpdateUserDto(
    @field:Schema(description = "User's First name", requiredMode = Schema.RequiredMode.REQUIRED)
    val firstName: String,

    @field:NotBlank(message = "User's Last name cannot be blank")
    @field:Schema(description = "User's Last name", requiredMode = Schema.RequiredMode.REQUIRED)
    val lastName: String,

    @field:NotBlank(message = "User's Job title cannot be blank")
    @field:Schema(description = "User's Job title", requiredMode = Schema.RequiredMode.REQUIRED)
    val jobTitle: String,

    @field:NotBlank(message = "User's Phone number cannot be blank")
    @field:Schema(description = "User's Phone number", requiredMode = Schema.RequiredMode.REQUIRED)
    val phone: String,

    @field:NotBlank(message = "User's email cannot be blank")
    @field:Schema(description = "User's email", requiredMode = Schema.RequiredMode.REQUIRED)
    val email: String,
)
