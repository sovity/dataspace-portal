/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.catalog.crawler.orchestration.schedules;

import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ConnectorQueueFiller;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ConnectorRefreshPriority;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@RequiredArgsConstructor
public class DeadConnectorRefreshJob implements Job {
    private final DslContextFactory dslContextFactory;
    private final ConnectorQueueFiller connectorQueueFiller;

    @Override
    public void execute(JobExecutionContext context) {
        dslContextFactory.transaction(dsl -> connectorQueueFiller.enqueueConnectors(dsl,
                ConnectorOnlineStatus.DEAD, ConnectorRefreshPriority.SCHEDULED_DEAD_REFRESH));
    }
}
