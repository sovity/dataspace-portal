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
import java.time.OffsetDateTime

@Schema(description = "Response DTO for connector registration")
data class CreateConnectorResponse(
    @field:Schema(description = "ID of the connector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val id: String?,

    @field:Schema(description = "Date and time of response", requiredMode = Schema.RequiredMode.REQUIRED)
    val changedDate: OffsetDateTime,

    @field:Schema(
        description = "Message status. Informs what type of action is necessary",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val status: CreateConnectorStatusDto,

    @field:Schema(description = "Optional error message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val message: String?,

    @field:Schema(description = "Connector's generated client ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val clientId: String?
) {

    companion object {
        fun ok(connectorId: String, clientId: String, changedTime: OffsetDateTime): CreateConnectorResponse {
            return CreateConnectorResponse(connectorId, changedTime, CreateConnectorStatusDto.OK, null, clientId)
        }

        fun error(message: String, changedTime: OffsetDateTime): CreateConnectorResponse {
            return CreateConnectorResponse(null, changedTime, CreateConnectorStatusDto.ERROR, message, null)
        }

        fun warning(connectorId: String, message: String, clientId: String, changedTime: OffsetDateTime): CreateConnectorResponse {
            return CreateConnectorResponse(connectorId, changedTime, CreateConnectorStatusDto.WARNING, message, clientId)
        }
    }
}
