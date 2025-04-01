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

package de.sovity.authorityportal.web.pages.userregistration

import de.sovity.authorityportal.api.model.UserRegistrationStatusDto
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus

fun UserRegistrationStatus.toDto(): UserRegistrationStatusDto = when (this) {
    UserRegistrationStatus.INVITED -> UserRegistrationStatusDto.INVITED
    UserRegistrationStatus.ONBOARDING -> UserRegistrationStatusDto.ONBOARDING
    UserRegistrationStatus.PENDING -> UserRegistrationStatusDto.PENDING
    UserRegistrationStatus.ACTIVE -> UserRegistrationStatusDto.ACTIVE
    UserRegistrationStatus.REJECTED -> UserRegistrationStatusDto.REJECTED
    UserRegistrationStatus.DEACTIVATED -> UserRegistrationStatusDto.DEACTIVATED
    else -> UserRegistrationStatusDto.REJECTED
}

fun UserRegistrationStatusDto.toDb(): UserRegistrationStatus = when (this) {
    UserRegistrationStatusDto.INVITED -> UserRegistrationStatus.INVITED
    UserRegistrationStatusDto.ONBOARDING -> UserRegistrationStatus.ONBOARDING
    UserRegistrationStatusDto.PENDING -> UserRegistrationStatus.PENDING
    UserRegistrationStatusDto.ACTIVE -> UserRegistrationStatus.ACTIVE
    UserRegistrationStatusDto.REJECTED -> UserRegistrationStatus.REJECTED
    UserRegistrationStatusDto.DEACTIVATED -> UserRegistrationStatus.DEACTIVATED
    else -> UserRegistrationStatus.REJECTED
}
