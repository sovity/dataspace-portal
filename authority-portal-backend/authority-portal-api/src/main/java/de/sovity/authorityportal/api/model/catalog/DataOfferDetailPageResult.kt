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

import de.sovity.edc.ce.api.common.model.UiAsset
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "Data Offer Detail Page.")
data class DataOfferDetailPageResult(
    @field:Schema(description = "ID of asset", requiredMode = Schema.RequiredMode.REQUIRED)
    val assetId: String,

    @field:Schema(description = "Title of asset", requiredMode = Schema.RequiredMode.REQUIRED)
    val assetTitle: String,

    @field:Schema(
        description = "Connector ID",
        example = "BPNL1234XX.C1234XX",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val connectorId: String,

    @field:Schema(
        description = "Connector Endpoint",
        example = "https://my-test.connector/api/v1/dsp",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val connectorEndpoint: String,

    @field:Schema(
        description = "Organization Name",
        example = "sovity GmbH",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val organizationName: String,

    @field:Schema(
        description = "Organization ID",
        example = "BPNL1234XX",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val organizationId: String,

    @field:Schema(description = "Connector Online Status", requiredMode = Schema.RequiredMode.REQUIRED)
    val connectorOnlineStatus: ConnectorOnlineStatusDto,

    @field:Schema(
        description = "Date to be displayed as last update date, for online connectors it's the " +
            "last refresh date, for offline connectors it's the creation date or last successful fetch.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val connectorOfflineSinceOrLastUpdatedAt: OffsetDateTime,

    @field:Schema(description = "Creation date in Broker", requiredMode = Schema.RequiredMode.REQUIRED)
    val createdAt: OffsetDateTime,

    @field:Schema(description = "Update date in Broker", requiredMode = Schema.RequiredMode.REQUIRED)
    val updatedAt: OffsetDateTime,

    @field:Schema(description = "Asset properties", requiredMode = Schema.RequiredMode.REQUIRED)
    val asset: UiAsset,

    @field:Schema(description = "Available Contract Offers", requiredMode = Schema.RequiredMode.REQUIRED)
    val contractOffers: List<DataOfferDetailContractOffer>,

    @field:Schema(description = "View Count", requiredMode = Schema.RequiredMode.REQUIRED)
    val viewCount: Int,
)
