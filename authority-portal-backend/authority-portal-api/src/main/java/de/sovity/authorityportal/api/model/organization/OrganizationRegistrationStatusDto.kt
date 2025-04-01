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

@Schema(description = "Possible user registration statuses.", enumAsRef = true)
enum class OrganizationRegistrationStatusDto {
    /**
     * Organization that has been invited by another participant (?)
     */
    INVITED,

    /**
     * The participant admin for this organization needs to fill in missing data on login
     */
    ONBOARDING,

    /**
     * Self-registered organization that is waiting for approval
     */
    PENDING,


    /**
     * Organization that has been approved and is active
     */
    ACTIVE,

    /**
     * Organization that has been rejected
     */
    REJECTED
}

