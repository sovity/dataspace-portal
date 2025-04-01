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
package de.sovity.authorityportal.broker.dao.pages.dataoffer

import de.sovity.authorityportal.broker.dao.pages.catalog.CatalogQueryContractOfferFetcher
import de.sovity.authorityportal.broker.dao.pages.catalog.CatalogQueryFields
import de.sovity.authorityportal.broker.dao.pages.catalog.fromCatalogQueryTables
import de.sovity.authorityportal.broker.dao.pages.dataoffer.model.DataOfferDetailRs
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.web.environment.CatalogDataspaceConfigService
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext

@ApplicationScoped
class DataOfferDetailPageQueryService(
    val catalogQueryContractOfferFetcher: CatalogQueryContractOfferFetcher,
    val catalogDataspaceConfigService: CatalogDataspaceConfigService,
    val dsl: DSLContext
) {
    fun queryDataOfferDetailsPage(environment: String, assetId: String, connectorId: String): DataOfferDetailRs? {
        // We are re-using the catalog page query stuff as long as we can get away with it
        val fields = CatalogQueryFields(
            Tables.CONNECTOR,
            Tables.DATA_OFFER,
            Tables.ORGANIZATION,
            Tables.DATA_OFFER_VIEW_COUNT,
            catalogDataspaceConfigService.forEnvironment(environment)
        )

        val d = fields.dataOfferTable
        val c = fields.connectorTable
        val org = fields.organizationTable

        return dsl.select(
            d.ASSET_ID,
            d.ASSET_TITLE,
            c.CONNECTOR_ID.`as`("connectorId"),
            c.ENDPOINT_URL.`as`("connectorEndpoint"),
            org.NAME.`as`("organizationName"),
            c.ORGANIZATION_ID.`as`("organizationId"),
            c.ONLINE_STATUS.`as`("connectorOnlineStatus"),
            fields.offlineSinceOrLastUpdatedAt.`as`("connectorOfflineSinceOrLastUpdatedAt"),
            d.CREATED_AT,
            d.UPDATED_AT,
            d.UI_ASSET_JSON.`as`("assetUiJson"),
            catalogQueryContractOfferFetcher.getContractOffers(fields.dataOfferTable).`as`("contractOffers"),
            fields.viewCount.`as`("viewCount")
        )
            .fromCatalogQueryTables(fields)
            .where(
                d.ASSET_ID.eq(assetId),
                d.CONNECTOR_ID.eq(connectorId),
                c.ENVIRONMENT.eq(environment)
            )
            .fetchOneInto(DataOfferDetailRs::class.java)
    }
}
