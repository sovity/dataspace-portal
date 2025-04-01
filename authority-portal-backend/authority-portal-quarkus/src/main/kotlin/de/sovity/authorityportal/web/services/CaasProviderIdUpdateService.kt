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

package de.sovity.authorityportal.web.services

import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.ConnectorType
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext

@ApplicationScoped
class CaasProviderIdUpdateService(
    val dsl: DSLContext,
    val organizationService: OrganizationService
) {

    @Scheduled(every = "24h")
    fun updateMissingProviderIds() {
        val c = Tables.CONNECTOR
        val connectors = dsl.selectFrom(c)
            .where(c.PROVIDER_ORGANIZATION_ID.isNull, c.TYPE.eq(ConnectorType.CAAS))
            .fetch()
            .toSet()

        if (connectors.isEmpty()) {
            return
        }

        val orgId = organizationService.getOrganizationIdByName("sovity GmbH")

        connectors.forEach {
            it.providerOrganizationId = orgId
        }
        dsl.batchUpdate(connectors).execute()
    }
}
