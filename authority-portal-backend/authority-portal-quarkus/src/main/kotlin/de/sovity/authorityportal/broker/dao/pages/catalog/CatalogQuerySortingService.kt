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
package de.sovity.authorityportal.broker.dao.pages.catalog

import de.sovity.authorityportal.api.model.catalog.CatalogPageSortingType
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.OrderField

@ApplicationScoped
class CatalogQuerySortingService {
    fun getOrderBy(fields: CatalogQueryFields, sorting: CatalogPageSortingType): List<OrderField<*>> {
        val orderBy = when (sorting) {
            CatalogPageSortingType.TITLE -> listOf<OrderField<*>>(
                fields.dataOfferTable.ASSET_TITLE.asc(),
                fields.connectorTable.CONNECTOR_ID.asc()
            )
            CatalogPageSortingType.MOST_RECENT -> listOf<OrderField<*>>(
                fields.dataOfferTable.CREATED_AT.desc(),
                fields.connectorTable.CONNECTOR_ID.asc()
            )
            CatalogPageSortingType.ORIGINATOR -> listOf<OrderField<*>>(
                fields.connectorTable.CONNECTOR_ID.asc(),
                fields.dataOfferTable.ASSET_TITLE.asc()
            )
            CatalogPageSortingType.VIEW_COUNT -> listOf<OrderField<*>>(
                fields.viewCount.desc(),
                fields.connectorTable.CONNECTOR_ID.asc()
            )
        }
        return orderBy
    }
}
