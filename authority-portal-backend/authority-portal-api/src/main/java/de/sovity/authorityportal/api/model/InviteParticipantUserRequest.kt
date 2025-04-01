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
import jakarta.validation.constraints.NotNull

@Schema(description = "User information for creating an organization-internal invitation.")
data class InviteParticipantUserRequest(
    @field:NotBlank(message = "Email address cannot be blank")
    @field:Schema(description = "Email address", requiredMode = Schema.RequiredMode.REQUIRED)
    val email: String,

    @field:NotBlank(message = "First name cannot be blank")
    @field:Schema(description = "First name", requiredMode = Schema.RequiredMode.REQUIRED)
    val firstName: String,

    @field:NotBlank(message = "Last name cannot be blank")
    @field:Schema(description = "Last name", requiredMode = Schema.RequiredMode.REQUIRED)
    val lastName: String,

    @NotNull(message = "Role cannot be null")
    @field:Schema(description = "Participant role", requiredMode = Schema.RequiredMode.REQUIRED)
    val role: UserRoleDto,
)
