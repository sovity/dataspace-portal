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

@Schema(description = "Connector deployment environment.")
data class DeploymentEnvironmentDto(
    @field:Schema(description = "Deployment environment ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val environmentId: String,
    @field:Schema(description = "Environment localized name", requiredMode = Schema.RequiredMode.REQUIRED)
    val title: String,
    @field:Schema(description = "DAPS JWKS URL", requiredMode = Schema.RequiredMode.REQUIRED)
    val dapsJwksUrl: String,
    @field:Schema(description = "DAPS Token URL", requiredMode = Schema.RequiredMode.REQUIRED)
    val dapsTokenUrl: String,
    @field:Schema(description = "Logging House URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val loggingHouseUrl: String?,
)
