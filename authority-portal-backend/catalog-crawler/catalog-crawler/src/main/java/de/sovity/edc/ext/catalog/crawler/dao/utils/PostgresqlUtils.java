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
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Collection;

/**
 * PostgreSQL + JooQ Utils
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgresqlUtils {

    /**
     * Replaces the IN operation with "field = ANY(...)"
     *
     * @param field field
     * @param values values
     * @return condition
     */
    public static Condition in(Field<String> field, Collection<String> values) {
        return field.eq(DSL.any(values.toArray(String[]::new)));
    }
}
