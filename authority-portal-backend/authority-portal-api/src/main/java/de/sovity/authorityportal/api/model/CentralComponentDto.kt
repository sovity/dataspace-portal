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

@Schema(description = "Information of a central dataspace component.")
data class CentralComponentDto (
    @field:Schema(description = "Central Component ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val centralComponentId: String,

    @field:Schema(description = "Component Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:Schema(description = "Home Page URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val homepageUrl: String?,

    @field:Schema(description = "Endpoint URL", requiredMode = Schema.RequiredMode.REQUIRED)
    val endpointUrl: String,

    @field:Schema(description = "Environment ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val environmentId: String,

    @field:Schema(description = "Created By Full Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val createdByUserFullName: String,

    @field:Schema(description = "Created By Organization Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val createdByOrgName: String,

    @field:Schema(description = "Created By Organization ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val createdByOrganizationId: String,
)
