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

package de.sovity.authorityportal.web.pages.organizationmanagement

import de.sovity.authorityportal.api.model.organization.OrganizationRegistrationStatusDto
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus

fun OrganizationRegistrationStatus.toDto(): OrganizationRegistrationStatusDto = when (this) {
    OrganizationRegistrationStatus.INVITED -> OrganizationRegistrationStatusDto.INVITED
    OrganizationRegistrationStatus.ONBOARDING -> OrganizationRegistrationStatusDto.ONBOARDING
    OrganizationRegistrationStatus.PENDING -> OrganizationRegistrationStatusDto.PENDING
    OrganizationRegistrationStatus.ACTIVE -> OrganizationRegistrationStatusDto.ACTIVE
    OrganizationRegistrationStatus.REJECTED -> OrganizationRegistrationStatusDto.REJECTED
}

fun OrganizationRegistrationStatusDto.toDb(): OrganizationRegistrationStatus = when (this) {
    OrganizationRegistrationStatusDto.INVITED -> OrganizationRegistrationStatus.INVITED
    OrganizationRegistrationStatusDto.ONBOARDING -> OrganizationRegistrationStatus.ONBOARDING
    OrganizationRegistrationStatusDto.PENDING -> OrganizationRegistrationStatus.PENDING
    OrganizationRegistrationStatusDto.ACTIVE -> OrganizationRegistrationStatus.ACTIVE
    OrganizationRegistrationStatusDto.REJECTED -> OrganizationRegistrationStatus.REJECTED
    else -> OrganizationRegistrationStatus.REJECTED
}
