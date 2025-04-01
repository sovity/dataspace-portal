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

package de.sovity.authorityportal.web.thirdparty.uptimekuma.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Information about the online status of Data Space components.")
class ComponentStatusOverview {
    @Schema(description = "DAPS status", requiredMode = Schema.RequiredMode.REQUIRED)
    var daps: ComponentStatus? = null
    @Schema(description = "Catalog crawler (Broker) status", requiredMode = Schema.RequiredMode.REQUIRED)
    var catalogCrawler: ComponentStatus? = null
    @Schema(description = "Logging House status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    var loggingHouse: ComponentStatus? = null
}
