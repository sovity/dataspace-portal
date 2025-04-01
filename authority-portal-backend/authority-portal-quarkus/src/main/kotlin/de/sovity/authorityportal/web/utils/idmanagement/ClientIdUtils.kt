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

package de.sovity.authorityportal.web.utils.idmanagement

import de.sovity.authorityportal.db.jooq.Tables
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.jooq.DSLContext
import java.util.Locale

@ApplicationScoped
class ClientIdUtils {
    @Inject
    lateinit var dsl: DSLContext

    fun generateFromConnectorId(connectorId: String): String {
        return connectorId
    }

    fun exists(clientId: String): Boolean {
        val c = Tables.CONNECTOR
        val connectorClientIds = dsl.select(c.CLIENT_ID.`as`("clientId"))
            .from(c)
            .where(c.CLIENT_ID.eq(clientId))

        val cmp = Tables.COMPONENT
        val centralComponentClientIds = dsl.select(cmp.CLIENT_ID.`as`("clientId"))
            .from(cmp)
            .where(cmp.CLIENT_ID.eq(clientId))

        return dsl.fetchExists(connectorClientIds.unionAll(centralComponentClientIds))
    }
}
