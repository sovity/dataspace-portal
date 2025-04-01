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

package de.sovity.edc.ext.catalog.crawler.orchestration.schedules;

import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.CrawlerTestDb;
import de.sovity.edc.ext.catalog.crawler.TestData;
import de.sovity.edc.ext.catalog.crawler.crawling.OfflineConnectorCleaner;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogCleaner;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorStatusUpdater;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.time.OffsetDateTime;

import static de.sovity.authorityportal.db.jooq.tables.Connector.CONNECTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfflineConnectorRemovalJobTest {
    @RegisterExtension
    private static final CrawlerTestDb TEST_DATABASE = new CrawlerTestDb();

    ConnectorRef connectorRef = TestData.connectorRef;

    CrawlerConfig crawlerConfig;
    OfflineConnectorCleaner offlineConnectorCleaner;
    ConnectorQueries connectorQueries;

    @BeforeEach
    void beforeEach() {
        crawlerConfig = mock(CrawlerConfig.class);
        connectorQueries = new ConnectorQueries(crawlerConfig);
        offlineConnectorCleaner = new OfflineConnectorCleaner(
                crawlerConfig,
                new ConnectorQueries(crawlerConfig),
                new CrawlerEventLogger(),
                new ConnectorStatusUpdater(),
                new CatalogCleaner()
        );
        when(crawlerConfig.getEnvironmentId()).thenReturn(connectorRef.getEnvironmentId());
    }

    @Test
    void test_offlineConnectorCleaner_should_be_dead() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            when(crawlerConfig.getKillOfflineConnectorsAfter()).thenReturn(Duration.ofDays(5));
            TestData.insertConnector(dsl, connectorRef, record -> {
                record.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
                record.setLastSuccessfulRefreshAt(OffsetDateTime.now().minusDays(6));
            });

            // act
            offlineConnectorCleaner.cleanConnectorsIfOfflineTooLong(dsl);

            // assert
            var connector = dsl.fetchOne(CONNECTOR, CONNECTOR.CONNECTOR_ID.eq(connectorRef.getConnectorId()));
            assertThat(connector.getOnlineStatus()).isEqualTo(ConnectorOnlineStatus.DEAD);
        });
    }

    @Test
    void test_offlineConnectorCleaner_should_not_be_dead() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            when(crawlerConfig.getKillOfflineConnectorsAfter()).thenReturn(Duration.ofDays(5));
            TestData.insertConnector(dsl, connectorRef, record -> {
                record.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
                record.setLastSuccessfulRefreshAt(OffsetDateTime.now().minusDays(2));
            });

            // act
            offlineConnectorCleaner.cleanConnectorsIfOfflineTooLong(dsl);

            // assert
            var connector = dsl.fetchOne(CONNECTOR, CONNECTOR.CONNECTOR_ID.eq(connectorRef.getConnectorId()));
            assertThat(connector.getOnlineStatus()).isEqualTo(ConnectorOnlineStatus.OFFLINE);
        });
    }

}
