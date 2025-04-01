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
import de.sovity.authorityportal.broker.dao.pages.catalog.models.DataOfferListEntryRs
import de.sovity.authorityportal.broker.dao.pages.catalog.models.PageQuery
import de.sovity.authorityportal.broker.dao.utils.MultisetUtils
import de.sovity.authorityportal.broker.services.api.filtering.model.FilterAttributeApplied
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.Field
import org.jooq.impl.DSL

@ApplicationScoped
class CatalogQueryDataOfferFetcher(
    val catalogQuerySortingService: CatalogQuerySortingService,
    val catalogQueryFilterService: CatalogQueryFilterService,
    val catalogQueryContractOfferFetcher: CatalogQueryContractOfferFetcher
) {
    /**
     * Query data offers
     *
     * @param fields      query fields
     * @param searchQuery search query
     * @param filters     filters (queries + filter clauses)
     * @param sorting     sorting
     * @param pageQuery   pagination
     * @return [Field] of [DataOfferListEntryRs]s
     */
    fun queryDataOffers(
        environment: String,
        fields: CatalogQueryFields,
        searchQuery: String?,
        filters: List<FilterAttributeApplied>,
        sorting: CatalogPageSortingType,
        pageQuery: PageQuery
    ): Field<List<DataOfferListEntryRs>> {
        val c = fields.connectorTable
        val d = fields.dataOfferTable
        val org = fields.organizationTable

        val query = DSL.select(
            d.ASSET_ID.`as`("assetId"),
            d.ASSET_TITLE.`as`("assetTitle"),
            fields.dataSourceAvailability.`as`("dataSourceAvailability"),
            d.SHORT_DESCRIPTION_NO_MARKDOWN.`as`("shortDescriptionNoMarkdown"),
            d.VERSION.`as`("version"),
            d.KEYWORDS.`as`("keywords"),
            c.CONNECTOR_ID.`as`("connectorId"),
            c.ORGANIZATION_ID.`as`("organizationId"),
            org.NAME.`as`("organizationName"),
            c.ONLINE_STATUS.`as`("connectorOnlineStatus"),
            fields.offlineSinceOrLastUpdatedAt.`as`("connectorOfflineSinceOrLastUpdatedAt"),
            c.ENDPOINT_URL.`as`("connectorEndpointUrl"),
            d.CREATED_AT,
            d.UPDATED_AT
        )
            .fromCatalogQueryTables(fields)
            .where(catalogQueryFilterService.filterDbQuery(environment, fields, searchQuery, filters))
            .orderBy(catalogQuerySortingService.getOrderBy(fields, sorting))
            .limit(pageQuery.offset, pageQuery.limit)

        return MultisetUtils.multiset(query, DataOfferListEntryRs::class)
    }

    /**
     * Query number of data offers
     *
     * @param fields      query fields
     * @param searchQuery search query
     * @param filters     filters (queries + filter clauses)
     * @return [Field] with number of data offers
     */
    fun queryNumDataOffers(
        environment: String,
        fields: CatalogQueryFields,
        searchQuery: String?,
        filters: List<FilterAttributeApplied>
    ): Field<Int> {
        val query = DSL.select(DSL.count())
            .fromCatalogQueryTables(fields)
            .where(catalogQueryFilterService.filterDbQuery(environment, fields, searchQuery, filters))
        return DSL.field(query)
    }

}
