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

import de.sovity.edc.utils.config.ConfigUtilsImpl;
import de.sovity.edc.utils.config.model.ConfigProp;
import de.sovity.edc.utils.config.utils.UrlPathUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@UtilityClass
public class CrawlerConfigProps {
    public static final List<ConfigProp> ALL_CRAWLER_PROPS = new ArrayList<>();

    /* Crawler-specific Configuration */
    public static final ConfigProp CRAWLER_ENVIRONMENT_ID = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.BASIC)
        .property("crawler.environment.id")
        .description("Environment ID")
        .required(true)
    );

    public static final ConfigProp CRAWLER_DB_JDBC_URL = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.BASIC)
        .property("crawler.db.jdbc.url")
        .description("PostgreSQL DB Connection: JDBC URL")
        .required(true)
    );

    public static final ConfigProp CRAWLER_DB_JDBC_USER = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.BASIC)
        .property("crawler.db.jdbc.user")
        .description("PostgreSQL DB Connection: Username")
        .required(true)
    );

    public static final ConfigProp CRAWLER_DB_JDBC_PASSWORD = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.BASIC)
        .property("crawler.db.jdbc.password")
        .description("PostgreSQL DB Connection: Password")
        .required(true)
    );

    public static final ConfigProp CRAWLER_DB_CONNECTION_POOL_SIZE = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.db.connection.pool.size")
        .description("Size of the Hikari Connection Pool")
        .defaultValue("30")
    );

    public static final ConfigProp CRAWLER_DB_CONNECTION_TIMEOUT_IN_MS = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.db.connection.timeout.in.ms")
        .description("Sets the connection timeout for the datasource in milliseconds.")
        .defaultValue("30000")
    );

    public static final ConfigProp CRAWLER_CRON_ONLINE_CONNECTOR_REFRESH = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.cron.online.connector.refresh")
        .description("Cron expression for crawling ONLINE connectors")
        .defaultValue("*/20 * * ? * *")
    );

    public static final ConfigProp CRAWLER_CRON_OFFLINE_CONNECTOR_REFRESH = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.cron.offline.connector.refresh")
        .description("Cron expression for crawling OFFLINE connectors")
        .defaultValue("0 */5 * ? * *")
    );

    public static final ConfigProp CRAWLER_CRON_DEAD_CONNECTOR_REFRESH = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.cron.dead.connector.refresh")
        .description("Cron expression for crawling DEAD connectors")
        .defaultValue("0 0 * ? * *")
    );

    public static final ConfigProp CRAWLER_SCHEDULED_KILL_OFFLINE_CONNECTORS = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.scheduled.kill.offline.connectors")
        .description("Scheduled task for marking connectors as DEAD")
        .defaultValue("0 0 2 ? * *")
    );

    public static final ConfigProp CRAWLER_KILL_OFFLINE_CONNECTORS_AFTER = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.kill.offline.connectors.after")
        .description("Time in Java Duration Format after which an OFFLINE connector is marked as DEAD")
        .defaultValue("P5D")
    );

    public static final ConfigProp CRAWLER_HIDE_OFFLINE_DATA_OFFERS_AFTER = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.hide.offline.data.offers.after")
        .description("Time in Java Duration Format after which an OFFLINE data offer is hidden")
        .defaultValue("P1D")
    );

    public static final ConfigProp CRAWLER_NUM_THREADS = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.num.threads")
        .description("Number of threads for crawling")
        .defaultValue("32")
    );

    public static final ConfigProp CRAWLER_MAX_DATA_OFFERS_PER_CONNECTOR = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.max.data.offers.per.connector")
        .description("Maximum number of data offers per connector")
        .defaultValue("50")
    );

    public static final ConfigProp CRAWLER_MAX_CONTRACT_OFFERS_PER_DATA_OFFER = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("crawler.max.contract.offers.per.data.offer")
        .description("Maximum number of contract offers per data offer")
        .defaultValue("10")
    );

    public static final ConfigProp MY_EDC_NETWORK_TYPE = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("my.edc.network.type")
        .description("Configuring EDCs for different environments. Available values are: %s".formatted(
            String.join(", ", CrawlerConfigProps.NetworkType.ALL_NETWORK_TYPES)))
        .warnIfOverridden(true)
        .defaultValue(CrawlerConfigProps.NetworkType.PRODUCTION)
    );

    /* Basic Configuration */

    public static final ConfigProp MY_EDC_PARTICIPANT_ID = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.BASIC)
        .property("my.edc.participant.id")
        .description("Participant ID / Connector ID")
        .defaultValue("broker")
        .warnIfOverridden(true)
        .required(true)
    );

    public static final ConfigProp MY_EDC_FQDN = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.BASIC)
        .property("my.edc.fqdn")
        .description("Fully Qualified Domain Name of where the Connector is hosted, e.g. my-connector.myorg.com")
        .requiredIf(props -> CrawlerConfigProps.NetworkType.isProduction(props) || CrawlerConfigProps.NetworkType.isLocalDemoDockerCompose(props))
        .defaultValueFn(props -> new CrawlerConfigProps.NetworkTypeMatcher<String>(props).unitTest(() -> "localhost").orElseThrow())
    );

    /* Auth */

    public static final ConfigProp EDC_OAUTH_TOKEN_URL = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.C2C_IAM)
        .property("edc.oauth.token.url")
        .description("OAuth2 / DAPS: Token URL")
        .required(true)
    );

    public static final ConfigProp EDC_OAUTH_PROVIDER_JWKS_URL = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.C2C_IAM)
        .property("edc.oauth.provider.jwks.url")
        .description("OAuth2 / DAPS: JWKS URL")
        .required(true)
    );

    public static final ConfigProp EDC_OAUTH_CLIENT_ID = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.C2C_IAM)
        .property("edc.oauth.client.id")
        .description("OAuth2 / DAPS: Client ID. Defaults to Participant ID")
        .defaultValueFn(MY_EDC_PARTICIPANT_ID::getRaw)
    );

    public static final ConfigProp EDC_OAUTH_CERTIFICATE_ALIAS = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.C2C_IAM)
        .property("edc.oauth.certificate.alias")
        .description("OAuth2 / DAPS: Certificate Vault Entry for the Public Key")
        .defaultValue("daps-cert")
    );

    public static final ConfigProp EDC_OAUTH_PRIVATE_KEY_ALIAS = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.C2C_IAM)
        .property("edc.oauth.private.key.alias")
        .description("OAuth2 / DAPS: Certificate Vault Entry for the Private Key")
        .defaultValue("daps-priv")
    );

    public static final ConfigProp EDC_OAUTH_PROVIDER_AUDIENCE = addCeProp(builder ->
        builder
        .category(CrawlerConfigProps.Category.C2C_IAM)
        .property("edc.oauth.provider.audience")
        .description("OAuth2 / DAPS: Provider Audience")
        .warnIfOverridden(true)
        .defaultValueFn(EDC_OAUTH_TOKEN_URL::getRaw)
    );

    public static final ConfigProp EDC_OAUTH_ENDPOINT_AUDIENCE = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.C2C_IAM)
        .property("edc.oauth.endpoint.audience")
        .description("OAuth2 / DAPS: Endpoint Audience")
        .warnIfOverridden(true)
        .defaultValue("edc:dsp-api")
    );

    public static final ConfigProp EDC_AGENT_IDENTITY_KEY = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.C2C_IAM)
        .property("edc.agent.identity.key")
        .description("OAuth2 / DAPS: Agent Identity Key")
        .warnIfOverridden(true)
        .defaultValue("azp")
    );

    /* Advanced */

    public static final ConfigProp MY_EDC_FIRST_PORT = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("my.edc.first.port")
        .description("The first port of several ports to be used for the several API endpoints. " +
            "Useful when starting two EDCs on the host machine network / during tests")
        .warnIfOverridden(true)
        .defaultValue("11000")
    );

    public static final ConfigProp EDC_WEB_REST_CORS_ENABLED = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("edc.web.rest.cors.enabled")
        .description("Enable CORS")
        .warnIfOverridden(true)
        .relevantIf(props -> !CrawlerConfigProps.NetworkType.isProduction(props))
        .defaultValue("true")
    );

    public static final ConfigProp EDC_WEB_REST_CORS_HEADERS = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("edc.web.rest.cors.headers")
        .description("CORS: Allowed Headers")
        .warnIfOverridden(true)
        .relevantIf(props -> !CrawlerConfigProps.NetworkType.isProduction(props))
        .defaultValue("origin,content-type,accept,authorization,X-Api-Key")
    );

    public static final ConfigProp EDC_WEB_REST_CORS_ORIGINS = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.ADVANCED)
        .property("edc.web.rest.cors.origins")
        .description("CORS: Allowed Origins")
        .warnIfOverridden(true)
        .relevantIf(props -> !CrawlerConfigProps.NetworkType.isProduction(props))
        .defaultValue("*")
    );

    /* Defaults of EDC Configuration */

    public static final ConfigProp MY_EDC_PROTOCOL = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.protocol")
        .description("HTTP Protocol for when the EDC exposes its own URL for callbacks")
        .warnIfOverridden(true)
        .defaultValueFn(props -> CrawlerConfigProps.NetworkType.isProduction(props) ? "https://" : "http://")
    );

    public static final ConfigProp MY_EDC_BASE_PATH = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("my.edc.base.path")
        .description("Optional prefix to be added before all API paths")
        .warnIfOverridden(true)
        .defaultValue("/")
    );

    public static final ConfigProp WEB_HTTP_PATH = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.path")
        .description("API Group 'Web' contains misc API endpoints, usually not meant to be public, this is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api"))
    );

    public static final ConfigProp WEB_HTTP_PORT = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.port")
        .description("API Group 'Web' contains misc API endpoints, usually not meant to be public, this is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 1))
    );

    public static final ConfigProp WEB_HTTP_MANAGEMENT_PATH = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.management.path")
        .description("API Group 'Management' contains API endpoints for EDC interaction and " +
            "should be protected from unauthorized access. This is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/management"))
    );

    public static final ConfigProp WEB_HTTP_MANAGEMENT_PORT = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.management.port")
        .description(
            "API Group 'Management' contains API endpoints for EDC interaction and " +
                "should be protected from unauthorized access. This is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 2))
    );

    public static final ConfigProp WEB_HTTP_PROTOCOL_PATH = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.protocol.path")
        .description("API Group 'Protocol' must be public as it is used for connector to connector communication, this is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/v1/dsp"))
    );

    public static final ConfigProp WEB_HTTP_PROTOCOL_PORT = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.protocol.port")
        .description("API Group 'Protocol' must be public as it is used for connector to connector communication, this is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 3))
    );

    public static final ConfigProp WEB_HTTP_CONTROL_PATH = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.control.path")
        .description("API Group 'Control' contains API endpoints for control plane/data plane interaction and " +
            "should be non-public, this is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/control"))
    );

    public static final ConfigProp WEB_HTTP_CONTROL_PORT = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.control.port")
        .description("API Group 'Control' contains API endpoints for control plane/data plane interaction and " +
            "should be non-public, this is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 4))
    );

    public static final ConfigProp WEB_HTTP_PUBLIC_PATH = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.public.path")
        .description("API Group 'Public' contains public data plane API endpoints. This is the base path.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> UrlPathUtils.urlPathJoin(MY_EDC_BASE_PATH.getRaw(props), "api/public"))
    );

    public static final ConfigProp WEB_HTTP_PUBLIC_PORT = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("web.http.public.port")
        .description("API Group 'Public' contains public data plane API endpoints. This is the port.")
        .warnIfOverridden(true)
        .defaultValueFn(props -> plus(props, MY_EDC_FIRST_PORT, 5))
    );

    public static final ConfigProp EDC_JSONLD_HTTPS_ENABLED = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.jsonld.https.enabled")
        .description("Required to be set since Eclipse EDC 0.2.1")
        .warnIfOverridden(true)
        .defaultValue("true")
    );

    public static final ConfigProp EDC_CONNECTOR_NAME = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.connector.name")
        .description("Connector Name")
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_PARTICIPANT_ID::getRaw)
    );

    public static final ConfigProp EDC_PARTICIPANT_ID = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.participant.id")
        .description("Participant ID / Connector ID")
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_PARTICIPANT_ID::getRaw)
    );

    public static final ConfigProp EDC_HOSTNAME = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.hostname")
        .description("Same as %s".formatted(MY_EDC_FQDN.getProperty()))
        .warnIfOverridden(true)
        .defaultValueFn(MY_EDC_FQDN::getRaw)
    );

    public static final ConfigProp EDC_DSP_CALLBACK_ADDRESS = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.dsp.callback.address")
        .description("Full URL for the DSP callback address")
        .warnIfOverridden(true)
        .defaultValueFn(ConfigUtilsImpl::getProtocolApiUrl)
    );

    public static final ConfigProp EDC_VAULT = addCeProp(builder -> builder
        .category(CrawlerConfigProps.Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.vault")
        .description("This file could contain an entry replacing the EDC_KEYSTORE ENV var, " +
            "but for some reason it is required, and EDC won't start up if it isn't configured." +
            "It is created in the Dockerfile")
        .relevantIf(CrawlerConfigProps.NetworkType::isProduction)
        .defaultValue("/app/empty-properties-file.properties")
    );

    public static final ConfigProp EDC_OAUTH_VALIDATION_NBF_LEEWAY = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.oauth.validation.nbf.leeway")
        .description("OAuth2 / DAPS: Leeway for the 'nbf' claim in seconds")
        .warnIfOverridden(true)
        .defaultValue("10")
    );

    public static final ConfigProp EDC_OAUTH_VALIDATION_ISSUED_AT_LEEWAY = addCeProp(builder -> builder
        .category(Category.RAW_EDC_CONFIG_DEFAULTS)
        .property("edc.oauth.validation.issued.at.leeway")
        .description("OAuth2 / DAPS: Leeway for the 'iat' claim in seconds")
        .warnIfOverridden(true)
        .defaultValue("10")
    );

    /* Helpers */

    private static ConfigProp addCeProp(Consumer<ConfigProp.ConfigPropBuilder> builderFn) {
        var builder = ConfigProp.builder();
        builderFn.accept(builder);
        var built = builder.build();

        // Register the property in the list of all available CE properties
        // Order matters here, as the property defaults are calculated in order
        built.also(ALL_CRAWLER_PROPS::add);
        return built;
    }

    private static String plus(Map<String, String> props, ConfigProp prop, int add) {
        var raw = prop.getRaw(props);
        var result = Integer.parseInt(raw == null ? "0" : raw) + add;
        return String.valueOf(result);
    }

    @UtilityClass
    public static class NetworkType {
        public static final String PRODUCTION = "production";
        public static final String LOCAL_DEMO_DOCKER_COMPOSE = "local-demo-docker-compose";
        public static final String UNIT_TEST = "unit-test";
        public static final List<String> ALL_NETWORK_TYPES = List.of(PRODUCTION, LOCAL_DEMO_DOCKER_COMPOSE, UNIT_TEST);

        public static boolean isProduction(Map<String, String> props) {
            return CrawlerConfigProps.NetworkType.PRODUCTION.equals(MY_EDC_NETWORK_TYPE.getRaw(props));
        }

        public static boolean isLocalDemoDockerCompose(Map<String, String> props) {
            return CrawlerConfigProps.NetworkType.LOCAL_DEMO_DOCKER_COMPOSE.equals(MY_EDC_NETWORK_TYPE.getRaw(props));
        }

        public static boolean isUnitTest(Map<String, String> props) {
            return CrawlerConfigProps.NetworkType.UNIT_TEST.equals(MY_EDC_NETWORK_TYPE.getRaw(props));
        }
    }

    @Setter
    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor
    public static class NetworkTypeMatcher<T> {
        private final Map<String, String> props;
        private Supplier<T> production;
        private Supplier<T> localDemoDockerCompose;
        private Supplier<T> unitTest;

        public T orElse(Supplier<T> elseFn) {
            if (production != null && CrawlerConfigProps.NetworkType.isProduction(props)) {
                return production.get();
            }

            if (localDemoDockerCompose != null && CrawlerConfigProps.NetworkType.isLocalDemoDockerCompose(props)) {
                return localDemoDockerCompose.get();
            }

            if (unitTest != null && CrawlerConfigProps.NetworkType.isUnitTest(props)) {
                return unitTest.get();
            }

            return elseFn.get();
        }

        public T orElseThrow() {
            return orElse(() -> {
                var msg = "Unhandled %s: %s".formatted(
                    MY_EDC_NETWORK_TYPE.getProperty(),
                    MY_EDC_NETWORK_TYPE.getRaw(props)
                );
                throw new IllegalArgumentException(msg);
            });
        }
    }

    @UtilityClass
    private static class Category {
        public static final String BASIC = "Basic Configuration";
        public static final String ADVANCED = "Advanced configuration";
        public static final String C2C_IAM = "Connector-to-Connector IAM";
        public static final String RAW_EDC_CONFIG_DEFAULTS = "EDC Config Defaults / Overrides";
    }
}
