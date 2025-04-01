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
package de.sovity.authorityportal.broker.dao.utils

import org.apache.commons.lang3.StringUtils
import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL

/**
 * Utilities for dealing with PostgreSQL Like Operation values
 */
object LikeUtils {
    /**
     * Create LIKE condition value for "field contains word".
     *
     * @param field         field
     * @param lowercaseWord word
     * @return "%escapedWord%"
     */
    fun contains(field: Field<String?>, lowercaseWord: String): Condition {
        if (StringUtils.isBlank(lowercaseWord)) {
            return DSL.trueCondition()
        }

        return field.likeIgnoreCase("%" + escape(lowercaseWord) + "%")
    }


    /**
     * Escapes "\", "%", "_" in given string for a LIKE operation
     *
     * @param string unescaped string
     * @return escaped string
     */
    private fun escape(string: String): String {
        return string.replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
    }
}
