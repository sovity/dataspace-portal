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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EdcConfigPropertyUtils {
    /**
     * For better refactoring it is better if the string constant is
     * found in the code as it is used and documented.
     *
     * @param envVarName e.g. "MY_EDC_PROP"
     * @return e.g. "my.edc.prop"
     */
    public static String toEdcProp(String envVarName) {
        return Arrays.stream(envVarName.split("_"))
                .map(String::toLowerCase)
                .collect(joining("."));
    }

}
