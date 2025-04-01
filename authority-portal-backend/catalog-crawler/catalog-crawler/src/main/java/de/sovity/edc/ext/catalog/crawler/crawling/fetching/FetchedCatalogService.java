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
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.utils.catalog.DspCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;

@RequiredArgsConstructor
public class FetchedCatalogService {
    private final DspCatalogService dspCatalogService;
    private final FetchedCatalogBuilder catalogPatchBuilder;

    /**
     * Fetches {@link ContractOffer}s and de-duplicates them into {@link FetchedDataOffer}s.
     *
     * @param connectorRef connector
     * @return updated connector db row
     */
    @SneakyThrows
    public FetchedCatalog fetchCatalog(ConnectorRef connectorRef) {
        var dspCatalog = dspCatalogService.fetchDataOffers(connectorRef.getEndpoint());
        return catalogPatchBuilder.buildFetchedCatalog(dspCatalog, connectorRef);
    }
}
