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

import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.authorityportal.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedCatalog;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.ConnectorChangeTracker;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class ConnectorUpdateSuccessWriter {
    private final CrawlerEventLogger crawlerEventLogger;
    private final ConnectorUpdateCatalogWriter connectorUpdateCatalogWriter;
    private final DataOfferLimitsEnforcer dataOfferLimitsEnforcer;

    public void handleConnectorOnline(
            DSLContext dsl,
            ConnectorRef connectorRef,
            ConnectorRecord connector,
            FetchedCatalog catalog
    ) {
        // Limit data offers and log limitation if necessary
        var limitedDataOffers = dataOfferLimitsEnforcer.enforceLimits(catalog.getDataOffers());
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(dsl, connectorRef, connector, limitedDataOffers);

        // Log Status Change and set status to online if necessary
        if (connector.getOnlineStatus() != ConnectorOnlineStatus.ONLINE || connector.getLastRefreshAttemptAt() == null) {
            crawlerEventLogger.logConnectorOnline(dsl, connectorRef);
            connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        }

        // Track changes for final log message
        var changes = new ConnectorChangeTracker();
        var now = OffsetDateTime.now();
        connector.setLastSuccessfulRefreshAt(now);
        connector.setLastRefreshAttemptAt(now);
        connector.update();

        // Update data offers
        connectorUpdateCatalogWriter.updateDataOffers(
                dsl,
                connectorRef,
                limitedDataOffers.abbreviatedDataOffers(),
                changes
        );

        // Log event if changes are present
        if (!changes.isEmpty()) {
            crawlerEventLogger.logConnectorUpdated(dsl, connectorRef, changes);
        }
    }

}
