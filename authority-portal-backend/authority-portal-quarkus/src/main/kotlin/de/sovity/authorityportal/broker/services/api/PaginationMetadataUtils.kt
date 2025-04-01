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
package de.sovity.authorityportal.broker.services.api

import de.sovity.authorityportal.api.model.catalog.PaginationMetadata
import de.sovity.authorityportal.broker.dao.pages.catalog.models.PageQuery
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaginationMetadataUtils {
    fun buildDummyPaginationMetadata(numResults: Int): PaginationMetadata {
        return PaginationMetadata(
            numTotal = numResults,
            numVisible = numResults,
            pageOneBased = 1,
            pageSize = numResults
        )
    }

    fun getPageQuery(pageOneBased: Int?, pageSize: Int): PageQuery {
        val pageZeroBased = getPageZeroBased(pageOneBased)
        val offset = pageZeroBased * pageSize
        return PageQuery(offset, pageSize)
    }

    fun buildPaginationMetadata(
        pageOneBased: Int?,
        pageSize: Int,
        numVisible: Int,
        numTotalResults: Int
    ): PaginationMetadata {
        val pageZeroBased = getPageZeroBased(pageOneBased)

        return PaginationMetadata(
            numTotal = numTotalResults,
            numVisible = numVisible,
            pageOneBased = pageZeroBased + 1,
            pageSize = pageSize
        )
    }

    private fun getPageZeroBased(pageOneBased: Int?): Int {
        return if (pageOneBased == null) 0 else (pageOneBased - 1)
    }
}
