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

import de.sovity.authorityportal.api.model.catalog.CatalogDataOffer
import de.sovity.authorityportal.api.model.catalog.CatalogPageQuery
import de.sovity.authorityportal.api.model.catalog.CatalogPageResult
import de.sovity.authorityportal.api.model.catalog.CatalogPageSortingItem
import de.sovity.authorityportal.api.model.catalog.CatalogPageSortingType
import de.sovity.authorityportal.api.model.catalog.ConnectorOnlineStatusDto
import de.sovity.authorityportal.broker.dao.pages.catalog.CatalogQueryService
import de.sovity.authorityportal.broker.dao.pages.catalog.models.DataOfferListEntryRs
import de.sovity.authorityportal.broker.services.api.filtering.CatalogFilterService
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentService
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext
import java.util.stream.Stream

@ApplicationScoped
class CatalogApiService(
    val paginationMetadataUtils: PaginationMetadataUtils,
    val catalogQueryService: CatalogQueryService,
    val catalogFilterService: CatalogFilterService,
    val dsl: DSLContext,
    val deploymentEnvironmentService: DeploymentEnvironmentService,
) {

    fun catalogPage(environment: String, query: CatalogPageQuery): CatalogPageResult {
        val brokerConfig = deploymentEnvironmentService.findByIdOrThrow(environment).dataCatalog()
        val filters = catalogFilterService.getCatalogQueryFilters(query.filter)

        val pageQuery = paginationMetadataUtils.getPageQuery(
            query.pageOneBased,
            brokerConfig.catalogPagePageSize()
        )

        val availableSortings = buildAvailableSortings()
        var sorting = query.sorting
        if (sorting == null) {
            sorting = availableSortings[0].sorting
        }

        // execute db query
        val catalogPageRs = catalogQueryService.queryCatalogPage(
            environment = environment,
            searchQuery = query.searchQuery,
            filters = filters,
            sorting = sorting,
            pageQuery = pageQuery
        )

        val paginationMetadata = paginationMetadataUtils.buildPaginationMetadata(
            query.pageOneBased,
            brokerConfig.catalogPagePageSize(),
            catalogPageRs.dataOffers.size,
            catalogPageRs.numTotalDataOffers
        )

        return CatalogPageResult(
            availableSortings = availableSortings,
            paginationMetadata = paginationMetadata,
            availableFilters = catalogFilterService.buildAvailableFilters(catalogPageRs.availableFilterValues),
            dataOffers = buildCatalogDataOffers(catalogPageRs.dataOffers)
        )
    }

    private fun buildCatalogDataOffers(dataOfferRs: List<DataOfferListEntryRs>): List<CatalogDataOffer> {
        return dataOfferRs.map {
            buildCatalogDataOffer(it)
        }
    }

    private fun buildCatalogDataOffer(dataOfferRs: DataOfferListEntryRs): CatalogDataOffer = CatalogDataOffer(
        assetId = dataOfferRs.assetId,
        assetTitle = dataOfferRs.assetTitle,
        assetDataSourceAvailability = dataOfferRs.dataSourceAvailability,
        descriptionShortText = dataOfferRs.shortDescriptionNoMarkdown,
        version = dataOfferRs.version,
        keywords = dataOfferRs.keywords,
        connectorId = dataOfferRs.connectorId,
        organizationId = dataOfferRs.organizationId,
        organizationName = dataOfferRs.organizationName,
        connectorOnlineStatus = getOnlineStatus(dataOfferRs),
        connectorOfflineSinceOrLastUpdatedAt = dataOfferRs.connectorOfflineSinceOrLastUpdatedAt
    )

    private fun getOnlineStatus(dataOfferRs: DataOfferListEntryRs): ConnectorOnlineStatusDto {
        return when (dataOfferRs.connectorOnlineStatus) {
            ConnectorOnlineStatus.ONLINE -> ConnectorOnlineStatusDto.ONLINE
            ConnectorOnlineStatus.OFFLINE -> ConnectorOnlineStatusDto.OFFLINE
            ConnectorOnlineStatus.DEAD -> ConnectorOnlineStatusDto.DEAD
        }
    }

    companion object {
        private fun buildAvailableSortings(): List<CatalogPageSortingItem> {
            return Stream.of(
                CatalogPageSortingType.MOST_RECENT,
                CatalogPageSortingType.TITLE,
                CatalogPageSortingType.ORIGINATOR,
                CatalogPageSortingType.VIEW_COUNT
            ).map { it: CatalogPageSortingType -> CatalogPageSortingItem(it, it.title) }
                .toList()
        }
    }
}

