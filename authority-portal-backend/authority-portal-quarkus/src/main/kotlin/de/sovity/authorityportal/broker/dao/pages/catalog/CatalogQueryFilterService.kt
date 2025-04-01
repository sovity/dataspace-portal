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
package de.sovity.authorityportal.broker.dao.pages.catalog

import de.sovity.authorityportal.broker.services.api.filtering.CatalogSearchService
import de.sovity.authorityportal.broker.services.api.filtering.model.FilterAttributeApplied
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus
import de.sovity.authorityportal.db.jooq.tables.Connector
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentService
import de.sovity.authorityportal.web.utils.TimeUtils
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.Condition
import org.jooq.impl.DSL

@ApplicationScoped
class CatalogQueryFilterService(
    val catalogSearchService: CatalogSearchService,
    val deploymentEnvironmentService: DeploymentEnvironmentService,
    val timeUtils: TimeUtils
) {
    fun filterDbQuery(
        environment: String,
        fields: CatalogQueryFields,
        searchQuery: String?,
        filters: List<FilterAttributeApplied>
    ): Condition {
        val conditions = ArrayList<Condition>()
        conditions.add(fields.connectorTable.ENVIRONMENT.eq(environment))
        conditions.add(visibleConnectorsOfEnvironment(environment, fields.connectorTable))
        conditions.add(catalogSearchService.filterBySearch(fields, searchQuery))
        conditions.addAll(filters.mapNotNull { it.filterConditionOrNull }.map { it(fields) })
        return DSL.and(conditions)
    }

    private fun visibleConnectorsOfEnvironment(environment: String, c: Connector): Condition {
        val maxOfflineDuration = deploymentEnvironmentService.findByIdOrThrow(environment)
            .dataCatalog()
            .hideOfflineDataOffersAfter()
        val maxOfflineDurationNotExceeded = c.LAST_SUCCESSFUL_REFRESH_AT
            .greaterThan(timeUtils.now().minus(maxOfflineDuration))
        val isOnline = c.ONLINE_STATUS.eq(ConnectorOnlineStatus.ONLINE)

        return DSL.or(isOnline, maxOfflineDurationNotExceeded)
    }
}
