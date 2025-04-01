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

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedCatalog;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedContractOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.utils.catalog.model.DspCatalog;
import de.sovity.edc.utils.catalog.model.DspContractOffer;
import de.sovity.edc.utils.catalog.model.DspDataOffer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class FetchedCatalogBuilder {
    private final FetchedCatalogMappingUtils fetchedCatalogMappingUtils;

    public FetchedCatalog buildFetchedCatalog(DspCatalog catalog, ConnectorRef connectorRef) {
        assertEqualEndpoint(catalog, connectorRef);
        assertEqualParticipantId(catalog, connectorRef);

        var fetchedDataOffers = catalog.getDataOffers().stream()
                .map(dspDataOffer -> buildFetchedDataOffer(dspDataOffer, connectorRef))
                .toList();

        var fetchedCatalog = new FetchedCatalog();
        fetchedCatalog.setConnectorRef(connectorRef);
        fetchedCatalog.setDataOffers(fetchedDataOffers);
        return fetchedCatalog;
    }

    private void assertEqualParticipantId(DspCatalog catalog, ConnectorRef connectorRef) {
        Validate.isTrue(
                connectorRef.getConnectorId().equals(catalog.getParticipantId()),
                String.format(
                        "Connector connectorId does not match the participantId: connectorId %s, participantId %s",
                        connectorRef.getConnectorId(),
                        catalog.getParticipantId()
                )
        );
    }

    private void assertEqualEndpoint(DspCatalog catalog, ConnectorRef connectorRef) {
        Validate.isTrue(
                connectorRef.getEndpoint().equals(catalog.getEndpoint()),
                String.format(
                        "Connector endpoint mismatch: expected %s, got %s",
                        connectorRef.getEndpoint(),
                        catalog.getEndpoint()
                )
        );
    }

    @NotNull
    private FetchedDataOffer buildFetchedDataOffer(
            DspDataOffer dspDataOffer,
            ConnectorRef connectorRef
    ) {
        var uiAsset = fetchedCatalogMappingUtils.buildUiAsset(dspDataOffer, connectorRef);
        var uiAssetJson = fetchedCatalogMappingUtils.buildUiAssetJson(uiAsset);

        var fetchedDataOffer = new FetchedDataOffer();
        fetchedDataOffer.setAssetId(uiAsset.getAssetId());
        fetchedDataOffer.setUiAsset(uiAsset);
        fetchedDataOffer.setUiAssetJson(uiAssetJson);
        fetchedDataOffer.setContractOffers(buildFetchedContractOffers(dspDataOffer.getContractOffers()));
        return fetchedDataOffer;
    }

    @NotNull
    private List<FetchedContractOffer> buildFetchedContractOffers(List<DspContractOffer> offers) {
        return offers.stream()
                .map(this::buildFetchedContractOffer)
                .toList();
    }

    @NotNull
    private FetchedContractOffer buildFetchedContractOffer(DspContractOffer offer) {
        var uiPolicyJson = fetchedCatalogMappingUtils.buildUiPolicyJson(offer.getPolicyJsonLd());
        var contractOffer = new FetchedContractOffer();
        contractOffer.setContractOfferId(offer.getContractOfferId());
        contractOffer.setUiPolicyJson(uiPolicyJson);
        return contractOffer;
    }

}
