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

import de.sovity.edc.ce.libs.mappers.PlaceholderEndpointService;
import de.sovity.edc.runtime.config.ConfigUtils;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.connector.controlplane.services.spi.catalog.CatalogService;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

@Provides({CrawlerExtensionContext.class})
public class CrawlerExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "Data Space Portal Data Catalog Crawler";

    @Inject
    private TypeManager typeManager;

    @Inject
    private TypeTransformerRegistry typeTransformerRegistry;

    @Inject
    private JsonLd jsonLd;

    @Inject
    private CatalogService catalogService;

    /**
     * Manual Dependency Injection Result
     */
    private CrawlerExtensionContext services;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        services = CrawlerExtensionContextBuilder.buildContext(
            context.getConfig(),
            context.getMonitor(),
            typeManager,
            typeTransformerRegistry,
            jsonLd,
            catalogService
        );

        // Provide access for the tests
        context.registerService(CrawlerExtensionContext.class, services);
    }

    @Override
    public void start() {
        if (services == null) {
            return;
        }
        services.crawlerInitializer().onStartup();
    }

    @Override
    public void shutdown() {
        if (services == null) {
            return;
        }
        services.dataSource().close();
    }
}
