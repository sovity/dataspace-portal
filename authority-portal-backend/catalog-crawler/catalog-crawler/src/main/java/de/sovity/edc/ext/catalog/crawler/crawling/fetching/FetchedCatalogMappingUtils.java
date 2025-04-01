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

package de.sovity.edc.ext.catalog.crawler.crawling.fetching;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.utils.catalog.model.DspDataOffer;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class FetchedCatalogMappingUtils {
    private final PolicyMapper policyMapper;
    private final AssetMapper assetMapper;
    private final ObjectMapper objectMapper;

    public UiAsset buildUiAsset(
            DspDataOffer dspDataOffer,
            ConnectorRef connectorRef
    ) {
        var assetJsonLd = assetMapper.buildAssetJsonLdFromDatasetProperties(dspDataOffer.getAssetPropertiesJsonLd());
        var asset = assetMapper.buildAsset(assetJsonLd);
        var uiAsset = assetMapper.buildUiAsset(asset, connectorRef.getEndpoint(), connectorRef.getConnectorId());
        uiAsset.setCreatorOrganizationName(connectorRef.getOrganizationLegalName());
        uiAsset.setParticipantId(connectorRef.getConnectorId());
        return uiAsset;
    }

    @SneakyThrows
    public String buildUiAssetJson(UiAsset uiAsset) {
        return objectMapper.writeValueAsString(uiAsset);
    }

    @SneakyThrows
    public String buildUiPolicyJson(JsonObject policyJsonLd) {
        var policy = policyMapper.buildPolicy(policyJsonLd);
        var uiPolicy = policyMapper.buildUiPolicy(policy);
        return objectMapper.writeValueAsString(uiPolicy);
    }
}
