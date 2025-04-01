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

package de.sovity.edc.ext.catalog.crawler.crawling;

import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogCleaner;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorStatusUpdater;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class OfflineConnectorCleaner {
    private final CrawlerConfig crawlerConfig;
    private final ConnectorQueries connectorQueries;
    private final CrawlerEventLogger crawlerEventLogger;
    private final ConnectorStatusUpdater connectorStatusUpdater;
    private final CatalogCleaner catalogCleaner;

    public void cleanConnectorsIfOfflineTooLong(DSLContext dsl) {
        var killOfflineConnectorsAfter = crawlerConfig.getKillOfflineConnectorsAfter();
        var connectorsToKill = connectorQueries.findAllConnectorsForKilling(dsl, killOfflineConnectorsAfter);

        catalogCleaner.removeCatalogByConnectors(dsl, connectorsToKill);
        connectorStatusUpdater.markAsDead(dsl, connectorsToKill);

        crawlerEventLogger.addKilledDueToOfflineTooLongMessages(dsl, connectorsToKill);
    }
}
