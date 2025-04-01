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

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.ConnectorChangeTracker;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogPatchApplier;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;

import java.util.Collection;

@RequiredArgsConstructor
public class ConnectorUpdateCatalogWriter {
    private final CatalogPatchBuilder catalogPatchBuilder;
    private final CatalogPatchApplier catalogPatchApplier;

    /**
     * Updates a connector's data offers with given {@link FetchedDataOffer}s.
     *
     * @param dsl dsl
     * @param connectorRef connector
     * @param fetchedDataOffers fetched data offers
     * @param changes change tracker for log message
     */
    @SneakyThrows
    public void updateDataOffers(
            DSLContext dsl,
            ConnectorRef connectorRef,
            Collection<FetchedDataOffer> fetchedDataOffers,
            ConnectorChangeTracker changes
    ) {
        var patch = catalogPatchBuilder.buildDataOfferPatch(dsl, connectorRef, fetchedDataOffers);
        changes.setNumOffersAdded(patch.dataOffers().getInsertions().size());
        changes.setNumOffersUpdated(patch.dataOffers().getUpdates().size());
        changes.setNumOffersDeleted(patch.dataOffers().getDeletions().size());
        catalogPatchApplier.applyDbUpdatesBatched(dsl, patch);
    }
}
