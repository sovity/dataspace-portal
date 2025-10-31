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
package de.sovity.edc.utils.config;

import de.sovity.edc.ext.catalog.crawler.CrawlerConfigProps;
import de.sovity.edc.runtime.config.AuthHeader;
import de.sovity.edc.runtime.config.ConfigUtils;
import de.sovity.edc.utils.config.model.ConfigProp;
import de.sovity.edc.utils.config.utils.UrlPathUtils;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
public class ConfigUtilsImpl implements ConfigUtils {
    private final String participantId;
    private final String managementApiUrl;
    private final String managementApiKey;
    private final String protocolApiUrl;
    private final String defaultApiUrl;
    private final String publicApiUrl;
    private final String controlApiUrl;
    private final String proxyApiUrl;

    public ConfigUtilsImpl(Map<String, String> config) {
        this.participantId = getParticipantId(config);
        this.managementApiUrl = "http://0.0.0.0/does-not-exist";
        this.managementApiKey = "management-api-key-does-not-exist";
        this.protocolApiUrl = getProtocolApiUrl(config);
        this.defaultApiUrl = getDefaultApiUrl(config);
        this.publicApiUrl = "http://0.0.0.0/does-not-exist";
        this.controlApiUrl = "http://0.0.0.0/does-not-exist";
        this.proxyApiUrl = "http://0.0.0.0/does-not-exist";
    }

    public static String getParticipantId(Map<String, String> props) {
        return CrawlerConfigProps.EDC_PARTICIPANT_ID.getRaw(props);
    }

    public static String getProtocolApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            CrawlerConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, CrawlerConfigProps.WEB_HTTP_PROTOCOL_PORT),
            CrawlerConfigProps.WEB_HTTP_PROTOCOL_PATH.getRaw(props)
        );
    }

    public static String getDefaultApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            CrawlerConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, CrawlerConfigProps.WEB_HTTP_PORT),
            CrawlerConfigProps.WEB_HTTP_PATH.getRaw(props)
        );
    }

    @Nullable
    private static String getHost(Map<String, String> props, ConfigProp portIfNoReverseProxy) {
        var hasReverseProxy = CrawlerConfigProps.NetworkType.isProduction(props);

        var host = CrawlerConfigProps.MY_EDC_FQDN.getRaw(props);
        if (!hasReverseProxy) {
            host = "%s:%s".formatted(host, portIfNoReverseProxy.getRaw(props));
        }
        return host;
    }

    @Override
    public @Nullable AuthHeader getManagementApiAuthHeader() {
        if (managementApiKey == null) {
            return null;
        }
        return new AuthHeader("x-api-key", managementApiKey);
    }
}
