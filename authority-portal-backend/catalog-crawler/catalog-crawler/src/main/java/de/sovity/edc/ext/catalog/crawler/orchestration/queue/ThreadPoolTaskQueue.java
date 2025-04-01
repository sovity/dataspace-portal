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

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ThreadPoolTaskQueue {

    @Getter
    private final PriorityBlockingQueue<ThreadPoolTask> queue = new PriorityBlockingQueue<>(50, ThreadPoolTask.COMPARATOR);

    @SuppressWarnings("unchecked")
    public PriorityBlockingQueue<Runnable> getAsRunnableQueue() {
        return (PriorityBlockingQueue<Runnable>) (PriorityBlockingQueue<? extends Runnable>) queue;
    }

    public void add(ThreadPoolTask task) {
        queue.add(task);
    }

    public Set<ConnectorRef> getConnectorRefs() {
        var queuedRunnables = new ArrayList<>(queue);

        return queuedRunnables.stream()
                .map(ThreadPoolTask::getConnectorRef)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
