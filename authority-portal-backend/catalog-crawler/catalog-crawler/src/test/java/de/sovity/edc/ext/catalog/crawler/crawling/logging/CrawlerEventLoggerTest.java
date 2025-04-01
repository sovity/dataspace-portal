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
import de.sovity.edc.ext.catalog.crawler.CrawlerTestDb;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class CrawlerEventLoggerTest {
    @RegisterExtension
    private static final CrawlerTestDb TEST_DATABASE = new CrawlerTestDb();

    @Test
    void testDataOfferWriter_allSortsOfUpdates() {
        TEST_DATABASE.testTransaction(dsl -> {
            var crawlerEventLogger = new CrawlerEventLogger();

            // Test that insertions insert required fields and don't cause DB errors
            var connectorRef = new ConnectorRef(
                    "MDSL1234XX.C1234XX",
                    "test",
                    "My Org",
                    "MDSL1234XX",
                    "https://example.com/api/dsp"
            );
            crawlerEventLogger.logConnectorUpdated(dsl, connectorRef, new ConnectorChangeTracker());
            crawlerEventLogger.logConnectorOnline(dsl, connectorRef);
            crawlerEventLogger.logConnectorOffline(dsl, connectorRef, new CrawlerEventErrorMessage("Message", "Stacktrace"));
            crawlerEventLogger.logConnectorUpdateContractOfferLimitExceeded(dsl, connectorRef, 10);
            crawlerEventLogger.logConnectorUpdateContractOfferLimitOk(dsl, connectorRef);
            crawlerEventLogger.logConnectorUpdateDataOfferLimitExceeded(dsl, connectorRef, 10);
            crawlerEventLogger.logConnectorUpdateDataOfferLimitOk(dsl, connectorRef);

            assertThat(numLogEntries(dsl)).isEqualTo(7);
        });
    }

    private Integer numLogEntries(DSLContext dsl) {
        return dsl.selectCount().from(Tables.CRAWLER_EVENT_LOG).fetchOne().component1();
    }
}
