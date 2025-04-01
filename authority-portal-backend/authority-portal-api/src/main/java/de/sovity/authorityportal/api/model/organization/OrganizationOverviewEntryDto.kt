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

@Schema(description = "Visible organization in organization overview page.")
data class OrganizationOverviewEntryDto(
    @field:Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val id: String,

    @field:Schema(description = "Legal Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:Schema(description = "Main Contact Email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val mainContactEmail: String?,

    @field:Schema(description = "Number of Users", requiredMode = Schema.RequiredMode.REQUIRED)
    val userCount: Int,

    @field:Schema(description = "Number of Connectors", requiredMode = Schema.RequiredMode.REQUIRED)
    val connectorCount: Int,

    @field:Schema(description = "Number of Data Offers", requiredMode = Schema.RequiredMode.REQUIRED)
    val dataOfferCount: Int,

    @field:Schema(description = "Number of Data Offers with a data source", requiredMode = Schema.RequiredMode.REQUIRED)
    val liveDataOfferCount: Int,

    @field:Schema(description = "Number of on-request Data Offers", requiredMode = Schema.RequiredMode.REQUIRED)
    val onRequestDataOfferCount: Int,

    @field:Schema(description = "Registration status", requiredMode = Schema.RequiredMode.REQUIRED)
    val registrationStatus: OrganizationRegistrationStatusDto,
)
