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

@Schema(description = "Information for registering a new central dataspace component.")
data class CentralComponentCreateRequest(
    @field:Schema(description = "Component Name", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotBlank(message = "Name of component cannot be blank")
    val name: String,

    @field:Schema(description = "Home Page URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val homepageUrl: String?,

    @field:Schema(description = "Endpoint URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotBlank(message = "Endpoint URL cannot be blank")
    val endpointUrl:  String,

    @field:Schema(description = "The component's certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotBlank(message = "Certificate cannot be blank")
    val certificate: String
)
