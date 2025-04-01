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

@Schema(description = "Detailed information about a deployed connector.")
data class ConnectorDetailsDto(
    @field:Schema(description = "Connector ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val connectorId: String,
    @field:Schema(description = "Type", requiredMode = Schema.RequiredMode.REQUIRED)
    val type: ConnectorTypeDto,
    @field:Schema(description = "Owning organization (name)", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationName: String,
    @field:Schema(description = "Owning organization (ID)", requiredMode = Schema.RequiredMode.REQUIRED)
    val organizationId: String,
    @field:Schema(description = "Hosting organization (name)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val hostOrganizationName: String?,
    @field:Schema(description = "Hosting organization (ID)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val hostOrganizationId: String?,
    @field:Schema(description = "Deployment Environment", requiredMode = Schema.RequiredMode.REQUIRED)
    val environment: DeploymentEnvironmentDto,
    @field:Schema(description = "Connector Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val connectorName: String,
    @field:Schema(description = "Location", requiredMode = Schema.RequiredMode.REQUIRED)
    val location: String,
    @field:Schema(description = "Frontend URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val frontendUrl: String?,
    @field:Schema(description = "Endpoint URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val endpointUrl: String?,
    @field:Schema(description = "Management URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val managementUrl: String?,
    @field:Schema(description = "Connector status", requiredMode = Schema.RequiredMode.REQUIRED)
    val status: ConnectorStatusDto,
    @field:Schema(description = "Client ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val clientId: String
)
