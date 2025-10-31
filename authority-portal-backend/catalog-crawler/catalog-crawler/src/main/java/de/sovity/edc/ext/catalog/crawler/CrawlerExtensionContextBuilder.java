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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.ce.libs.mappers.AssetMapper;
import de.sovity.edc.ce.libs.mappers.PlaceholderEndpointService;
import de.sovity.edc.ce.libs.mappers.PolicyMapper;
import de.sovity.edc.ce.libs.mappers.asset.AssetEditRequestMapper;
import de.sovity.edc.ce.libs.mappers.asset.AssetJsonLdBuilder;
import de.sovity.edc.ce.libs.mappers.asset.AssetJsonLdParser;
import de.sovity.edc.ce.libs.mappers.asset.utils.AssetJsonLdUtils;
import de.sovity.edc.ce.libs.mappers.asset.utils.EdcPropertyUtils;
import de.sovity.edc.ce.libs.mappers.asset.utils.ShortDescriptionBuilder;
import de.sovity.edc.ce.libs.mappers.dataaddress.DataSourceMapper;
import de.sovity.edc.ce.libs.mappers.dataaddress.http.HttpDataAddressMapper;
import de.sovity.edc.ce.libs.mappers.dataaddress.http.HttpDataSourceMapper;
import de.sovity.edc.ce.libs.mappers.dataaddress.http.HttpHeaderMapper;
import de.sovity.edc.ce.libs.mappers.dataaddress.http.OnRequestDataSourceMapper;
import de.sovity.edc.ce.libs.mappers.dsp.DspCatalogService;
import de.sovity.edc.ce.libs.mappers.dsp.mapper.DspDataOfferParser;
import de.sovity.edc.ce.libs.mappers.policy.*;
import de.sovity.edc.ext.catalog.crawler.crawling.ConnectorCrawler;
import de.sovity.edc.ext.catalog.crawler.crawling.OfflineConnectorCleaner;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogBuilder;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogMappingUtils;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogService;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerExecutionTimeLogger;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.*;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogCleaner;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogPatchApplier;
import de.sovity.edc.ext.catalog.crawler.dao.config.DataSourceFactory;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorStatusUpdater;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfigFactory;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ConnectorQueue;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ConnectorQueueFiller;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ThreadPool;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ThreadPoolTaskQueue;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.*;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.utils.CronJobRef;
import de.sovity.edc.runtime.config.ConfigUtils;
import de.sovity.edc.utils.config.ConfigUtilsImpl;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.controlplane.services.spi.catalog.CatalogService;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.constants.CoreConstants;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * Manual Dependency Injection (DYDI).
 * <p>
 * We want to develop as Java Backend Development is done, but we have
 * no CDI / DI Framework to rely on.
 * <p>
 * EDC {@link Inject} only works in {@link CrawlerExtension}.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CrawlerExtensionContextBuilder {

    public static CrawlerExtensionContext buildContext(
        Config config,
        Monitor monitor,
        TypeManager typeManager,
        TypeTransformerRegistry typeTransformerRegistry,
        JsonLd jsonLd,
        CatalogService catalogService
    ) {
        // Config
        var crawlerConfigFactory = new CrawlerConfigFactory(config);
        var crawlerConfig = crawlerConfigFactory.buildCrawlerConfig();

        // DB
        var dataSourceFactory = new DataSourceFactory(config);
        var dataSource = dataSourceFactory.newDataSource();

        // Dao
        var dataOfferQueries = new DataOfferQueries();
        var dslContextFactory = new DslContextFactory(dataSource);
        var connectorQueries = new ConnectorQueries(crawlerConfig);

        // Services
        var objectMapperJsonLd = getJsonLdObjectMapper(typeManager);
        var configUtils = new ConfigUtilsImpl(config.getEntries());
        var placeholderEndpointService = new PlaceholderEndpointService(configUtils);
        var assetMapper = newAssetMapper(typeTransformerRegistry, jsonLd, placeholderEndpointService);
        var policyMapper = newPolicyMapper(typeTransformerRegistry, objectMapperJsonLd, jsonLd, configUtils);
        var crawlerEventLogger = new CrawlerEventLogger();
        var crawlerExecutionTimeLogger = new CrawlerExecutionTimeLogger();
        var dataOfferMappingUtils = new FetchedCatalogMappingUtils(
            policyMapper,
            assetMapper,
            objectMapperJsonLd
        );
        var contractOfferRecordUpdater = new ContractOfferRecordUpdater();
        var shortDescriptionBuilder = new ShortDescriptionBuilder();
        var dataOfferRecordUpdater = new DataOfferRecordUpdater(shortDescriptionBuilder);
        var contractOfferQueries = new ContractOfferQueries();
        var dataOfferLimitsEnforcer = new DataOfferLimitsEnforcer(crawlerConfig, crawlerEventLogger);
        var dataOfferPatchBuilder = new CatalogPatchBuilder(
            contractOfferQueries,
            dataOfferQueries,
            dataOfferRecordUpdater,
            contractOfferRecordUpdater
        );
        var dataOfferPatchApplier = new CatalogPatchApplier();
        var dataOfferWriter = new ConnectorUpdateCatalogWriter(dataOfferPatchBuilder, dataOfferPatchApplier);
        var connectorUpdateSuccessWriter = new ConnectorUpdateSuccessWriter(
            crawlerEventLogger,
            dataOfferWriter,
            dataOfferLimitsEnforcer
        );
        var fetchedDataOfferBuilder = new FetchedCatalogBuilder(dataOfferMappingUtils);
        var dspDataOfferParser = new DspDataOfferParser(jsonLd);
        var dspCatalogService = new DspCatalogService(
            catalogService,
            dspDataOfferParser
        );
        var dataOfferFetcher = new FetchedCatalogService(dspCatalogService, fetchedDataOfferBuilder);
        var connectorUpdateFailureWriter = new ConnectorUpdateFailureWriter(crawlerEventLogger, monitor);
        var connectorUpdater = new ConnectorCrawler(
            dataOfferFetcher,
            connectorUpdateSuccessWriter,
            connectorUpdateFailureWriter,
            connectorQueries,
            dslContextFactory,
            monitor,
            crawlerExecutionTimeLogger
        );

        var threadPoolTaskQueue = new ThreadPoolTaskQueue();
        var threadPool = new ThreadPool(threadPoolTaskQueue, crawlerConfig, monitor);
        var connectorQueue = new ConnectorQueue(connectorUpdater, threadPool);
        var connectorQueueFiller = new ConnectorQueueFiller(connectorQueue, connectorQueries);
        var connectorStatusUpdater = new ConnectorStatusUpdater();
        var catalogCleaner = new CatalogCleaner();
        var offlineConnectorCleaner = new OfflineConnectorCleaner(
            crawlerConfig,
            connectorQueries,
            crawlerEventLogger,
            connectorStatusUpdater,
            catalogCleaner
        );

        // Schedules
        List<CronJobRef<?>> jobs = List.of(
            getOnlineConnectorRefreshCronJob(dslContextFactory, connectorQueueFiller, config),
            getOfflineConnectorRefreshCronJob(dslContextFactory, connectorQueueFiller, config),
            getDeadConnectorRefreshCronJob(dslContextFactory, connectorQueueFiller, config),
            getOfflineConnectorCleanerCronJob(dslContextFactory, offlineConnectorCleaner, config)
        );

        // Startup
        var quartzScheduleInitializer = new QuartzScheduleInitializer(config, monitor, jobs);
        var crawlerInitializer = new CrawlerInitializer(quartzScheduleInitializer);

        return new CrawlerExtensionContext(
            crawlerInitializer,
            dataSource,
            dslContextFactory,
            connectorUpdater,
            policyMapper,
            fetchedDataOfferBuilder,
            dataOfferRecordUpdater
        );
    }

    @NotNull
    private static PolicyMapper newPolicyMapper(
        TypeTransformerRegistry typeTransformerRegistry,
        ObjectMapper objectMapperJsonLd,
        JsonLd jsonLd,
        ConfigUtils configUtils
    ) {
        var operatorMapper = new OperatorMapper();
        var literalMapper = new LiteralMapper(objectMapperJsonLd);
        var atomicConstraintMapper = new AtomicConstraintMapper(
            literalMapper,
            operatorMapper
        );
        var policyValidator = new PolicyValidator();
        var expressionMapper = new ExpressionMapper(atomicConstraintMapper);
        var expressionExtractor = new ExpressionExtractor(
            policyValidator,
            expressionMapper
        );
        return new PolicyMapper(
            expressionExtractor,
            expressionMapper,
            typeTransformerRegistry,
            jsonLd,
            configUtils
        );
    }

    @NotNull
    private static AssetMapper newAssetMapper(
        TypeTransformerRegistry typeTransformerRegistry,
        JsonLd jsonLd,
        PlaceholderEndpointService placeholderEndpointService
    ) {
        var edcPropertyUtils = new EdcPropertyUtils();
        var assetJsonLdUtils = new AssetJsonLdUtils();
        var assetEditRequestMapper = new AssetEditRequestMapper();
        var shortDescriptionBuilder = new ShortDescriptionBuilder();
        var assetJsonLdParser = new AssetJsonLdParser(
            assetJsonLdUtils,
            shortDescriptionBuilder,
            endpoint -> false
        );
        var httpHeaderMapper = new HttpHeaderMapper();
        var httpDataAddressMapper = new HttpDataAddressMapper(httpHeaderMapper);
        var httpDataSourceMapper = new HttpDataSourceMapper(httpDataAddressMapper);
        var onRequestDataSourceMapper = new OnRequestDataSourceMapper(httpDataSourceMapper, placeholderEndpointService);
        var dataSourceMapper = new DataSourceMapper(
            edcPropertyUtils,
            httpDataSourceMapper,
            onRequestDataSourceMapper
        );
        var assetJsonLdBuilder = new AssetJsonLdBuilder(
            dataSourceMapper,
            assetJsonLdParser,
            assetEditRequestMapper
        );
        return new AssetMapper(
            typeTransformerRegistry,
            assetJsonLdBuilder,
            assetJsonLdParser,
            jsonLd
        );
    }

    @NotNull
    private static CronJobRef<OfflineConnectorCleanerJob> getOfflineConnectorCleanerCronJob(DslContextFactory dslContextFactory,
                                                                                            OfflineConnectorCleaner offlineConnectorCleaner, Config config) {
        return new CronJobRef<>(
            CrawlerConfigProps.CRAWLER_SCHEDULED_KILL_OFFLINE_CONNECTORS.getProperty(),
            OfflineConnectorCleanerJob.class,
            () -> new OfflineConnectorCleanerJob(dslContextFactory, offlineConnectorCleaner)
        );
    }

    @NotNull
    private static CronJobRef<OnlineConnectorRefreshJob> getOnlineConnectorRefreshCronJob(
        DslContextFactory dslContextFactory,
        ConnectorQueueFiller connectorQueueFiller,
        Config config
    ) {
        return new CronJobRef<>(
            CrawlerConfigProps.CRAWLER_CRON_ONLINE_CONNECTOR_REFRESH.getProperty(),
            OnlineConnectorRefreshJob.class,
            () -> new OnlineConnectorRefreshJob(dslContextFactory, connectorQueueFiller)
        );
    }

    @NotNull
    private static CronJobRef<OfflineConnectorRefreshJob> getOfflineConnectorRefreshCronJob(
        DslContextFactory dslContextFactory,
        ConnectorQueueFiller connectorQueueFiller,
        Config config
    ) {
        return new CronJobRef<>(
            CrawlerConfigProps.CRAWLER_CRON_OFFLINE_CONNECTOR_REFRESH.getProperty(),
            OfflineConnectorRefreshJob.class,
            () -> new OfflineConnectorRefreshJob(dslContextFactory, connectorQueueFiller)
        );
    }

    @NotNull
    private static CronJobRef<DeadConnectorRefreshJob> getDeadConnectorRefreshCronJob(DslContextFactory dslContextFactory,
                                                                                      ConnectorQueueFiller connectorQueueFiller, Config config) {
        return new CronJobRef<>(
            CrawlerConfigProps.CRAWLER_CRON_DEAD_CONNECTOR_REFRESH.getProperty(),
            DeadConnectorRefreshJob.class,
            () -> new DeadConnectorRefreshJob(dslContextFactory, connectorQueueFiller)
        );
    }

    private static ObjectMapper getJsonLdObjectMapper(TypeManager typeManager) {
        var objectMapper = typeManager.getMapper(CoreConstants.JSON_LD);

        // Fixes Dates in JSON-LD Object Mapper
        // The Core EDC uses longs over OffsetDateTime, so they never fixed the date format
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}
