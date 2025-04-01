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

import de.sovity.authorityportal.api.model.catalog.ConnectorOnlineStatusDto
import de.sovity.authorityportal.api.model.catalog.DataOfferDetailContractOffer
import de.sovity.authorityportal.api.model.catalog.DataOfferDetailPageQuery
import de.sovity.authorityportal.api.model.catalog.DataOfferDetailPageResult
import de.sovity.authorityportal.broker.dao.pages.dataoffer.DataOfferDetailPageQueryService
import de.sovity.authorityportal.broker.dao.pages.dataoffer.ViewCountLogger
import de.sovity.authorityportal.broker.dao.pages.dataoffer.model.ContractOfferRs
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus
import de.sovity.authorityportal.web.utils.notFound
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext

@ApplicationScoped
class DataOfferDetailApiService(
    val dataOfferDetailPageQueryService: DataOfferDetailPageQueryService,
    val viewCountLogger: ViewCountLogger,
    val dataOfferMapper: DataOfferMapper,
    val dsl: DSLContext
) {
    fun dataOfferDetailPage(environment: String, query: DataOfferDetailPageQuery): DataOfferDetailPageResult {
        val dataOffer = dataOfferDetailPageQueryService
            .queryDataOfferDetailsPage(environment, query.assetId, query.connectorId)
            ?: notFound("Data offer not found.")

        val asset = dataOfferMapper.readUiAsset(dataOffer.assetUiJson)
        viewCountLogger.increaseDataOfferViewCount(query.assetId, query.connectorId)

        return DataOfferDetailPageResult(
            assetId = dataOffer.assetId,
            assetTitle = dataOffer.assetTitle,
            connectorId = dataOffer.connectorId,
            connectorEndpoint = dataOffer.connectorEndpoint,
            organizationName = dataOffer.organizationName,
            organizationId = dataOffer.organizationId,
            connectorOnlineStatus = mapConnectorOnlineStatus(dataOffer.connectorOnlineStatus),
            connectorOfflineSinceOrLastUpdatedAt = dataOffer.connectorOfflineSinceOrLastUpdatedAt,
            createdAt = dataOffer.createdAt,
            updatedAt = dataOffer.updatedAt,
            asset = asset,
            contractOffers = buildDataOfferDetailContractOffers(dataOffer.contractOffers),
            viewCount = dataOffer.viewCount
        )
    }

    private fun mapConnectorOnlineStatus(connectorOnlineStatus: ConnectorOnlineStatus?): ConnectorOnlineStatusDto {
        if (connectorOnlineStatus == null) {
            return ConnectorOnlineStatusDto.OFFLINE
        }

        return when (connectorOnlineStatus) {
            ConnectorOnlineStatus.ONLINE -> ConnectorOnlineStatusDto.ONLINE
            ConnectorOnlineStatus.OFFLINE -> ConnectorOnlineStatusDto.OFFLINE
            ConnectorOnlineStatus.DEAD -> ConnectorOnlineStatusDto.DEAD
        }
    }

    private fun buildDataOfferDetailContractOffers(contractOffers: List<ContractOfferRs>): List<DataOfferDetailContractOffer> {
        return contractOffers.map { buildDataOfferDetailContractOffer(it) }
    }

    private fun buildDataOfferDetailContractOffer(offer: ContractOfferRs): DataOfferDetailContractOffer {
        return DataOfferDetailContractOffer(
            createdAt = offer.createdAt,
            updatedAt = offer.updatedAt,
            contractOfferId = offer.contractOfferId,
            contractPolicy = dataOfferMapper.readUiPolicy(offer.policyUiJson)
        )

    }
}
