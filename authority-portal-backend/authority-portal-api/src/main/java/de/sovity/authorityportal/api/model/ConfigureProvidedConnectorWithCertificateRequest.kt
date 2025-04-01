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
import jakarta.validation.constraints.NotBlank

@Schema(description = "Request data for configuring SP Connectors")
data class ConfigureProvidedConnectorWithCertificateRequest(
    @field:NotBlank(message = "URL of the connector frontend cannot be blank")
    @field:Schema(description = "URL of the connector frontend", requiredMode = Schema.RequiredMode.REQUIRED)
    var frontendUrl: String,

    @field:NotBlank(message = "URL of the connector endpoint cannot be blank")
    @field:Schema(description = "URL of the connector endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    var endpointUrl: String,

    @field:NotBlank(message = "URL of the connector management endpoint cannot be blank")
    @field:Schema(description = "URL of the connector management endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    var managementUrl: String,

    @field:NotBlank(message = "Public key of certificate cannot be blank")
    @field:Schema(description = "Public key of certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    val certificate: String,
)
