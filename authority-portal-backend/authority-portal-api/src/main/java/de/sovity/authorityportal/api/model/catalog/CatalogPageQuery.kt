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

@Schema(description = "Filterable Catalog Page Query")
data class CatalogPageQuery(
    @field:Schema(description = "Selected filters", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val filter: CnfFilterValue?,

    @field:Schema(description = "Search query", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val searchQuery: String?,

    @field:Schema(description = "Sorting", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val sorting: CatalogPageSortingType?,

    @field:Schema(
        description = "Page number, one based, meaning the first page is page 1.",
        example = "1",
        defaultValue = "1",
        type = "n",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    val pageOneBased: Int = 1,
)

