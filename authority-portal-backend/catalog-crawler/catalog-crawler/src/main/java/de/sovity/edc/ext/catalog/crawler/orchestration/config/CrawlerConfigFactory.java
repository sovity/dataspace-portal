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

package de.sovity.edc.ext.catalog.crawler.orchestration.config;

import de.sovity.edc.ext.catalog.crawler.CrawlerConfigProps;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import java.time.Duration;

@RequiredArgsConstructor
public class CrawlerConfigFactory {
    private final Config config;

    public CrawlerConfig buildCrawlerConfig() {
        var environmentId = CrawlerConfigProps.CRAWLER_ENVIRONMENT_ID.getStringOrThrow(config);
        var numThreads = CrawlerConfigProps.CRAWLER_NUM_THREADS.getInt(config);
        var killOfflineConnectorsAfter = Duration.parse(CrawlerConfigProps.CRAWLER_KILL_OFFLINE_CONNECTORS_AFTER.getStringOrThrow(config));
        var maxDataOffers = CrawlerConfigProps.CRAWLER_MAX_DATA_OFFERS_PER_CONNECTOR.getInt(config);
        var maxContractOffers = CrawlerConfigProps.CRAWLER_MAX_CONTRACT_OFFERS_PER_DATA_OFFER.getInt(config);

        return CrawlerConfig.builder()
                .environmentId(environmentId)
                .numThreads(numThreads)
                .killOfflineConnectorsAfter(killOfflineConnectorsAfter)
                .maxDataOffersPerConnector(maxDataOffers)
                .maxContractOffersPerDataOffer(maxContractOffers)
                .build();
    }
}
