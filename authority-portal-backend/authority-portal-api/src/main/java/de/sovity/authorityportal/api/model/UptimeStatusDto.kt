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

@Schema(description = "Uptime information for a single component.")
data class UptimeStatusDto(
    @field:Schema(description = "Status of the component", requiredMode = Schema.RequiredMode.REQUIRED)
    val componentStatus: ComponentStatusDto,
    @field:Schema(description = "Uptime in percent", requiredMode = Schema.RequiredMode.REQUIRED)
    val uptimePercentage: Double,
    @field:Schema(
        description = "Time span used for uptime percentage calculation",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val timeSpanSeconds: Long,
    @field:Schema(description = "Time span since last incident", requiredMode = Schema.RequiredMode.REQUIRED)
    val upSinceSeconds: Long,
)
