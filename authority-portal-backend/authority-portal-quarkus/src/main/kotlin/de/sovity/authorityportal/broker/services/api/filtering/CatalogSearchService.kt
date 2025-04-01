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
package de.sovity.authorityportal.broker.services.api.filtering

import de.sovity.authorityportal.broker.dao.pages.catalog.CatalogQueryFields
import de.sovity.authorityportal.broker.dao.utils.SearchUtils.simpleSearch
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.Condition

@ApplicationScoped
class CatalogSearchService {
    fun filterBySearch(fields: CatalogQueryFields, searchQuery: String?): Condition {
        return simpleSearch(
            searchQuery, listOf(
                fields.dataOfferTable.ASSET_ID,
                fields.dataOfferTable.ASSET_TITLE,
                fields.dataOfferTable.DATA_CATEGORY,
                fields.dataOfferTable.DATA_SUBCATEGORY,
                fields.dataOfferTable.DESCRIPTION_NO_MARKDOWN,
                fields.dataOfferTable.KEYWORDS_COMMA_JOINED,
                fields.connectorTable.ENDPOINT_URL,
                fields.connectorTable.ORGANIZATION_ID,
                fields.organizationTable.NAME
            )
        )
    }
}
