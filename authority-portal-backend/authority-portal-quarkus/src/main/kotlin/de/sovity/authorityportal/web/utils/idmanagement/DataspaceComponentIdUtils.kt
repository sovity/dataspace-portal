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
import org.jooq.DSLContext
import org.jooq.impl.DSL

@ApplicationScoped
class DataspaceComponentIdUtils(
    val dsl: DSLContext,
    val idUtils: IdUtils,
) {
    private val dataspaceComponentIdLength = 4

    fun generateDataspaceComponentId(organizationId: String): String {
        val usedDataspaceComponentIds = getUsedDataspaceComponentIds(organizationId)
        var dataspaceComponentId: String

        do {
            dataspaceComponentId = getDataspaceComponentIdCandidate(organizationId)
        } while (usedDataspaceComponentIds.contains(dataspaceComponentId))

        return dataspaceComponentId
    }

    private fun getDataspaceComponentIdCandidate(organizationId: String): String {
        val prefix = "$organizationId.C"
        val identifier = idUtils.randomIdentifier(dataspaceComponentIdLength)
        val checksum = idUtils.calculateVerificationDigits(identifier)
        return "$prefix$identifier$checksum"
    }

    private fun getUsedDataspaceComponentIds(organizationId: String): Set<String> {
        val c = Tables.CONNECTOR
        val connectorIds = dsl.select(c.CONNECTOR_ID.`as`("dataspaceComponentId"))
            .from(c)
            .where(c.ORGANIZATION_ID.eq(organizationId))

        val cmp = Tables.COMPONENT
        val componentIds = dsl.select(cmp.ID.`as`("dataspaceComponentId"))
            .from(cmp)
            .where(cmp.ORGANIZATION_ID.eq(organizationId))

        return connectorIds.unionAll(componentIds)
            .fetchSet(DSL.field("dataspaceComponentId", String::class.java))
    }
}
