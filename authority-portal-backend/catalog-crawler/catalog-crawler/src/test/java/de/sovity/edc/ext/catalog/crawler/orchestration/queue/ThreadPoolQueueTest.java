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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

class ThreadPoolQueueTest {


    /**
     * Regression against bug where the queue did not act like a queue.
     */
    @Test
    void testOrdering() {
        Runnable noop = () -> {
        };

        var c10 = mock(ConnectorRef.class);
        var c20 = mock(ConnectorRef.class);
        var c11 = mock(ConnectorRef.class);
        var c21 = mock(ConnectorRef.class);
        var c00 = mock(ConnectorRef.class);

        var queue = new ThreadPoolTaskQueue();
        queue.add(new ThreadPoolTask(1, noop, c10));
        queue.add(new ThreadPoolTask(2, noop, c20));
        queue.add(new ThreadPoolTask(1, noop, c11));
        queue.add(new ThreadPoolTask(2, noop, c21));
        queue.add(new ThreadPoolTask(0, noop, c00));

        var result = new ArrayList<ThreadPoolTask>();
        queue.getQueue().drainTo(result);

        Assertions.assertThat(result).extracting(ThreadPoolTask::getConnectorRef)
                .containsExactly(c00, c10, c11, c20, c21);
    }
}
