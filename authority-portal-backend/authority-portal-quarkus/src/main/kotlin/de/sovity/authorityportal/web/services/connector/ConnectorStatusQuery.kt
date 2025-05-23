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

package de.sovity.authorityportal.web.services.connector

import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.time.OffsetDateTime

@ApplicationScoped
class ConnectorStatusQuery(
    val dsl: DSLContext,
) {

    data class ConnectorStatusInfoRs(
        val connectorId: String,
        val onlineStatus: ConnectorOnlineStatus,
        val lastSuccessfulRefreshAt: OffsetDateTime?,
    )

    fun getConnectorStatusInfoByEnvironment(environmentId: String): List<ConnectorStatusInfoRs> {
        val c = Tables.CONNECTOR

        return dsl.select(c.CONNECTOR_ID, c.ONLINE_STATUS, c.LAST_SUCCESSFUL_REFRESH_AT)
            .from(c)
            .where(c.ENVIRONMENT.eq(environmentId))
            .fetchInto(ConnectorStatusInfoRs::class.java)
    }

    fun getConnectorStatusInfoByOrganizationIdAndEnvironment(organizationId: String, environmentId: String): List<ConnectorStatusInfoRs> {
        val c = Tables.CONNECTOR

        return dsl.select(c.CONNECTOR_ID, c.ONLINE_STATUS, c.LAST_SUCCESSFUL_REFRESH_AT)
            .from(c)
            .where(
                c.ENVIRONMENT.eq(environmentId),
                DSL.or(c.ORGANIZATION_ID.eq(organizationId), c.PROVIDER_ORGANIZATION_ID.eq(organizationId))
            )
            .fetchInto(ConnectorStatusInfoRs::class.java)
    }
}
