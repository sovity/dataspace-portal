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

package de.sovity.edc.ext.catalog.crawler.crawling.logging;

import de.sovity.authorityportal.db.jooq.Tables;
import de.sovity.authorityportal.db.jooq.enums.MeasurementErrorStatus;
import de.sovity.authorityportal.db.jooq.enums.MeasurementType;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class CrawlerExecutionTimeLogger {
    public void logExecutionTime(DSLContext dsl, ConnectorRef connectorRef, long executionTimeInMs, MeasurementErrorStatus errorStatus) {
        var logEntry = dsl.newRecord(Tables.CRAWLER_EXECUTION_TIME_MEASUREMENT);
        logEntry.setId(UUID.randomUUID());
        logEntry.setEnvironment(connectorRef.getEnvironmentId());
        logEntry.setConnectorId(connectorRef.getConnectorId());
        logEntry.setDurationInMs(executionTimeInMs);
        logEntry.setType(MeasurementType.CONNECTOR_REFRESH);
        logEntry.setErrorStatus(errorStatus);
        logEntry.setCreatedAt(OffsetDateTime.now());
        logEntry.insert();
    }
}
