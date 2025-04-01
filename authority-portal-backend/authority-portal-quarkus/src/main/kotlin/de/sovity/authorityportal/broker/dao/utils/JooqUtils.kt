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

import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL

/**
 * Replaces the IN operation with "field = ANY(...)"
 *
 * @param values values
 * @return condition
 */
fun Field<String>.eqAny(values: Collection<String>): Condition = this.eq(DSL.any(*values.toTypedArray()))

fun <T, R> Field<T>.mapInline(mapping: Map<T, R>, default: R): Field<R> {
    if (mapping.isEmpty()) {
        return DSL.`val`(default)
    }

    val entries = mapping.entries.toList()

    val first = entries.first()
    var caseExpression = DSL.case_(this).`when`(first.key, first.value)

    entries.drop(1).forEach {
        caseExpression = caseExpression.`when`(it.key, it.value)
    }

    return caseExpression.else_(DSL.`val`(default))
}
