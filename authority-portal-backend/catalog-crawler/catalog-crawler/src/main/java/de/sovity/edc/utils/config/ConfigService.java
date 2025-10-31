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
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class ConfigService {
    private final Monitor monitor;

    /**
     * Will be evaluated in sequence to apply default config values and validate the configuration.
     */
    private final List<ConfigProp> configProps;

    /**
     * Validate config and apply defaults
     *
     * @param properties raw config
     * @return config with defaults and validation
     */
    public Map<String, String> applyDefaults(Map<String, String> properties) {
        var withDefaults = new HashMap<>(translateToDotCase(properties));
        configProps.forEach(prop -> applyDefault(withDefaults, prop));
        return withDefaults;
    }

    private void applyDefault(Map<String, String> properties, ConfigProp prop) {
        if (prop.getRelevantIf() != null && !prop.getRelevantIf().predicate(properties)) {
            return;
        }
        String value = prop.getRaw(properties);
        warnIfRequired(prop, value);

        if (value != null) {
            return;
        }

        if (prop.isRequired()) {
            var message = "Missing required Config Property: %s \"%s\" (%s)".formatted(
                prop.getProperty(),
                prop.getDescription(),
                prop.getCategory()
            );
            throw new IllegalStateException(message);
        }

        var defaultValue = prop.getDefaultValue();
        if (prop.getDefaultValueFn() != null) {
            defaultValue = prop.getDefaultValueFn().apply(properties);
        }
        if (defaultValue != null) {
            properties.put(prop.getProperty(), defaultValue);
        }
    }

    private void warnIfRequired(ConfigProp prop, String value) {
        if (value != null && prop.isWarnIfOverridden()) {
            monitor.warning("Property set to 'warn if overriden': %s, value=%s \"%s\" (%s)".formatted(
                prop.getProperty(),
                value,
                prop.getDescription(),
                prop.getCategory()
            ));
        }

        if ((value == null || value.isBlank()) && prop.isWarnIfUnset()) {
            monitor.warning("Property set to 'warn if unset': %s, value=%s \"%s\" (%s)".formatted(
                prop.getProperty(),
                value,
                prop.getDescription(),
                prop.getCategory()
            ));
        }
    }

    private Map<String, String> translateToDotCase(Map<String, String> properties) {
        Map<String, String> result = new HashMap<>();
        properties.forEach((key, value) -> {
            String newKey = toDotCase(key);
            if (result.containsKey(newKey)) {
                monitor.warning("Duplicate Config Property: %s: %s -> %s".formatted(key, result.get(newKey), value));
            }
            result.put(newKey, value);
        });
        return result;
    }

    @NotNull
    private String toDotCase(String key) {
        return key.toLowerCase().replace("_", ".");
    }

    /**
     * Helper method used in our test utilities
     *
     * @param propertiesInput properties
     * @param configProps will be evaluated in sequence to apply default config values and validate the configuration.
     * @return edc config properties
     */
    public static Map<String, String> applyDefaults(
        Map<ConfigProp, String> propertiesInput,
        List<ConfigProp> configProps
    ) {
        var configService = new ConfigService(new ConsoleMonitor(), configProps);
        var properties = propertiesInput.entrySet().stream()
            .collect(toMap(e -> e.getKey().getProperty(), Map.Entry::getValue));
        return configService.applyDefaults(properties);
    }
}
