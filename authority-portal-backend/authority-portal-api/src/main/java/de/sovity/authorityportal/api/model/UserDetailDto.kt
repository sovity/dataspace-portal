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
import java.time.OffsetDateTime

@Schema(description = "Information about the user.")
data class UserDetailDto(
    @field:Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val userId: String,

    @field:Schema(description = "First name", requiredMode = Schema.RequiredMode.REQUIRED)
    val firstName: String,

    @field:Schema(description = "Last name", requiredMode = Schema.RequiredMode.REQUIRED)
    val lastName: String,

    @field:Schema(description = "Email", requiredMode = Schema.RequiredMode.REQUIRED)
    val email: String,

    @field:Schema(description = "Roles of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    val roles: List<UserRoleDto>,

    @field:Schema(description = "Registration status of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    val registrationStatus: UserRegistrationStatusDto,

    @field:Schema(description = "Creation date of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    val creationDate: OffsetDateTime,

    @field:Schema(description = "Organization ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationId: String,

    @field:Schema(description = "Organization name", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationName: String,

    @field:Schema(description = "Phone number", requiredMode = Schema.RequiredMode.REQUIRED)
    val phone: String,

    @field:Schema(description = "Job description", requiredMode = Schema.RequiredMode.REQUIRED)
    val position: String,

    @field:Schema(description = "Onboarding type", requiredMode = Schema.RequiredMode.REQUIRED)
    val onboardingType: UserOnboardingTypeDto,

    @field:Schema(description = "Inviting user's id if applicable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val invitingUserId: String?,

    @field:Schema(description = "Inviting user's first name if applicable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val invitingUserFirstName: String?,

    @field:Schema(description = "Inviting user's last name if applicable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val invitingUserLastName: String?,
)
