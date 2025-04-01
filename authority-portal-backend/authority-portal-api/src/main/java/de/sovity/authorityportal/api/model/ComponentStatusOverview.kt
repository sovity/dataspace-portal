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

@Schema(description = "Status information for components and connectors.")
data class ComponentStatusOverview(
    @field:Schema(description = "DAPS Status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val dapsStatus: UptimeStatusDto?,
    @field:Schema(description = "Logging House Status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val loggingHouseStatus: UptimeStatusDto?,
    @field:Schema(description = "Catalog crawler (Broker) Status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val crawlerStatus: UptimeStatusDto?,
    @field:Schema(description = "Number of online connectors", requiredMode = Schema.RequiredMode.REQUIRED)
    val onlineConnectors: Int,
    @field:Schema(description = "Number of disturbed connectors", requiredMode = Schema.RequiredMode.REQUIRED)
    val disturbedConnectors: Int,
    @field:Schema(description = "Number of offline connectors", requiredMode = Schema.RequiredMode.REQUIRED)
    val offlineConnectors: Int,
)
