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

import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private final ThreadPoolTaskQueue queue;

    private final boolean enabled;
    private final ThreadPoolExecutor threadPoolExecutor;

    public ThreadPool(ThreadPoolTaskQueue queue, CrawlerConfig crawlerConfig, Monitor monitor) {
        this.queue = queue;
        int numThreads = crawlerConfig.getNumThreads();
        enabled = numThreads > 0;

        if (enabled) {
            monitor.info("Initializing ThreadPoolExecutor with %d threads.".formatted(numThreads));
            threadPoolExecutor = new ThreadPoolExecutor(
                    numThreads,
                    numThreads,
                    60,
                    TimeUnit.SECONDS,
                    queue.getAsRunnableQueue()
            );
            threadPoolExecutor.prestartAllCoreThreads();
        } else {
            monitor.info("Skipped ThreadPoolExecutor initialization.");
            threadPoolExecutor = null;
        }
    }

    public void enqueueConnectorRefreshTask(int priority, Runnable runnable, ConnectorRef connectorRef) {
        enqueueTask(new ThreadPoolTask(priority, runnable, connectorRef));
    }

    public Set<ConnectorRef> getQueuedConnectorRefs() {
        return queue.getConnectorRefs();
    }

    private void enqueueTask(ThreadPoolTask task) {
        if (enabled) {
            threadPoolExecutor.execute(task);
        } else {
            // Only relevant for test environment, where execution is disabled
            queue.add(task);
        }
    }
}
