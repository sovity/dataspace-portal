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

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogPatchApplier;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.ShortDescriptionBuilder;
import lombok.Value;
import org.eclipse.edc.spi.system.configuration.Config;

import static org.mockito.Mockito.mock;

@Value
class DataOfferWriterTestDydi {
    Config config = mock(Config.class);
    CrawlerConfig crawlerConfig = mock(CrawlerConfig.class);
    DataOfferQueries dataOfferQueries = new DataOfferQueries();
    ContractOfferQueries contractOfferQueries = new ContractOfferQueries();
    ContractOfferRecordUpdater contractOfferRecordUpdater = new ContractOfferRecordUpdater();
    ConnectorQueries connectorQueries = new ConnectorQueries(crawlerConfig);
    ShortDescriptionBuilder shortDescriptionBuilder = new ShortDescriptionBuilder();
    DataOfferRecordUpdater dataOfferRecordUpdater = new DataOfferRecordUpdater(shortDescriptionBuilder);
    CatalogPatchBuilder catalogPatchBuilder = new CatalogPatchBuilder(
            contractOfferQueries,
            dataOfferQueries,
            dataOfferRecordUpdater,
            contractOfferRecordUpdater
    );
    CatalogPatchApplier catalogPatchApplier = new CatalogPatchApplier();
    ConnectorUpdateCatalogWriter connectorUpdateCatalogWriter = new ConnectorUpdateCatalogWriter(catalogPatchBuilder, catalogPatchApplier);

    // for the ConnectorUpdateSuccessWriterTest
    CrawlerEventLogger crawlerEventLogger = new CrawlerEventLogger();
    DataOfferLimitsEnforcer dataOfferLimitsEnforcer = new DataOfferLimitsEnforcer(
            crawlerConfig,
            crawlerEventLogger
    );
    ConnectorUpdateSuccessWriter connectorUpdateSuccessWriter = new ConnectorUpdateSuccessWriter(
            crawlerEventLogger,
            connectorUpdateCatalogWriter,
            dataOfferLimitsEnforcer
    );
}
