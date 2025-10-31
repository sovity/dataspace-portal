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

import de.sovity.edc.utils.config.model.ConfigProp;
import org.eclipse.edc.boot.system.DefaultServiceExtensionContext;
import org.eclipse.edc.boot.system.ExtensionLoader;
import org.eclipse.edc.boot.system.ServiceLocatorImpl;
import org.eclipse.edc.boot.system.injection.EdcInjectionException;
import org.eclipse.edc.boot.system.injection.lifecycle.ExtensionLifecycleManager;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.eclipse.edc.spi.system.health.HealthCheckResult;
import org.eclipse.edc.spi.system.health.HealthCheckService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;

public class CrawlerRuntime {
    public void boot(List<ConfigProp> configProps, String[] args, boolean addShutdownHook) {
        var extensionLoader = new ExtensionLoader(new ServiceLocatorImpl());
        var monitor = extensionLoader.loadMonitor(args);
        monitor.info("Booting Crawler runtime");

        var config = loadConfigWithDefaultsAndValidate(monitor, configProps);
        var context = new DefaultServiceExtensionContext(monitor, config);
        context.initialize();

        try {
            var graph = extensionLoader.buildDependencyGraph(context);

            if (!graph.isValid()) {
                throw new EdcInjectionException("The following problems occurred during dependency injection:\n%s".formatted(String.join("\n", graph.getProblems())));
            }

            ExtensionLifecycleManager.bootServiceExtensions(graph.getInjectionContainers(), context);

            var serviceExtensions = graph.getExtensions();

            if (addShutdownHook) {
                getRuntime().addShutdownHook(new Thread(() -> {
                    var iter = serviceExtensions.listIterator(serviceExtensions.size());
                    while (iter.hasPrevious()) {
                        var extension = iter.previous();
                        extension.shutdown();
                        monitor.debug("Shutdown " + extension.name());
                    }
                    monitor.info("Shutdown complete");
                }));
            }

            if (context.hasService(HealthCheckService.class)) {
                var startupStatusRef = new AtomicReference<>(HealthCheckResult.Builder.newInstance().component("CrawlerRuntime").success().build());
                var healthCheckService = context.getService(HealthCheckService.class);
                healthCheckService.addStartupStatusProvider(startupStatusRef::get);
            }

        } catch (Throwable e) {
            monitor.severe(format("Error booting runtime: %s", e.getMessage()), e);
            throw new EdcException(e);
        }

        monitor.info(format("Runtime %s ready", context.getRuntimeId()));
    }

    private Config loadConfigWithDefaultsAndValidate(Monitor monitor, List<ConfigProp> configProps) {
        var configs = new ArrayList<Config>();

        configs.add(ConfigFactory.fromEnvironment(System.getenv()));
        configs.add(ConfigFactory.fromProperties(System.getProperties()));

        var rawConfig = configs.stream().reduce(Config::merge)
            .orElseGet(ConfigFactory::empty);

        var configService = new ConfigService(monitor, configProps);
        return ConfigFactory.fromMap(configService.applyDefaults(rawConfig.getEntries()));
    }
}
