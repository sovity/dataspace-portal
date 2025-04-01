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

import de.sovity.authorityportal.db.jooq.enums.MeasurementErrorStatus;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogService;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerExecutionTimeLogger;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateFailureWriter;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateSuccessWriter;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.concurrent.TimeUnit;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class ConnectorCrawler {
    private final FetchedCatalogService fetchedCatalogService;
    private final ConnectorUpdateSuccessWriter connectorUpdateSuccessWriter;
    private final ConnectorUpdateFailureWriter connectorUpdateFailureWriter;
    private final ConnectorQueries connectorQueries;
    private final DslContextFactory dslContextFactory;
    private final Monitor monitor;
    private final CrawlerExecutionTimeLogger crawlerExecutionTimeLogger;

    /**
     * Updates single connector.
     *
     * @param connectorRef connector
     */
    public void crawlConnector(ConnectorRef connectorRef) {
        var executionTime = StopWatch.createStarted();
        var failed = false;

        try {
            monitor.info("Updating connector: " + connectorRef);

            var catalog = fetchedCatalogService.fetchCatalog(connectorRef);

            // Update connector in a single transaction
            dslContextFactory.transaction(dsl -> {
                var connectorRecord = connectorQueries.findByConnectorId(dsl, connectorRef.getConnectorId());
                connectorUpdateSuccessWriter.handleConnectorOnline(dsl, connectorRef, connectorRecord, catalog);
            });
        } catch (Exception e) {
            failed = true;
            try {
                // Update connector in a single transaction
                dslContextFactory.transaction(dsl -> {
                    var connectorRecord = connectorQueries.findByConnectorId(dsl, connectorRef.getConnectorId());
                    connectorUpdateFailureWriter.handleConnectorOffline(dsl, connectorRef, connectorRecord, e);
                });
            } catch (Exception e1) {
                e1.addSuppressed(e);
                monitor.severe("Failed updating connector as failed.", e1);
            }
        } finally {
            executionTime.stop();
            try {
                var status = failed ? MeasurementErrorStatus.ERROR : MeasurementErrorStatus.OK;
                dslContextFactory.transaction(dsl -> crawlerExecutionTimeLogger.logExecutionTime(
                        dsl,
                        connectorRef,
                        executionTime.getTime(TimeUnit.MILLISECONDS),
                        status
                ));
            } catch (Exception e) {
                monitor.severe("Failed logging connector update execution time.", e);
            }
        }
    }
}
