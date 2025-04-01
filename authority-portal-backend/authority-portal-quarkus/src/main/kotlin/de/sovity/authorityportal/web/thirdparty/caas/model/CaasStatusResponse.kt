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

@Schema(description = "Response of the status of a CaaS")
class CaasStatusResponse {
    @Schema(description = "The ID of the connector")
    var connectorId: String = ""
    @Schema(description = "Connector's Endpoint URL")
    var connectorEndpointUrl: String? = null
    @Schema(description = "Connector's Frontend URL")
    var frontendUrl: String? = null
    @Schema(description = "Connector's Management API URL")
    var managementApiUrl: String? = null
    @Schema(description = "Connector's JWKS URL")
    var connectorJwksUrl: String? = null
    @Schema(description = "Connector's status")
    var status: CaasStatusDto = CaasStatusDto.NOT_FOUND
    @Schema(description = "The end-of-life date and time of the connector")
    var endDateTime: OffsetDateTime? = null
}
