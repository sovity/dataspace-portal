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
import de.sovity.authorityportal.db.jooq.enums.CrawlerEventStatus;
import de.sovity.authorityportal.db.jooq.enums.CrawlerEventType;
import de.sovity.authorityportal.db.jooq.tables.records.CrawlerEventLogRecord;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class CrawlerEventLogger {

    public void logConnectorUpdated(DSLContext dsl, ConnectorRef connectorRef, ConnectorChangeTracker changes) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_UPDATED);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage(changes.toString());
        logEntry.insert();
    }

    public void logConnectorOffline(DSLContext dsl, ConnectorRef connectorRef, CrawlerEventErrorMessage errorMessage) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_STATUS_CHANGE_OFFLINE);
        logEntry.setEventStatus(CrawlerEventStatus.ERROR);
        logEntry.setUserMessage("Connector is offline.");
        logEntry.setErrorStack(errorMessage.stackTraceOrNull());
        logEntry.insert();
    }

    public void logConnectorOnline(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_STATUS_CHANGE_ONLINE);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage("Connector is online.");
        logEntry.insert();
    }

    public void logConnectorUpdateDataOfferLimitExceeded(
            DSLContext dsl,
            ConnectorRef connectorRef,
            Integer maxDataOffersPerConnector
    ) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_DATA_OFFER_LIMIT_EXCEEDED);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage(
                "Connector has more than %d data offers. Exceeding data offers will be ignored.".formatted(maxDataOffersPerConnector));
        logEntry.insert();
    }

    public void logConnectorUpdateDataOfferLimitOk(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_DATA_OFFER_LIMIT_OK);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage("Connector is not exceeding the maximum number of data offers limit anymore.");
        logEntry.insert();
    }

    public void logConnectorUpdateContractOfferLimitExceeded(
            DSLContext dsl,
            ConnectorRef connectorRef,
            Integer maxContractOffersPerConnector
    ) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_CONTRACT_OFFER_LIMIT_EXCEEDED);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage(String.format(
                "Some data offers have more than %d contract offers. Exceeding contract offers will be ignored.: ",
                maxContractOffersPerConnector
        ));
        logEntry.insert();
    }

    public void logConnectorUpdateContractOfferLimitOk(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_CONTRACT_OFFER_LIMIT_OK);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage("Connector is not exceeding the maximum number of contract offers per data offer limit anymore.");
        logEntry.insert();
    }

    public void addKilledDueToOfflineTooLongMessages(DSLContext dsl, Collection<ConnectorRef> connectorRefs) {
        var logEntries = connectorRefs.stream()
                .map(connectorRef -> buildKilledDueToOfflineTooLongMessage(dsl, connectorRef))
                .toList();
        dsl.batchInsert(logEntries).execute();
    }

    private CrawlerEventLogRecord buildKilledDueToOfflineTooLongMessage(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_KILLED_DUE_TO_OFFLINE_FOR_TOO_LONG);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage("Connector was marked as dead for being offline too long.");
        return logEntry;
    }

    private CrawlerEventLogRecord newLogEntry(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = dsl.newRecord(Tables.CRAWLER_EVENT_LOG);
        logEntry.setId(UUID.randomUUID());
        logEntry.setEnvironment(connectorRef.getEnvironmentId());
        logEntry.setConnectorId(connectorRef.getConnectorId());
        logEntry.setCreatedAt(OffsetDateTime.now());
        return logEntry;
    }
}
