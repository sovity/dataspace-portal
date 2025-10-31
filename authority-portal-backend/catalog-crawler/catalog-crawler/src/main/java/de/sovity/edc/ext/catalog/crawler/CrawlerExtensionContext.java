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

package de.sovity.edc.ext.catalog.crawler;

import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.ext.catalog.crawler.crawling.ConnectorCrawler;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogBuilder;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferRecordUpdater;
import de.sovity.edc.ce.libs.mappers.PolicyMapper;


/**
 * Manual Dependency Injection result
 *
 * @param crawlerInitializer Startup Logic
 */
public record CrawlerExtensionContext(
        CrawlerInitializer crawlerInitializer,
        // Required for stopping connections on closing
        HikariDataSource dataSource,
        DslContextFactory dslContextFactory,

        // Required for Integration Tests
        ConnectorCrawler connectorCrawler,
        PolicyMapper policyMapper,
        FetchedCatalogBuilder catalogPatchBuilder,
        DataOfferRecordUpdater dataOfferRecordUpdater
) {
}
