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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;


@Getter
@RequiredArgsConstructor
public class ThreadPoolTask implements Runnable {

    public static final Comparator<ThreadPoolTask> COMPARATOR = Comparator.comparing(ThreadPoolTask::getPriority)
            .thenComparing(ThreadPoolTask::getSequence);

    /**
     * {@link java.util.concurrent.PriorityBlockingQueue} does not guarantee sequential execution, so we need to add this.
     */
    private static final AtomicLong SEQ = new AtomicLong(0);
    private final long sequence = SEQ.incrementAndGet();
    private final int priority;
    private final Runnable task;
    private final ConnectorRef connectorRef;

    @Override
    public void run() {
        this.task.run();
    }
}
