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

import de.sovity.authorityportal.broker.dao.utils.LikeUtils.contains
import de.sovity.authorityportal.web.utils.lowercaseWords
import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL

/**
 * DB Search Queries
 */
object SearchUtils {
    /**
     * Simple search
     * <br></br>
     * All search query words must be contained in at least one search target.
     *
     * @param searchQuery   search query
     * @param searchTargets target fields
     * @return JOOQ Condition
     */
    fun simpleSearch(searchQuery: String?, searchTargets: List<Field<String?>>): Condition {
        val words = searchQuery.lowercaseWords()
        return DSL.and(words.map { anySearchTargetContains(searchTargets, it) })
    }

    private fun anySearchTargetContains(searchTargets: List<Field<String?>>, word: String): Condition {
        return DSL.or(searchTargets.map { contains(it, word) })
    }
}
