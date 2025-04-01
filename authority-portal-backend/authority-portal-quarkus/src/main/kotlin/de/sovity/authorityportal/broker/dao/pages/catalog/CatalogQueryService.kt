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
import de.sovity.authorityportal.broker.dao.pages.catalog.models.CatalogPageRs
import de.sovity.authorityportal.broker.dao.pages.catalog.models.PageQuery
import de.sovity.authorityportal.broker.services.api.filtering.model.FilterAttributeApplied
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.web.environment.CatalogDataspaceConfigService
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext

@ApplicationScoped
class CatalogQueryService(
    val catalogQueryDataOfferFetcher: CatalogQueryDataOfferFetcher,
    val catalogQueryAvailableFilterFetcher: CatalogQueryAvailableFilterFetcher,
    val catalogDataspaceConfigService: CatalogDataspaceConfigService,
    val dsl: DSLContext
) {
    /**
     * Query all data required for the catalog page
     *
     * @param dsl         transaction
     * @param searchQuery search query
     * @param filters     filters (queries + filter clauses)
     * @param sorting     sorting
     * @param pageQuery   pagination
     * @return [CatalogPageRs]
     */
    fun queryCatalogPage(
        environment: String,
        searchQuery: String?,
        filters: List<FilterAttributeApplied>,
        sorting: CatalogPageSortingType,
        pageQuery: PageQuery
    ): CatalogPageRs {
        val fields = CatalogQueryFields(
            Tables.CONNECTOR,
            Tables.DATA_OFFER,
            Tables.ORGANIZATION,
            Tables.DATA_OFFER_VIEW_COUNT,
            catalogDataspaceConfigService.forEnvironment(environment)
        )

        val availableFilterValues = catalogQueryAvailableFilterFetcher
            .queryAvailableFilterValues(environment, fields, searchQuery, filters)

        val dataOffers = catalogQueryDataOfferFetcher.queryDataOffers(
            environment, fields, searchQuery, filters, sorting, pageQuery
        )

        val numTotalDataOffers =
            catalogQueryDataOfferFetcher.queryNumDataOffers(environment, fields, searchQuery, filters)

        return dsl.select(
            dataOffers.`as`("dataOffers"),
            availableFilterValues.`as`("availableFilterValues"),
            numTotalDataOffers.`as`("numTotalDataOffers")
        ).fetchOneInto(CatalogPageRs::class.java)!!
    }
}
