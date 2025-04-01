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
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventErrorMessage;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class ConnectorUpdateFailureWriter {
    private final CrawlerEventLogger crawlerEventLogger;
    private final Monitor monitor;

    public void handleConnectorOffline(
            DSLContext dsl,
            ConnectorRef connectorRef,
            ConnectorRecord connector,
            Throwable e
    ) {
        // Log Status Change and set status to offline if necessary
        if (connector.getOnlineStatus() == ConnectorOnlineStatus.ONLINE || connector.getLastRefreshAttemptAt() == null) {
            monitor.info("Connector is offline: " + connector.getEndpointUrl(), e);
            crawlerEventLogger.logConnectorOffline(dsl, connectorRef, getFailureMessage(e));
            connector.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
        }

        connector.setLastRefreshAttemptAt(OffsetDateTime.now());
        connector.update();
    }

    public CrawlerEventErrorMessage getFailureMessage(Throwable e) {
        return CrawlerEventErrorMessage.ofStackTrace("Unexpected exception during connector update.", e);
    }
}
