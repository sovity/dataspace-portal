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

package de.sovity.authorityportal.web.thirdparty.caas.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "Wrapper for retrieving the status of a connector")
class ConnectorStatusDetails {
    @Schema(description = "URL pointing to the connector API endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    var endpointUrl: String = ""

    @Schema(description = "URL pointing to the connector frontend", requiredMode = Schema.RequiredMode.REQUIRED)
    var frontendUrl: String = ""

    @Schema(description = "Connector status", requiredMode = Schema.RequiredMode.REQUIRED)
    var status: CaasStatusDto = CaasStatusDto.ERROR

    @Schema(description = "Date and time of the connector's start", requiredMode = Schema.RequiredMode.REQUIRED)
    var startedAt: OffsetDateTime = OffsetDateTime.MIN

    @Schema(description = "Date and time of the connector's shutdown", requiredMode = Schema.RequiredMode.REQUIRED)
    var endedAt: OffsetDateTime = OffsetDateTime.MIN
}
