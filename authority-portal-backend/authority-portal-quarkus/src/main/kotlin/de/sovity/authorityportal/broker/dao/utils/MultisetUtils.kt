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

import org.jooq.Field
import org.jooq.TableLike
import org.jooq.impl.DSL
import kotlin.reflect.KClass

object MultisetUtils {
    fun <R : Any> multiset(table: TableLike<*>, type: KClass<R>): Field<List<R>> {
        return DSL.multiset(table).convertFrom { it.into (type.java) }
    }
}
