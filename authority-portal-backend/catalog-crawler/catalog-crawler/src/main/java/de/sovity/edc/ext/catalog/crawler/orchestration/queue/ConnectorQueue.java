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

package de.sovity.edc.ext.catalog.crawler.orchestration.queue;

import de.sovity.edc.ext.catalog.crawler.crawling.ConnectorCrawler;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class ConnectorQueue {
    private final ConnectorCrawler connectorCrawler;
    private final ThreadPool threadPool;

    /**
     * Enqueues connectors for update.
     *
     * @param connectorRefs connectors
     * @param priority priority from {@link ConnectorRefreshPriority}
     */
    public void addAll(Collection<ConnectorRef> connectorRefs, int priority) {
        var queued = threadPool.getQueuedConnectorRefs();
        connectorRefs = new ArrayList<>(connectorRefs);
        connectorRefs.removeIf(queued::contains);

        for (var connectorRef : connectorRefs) {
            threadPool.enqueueConnectorRefreshTask(
                    priority,
                    () -> connectorCrawler.crawlConnector(connectorRef),
                    connectorRef
            );
        }
    }
}
