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
package de.sovity.authorityportal.api.model.catalog

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Pagination Metadata")
data class PaginationMetadata(
    @field:Schema(
        description = "Total number of results",
        example = "368",
        type = "n",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val numTotal: Int,

    @field:Schema(
        description = "Visible number of results",
        example = "20",
        type = "n",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val numVisible: Int,

    @field:Schema(
        description = "Page number, one based, meaning the first page is page 1.",
        example = "1",
        defaultValue = "1",
        type = "n",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val pageOneBased: Int,

    @field:Schema(description = "Items per page", example = "20", type = "n", requiredMode = Schema.RequiredMode.REQUIRED)
    val pageSize: Int,
)
