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

package de.sovity.edc.ext.catalog.crawler.dao.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.JSONB;

/**
 * Utilities for dealing with {@link org.jooq.JSONB} fields.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonbUtils {

    /**
     * Returns the data of the given {@link JSONB} or null.
     *
     * @param jsonb {@link org.jooq.JSON}
     * @return data or null
     */
    public static String getDataOrNull(JSONB jsonb) {
        return jsonb == null ? null : jsonb.data();
    }
}
