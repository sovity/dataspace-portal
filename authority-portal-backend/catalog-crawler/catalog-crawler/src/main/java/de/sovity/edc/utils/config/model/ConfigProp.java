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

package de.sovity.edc.utils.config.model;

import lombok.*;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

@Setter
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigProp {
    @NotNull
    private String property;

    @NotNull
    private String category;

    @NotNull
    private String description;

    /**
     * Turns off all required / defaulting logic, if false
     */
    private ConfigPropRequiredIfFn relevantIf;

    private boolean required;
    private ConfigPropRequiredIfFn requiredIf;
    private String defaultValue;
    private ConfigPropDefaultValueFn defaultValueFn;

    private boolean warnIfOverridden;
    private boolean warnIfUnset;

    public ConfigProp also(Consumer<ConfigProp> fn) {
        fn.accept(this);
        return this;
    }

    @Nullable
    public String getRaw(Map<String, String> props) {
        return props.get(property);
    }

    public String getStringOrNull(Config config) {
        return config.getString(property, null);
    }

    public String getStringOrThrow(Config config) {
        return config.getString(property);
    }

    public String getStringOrEmpty(Config config) {
        // Default should already be handled by ConfigProp
        return config.getString(property, "");
    }

    public Boolean getBoolean(Config config) {
        // Default should already be handled by ConfigProp
        return config.getBoolean(property);
    }

    public Integer getInt(Config config) {
        // Default should already be handled by ConfigProp
        return config.getInteger(property);
    }
}
