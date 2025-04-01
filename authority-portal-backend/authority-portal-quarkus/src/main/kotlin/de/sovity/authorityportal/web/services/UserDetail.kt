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

package de.sovity.authorityportal.web.services

import de.sovity.authorityportal.db.jooq.enums.UserOnboardingType
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import java.time.OffsetDateTime

data class UserDetail(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String?,
    val phoneNumber: String?,
    val organizationId: String?,
    val registrationStatus: UserRegistrationStatus,
    val createdAt: OffsetDateTime,
    val roles: Set<String>,
    val onboardingType: UserOnboardingType,
    val invitedBy: String?
)
