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

package de.sovity.edc.utils.config.utils;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.stream.Stream;

@UtilityClass
public class UrlPathUtils {
    public static String urlPathJoin(String... parts) {
        return Stream.of(parts)
            .filter(Objects::nonNull)
            .filter(it -> !it.isEmpty())
            .reduce("", (cur, add) -> {
                // Join with a single slash
                if (cur.endsWith("/") && add.startsWith("/")) {
                    return cur + add.substring(1);
                } else if (!cur.isEmpty() && !cur.endsWith("/") && !add.startsWith("/")) {
                    return cur + "/" + add;
                } else {
                    return cur + add;
                }
            });
    }
}
