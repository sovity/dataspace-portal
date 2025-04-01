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
import de.sovity.authorityportal.db.jooq.enums.ComponentOnlineStatus
import de.sovity.authorityportal.db.jooq.enums.ComponentType
import de.sovity.authorityportal.db.jooq.tables.records.ComponentDowntimesRecord
import de.sovity.authorityportal.web.thirdparty.uptimekuma.UptimeKumaClient
import de.sovity.authorityportal.web.thirdparty.uptimekuma.model.toDb
import de.sovity.authorityportal.web.utils.TimeUtils
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.time.OffsetDateTime
import java.util.Optional

@ApplicationScoped
class ComponentStatusService(
    val dsl: DSLContext,
    val uptimeKumaClient: UptimeKumaClient,
    val timeUtils: TimeUtils,
    @ConfigProperty(name = "authority-portal.kuma.api-key") val uptimeKumaApiKey: Optional<String>
) {

    @Scheduled(every = "30s")
    fun fetchComponentStatuses() {
        if (uptimeKumaApiKey.isEmpty) {
            return
        }

        val componentsStatusByEnvironment = uptimeKumaClient.getStatusByEnvironments()

        componentsStatusByEnvironment.forEach { (env, componentStatuses) ->
            addComponentStatusIfChanged(ComponentType.DAPS, env, componentStatuses.daps?.toDb())
            addComponentStatusIfChanged(ComponentType.LOGGING_HOUSE, env, componentStatuses.loggingHouse?.toDb())
            addComponentStatusIfChanged(ComponentType.CATALOG_CRAWLER, env, componentStatuses.catalogCrawler?.toDb())
        }
    }

    fun getLatestComponentStatus(component: ComponentType, environment: String): ComponentDowntimesRecord? {
        val c = Tables.COMPONENT_DOWNTIMES

        return dsl.selectFrom(c)
            .where(c.COMPONENT.eq(component), c.ENVIRONMENT.eq(environment))
            .orderBy(c.TIME_STAMP.desc())
            .limit(1)
            .fetchOne()
    }

    fun getStatusHistoryAscSince(
        component: ComponentType,
        limit: OffsetDateTime,
        environment: String
    ): List<ComponentDowntimesRecord> {
        val c = Tables.COMPONENT_DOWNTIMES

        return dsl.selectFrom(c)
            .where(
                c.COMPONENT.eq(component),
                c.ENVIRONMENT.eq(environment),
                c.TIME_STAMP.lessThan(limit)
            )
            .orderBy(c.TIME_STAMP.desc())
            .limit(1)
            .union(
                dsl.selectFrom(c)
                    .where(
                        c.COMPONENT.eq(component),
                        c.ENVIRONMENT.eq(environment),
                        c.TIME_STAMP.greaterOrEqual(limit)
                    )
            )
            .orderBy(c.TIME_STAMP.asc())
            .fetch()
    }

    fun getUpOrDownRecordsOrderByTimestampAsc(environment: String): List<ComponentDowntimesRecord> {
        val c = Tables.COMPONENT_DOWNTIMES

        return dsl.selectFrom(c)
            .where(
                c.ENVIRONMENT.eq(environment),
                c.STATUS.eq(DSL.any(ComponentOnlineStatus.UP, ComponentOnlineStatus.DOWN))
            )
            .orderBy(c.TIME_STAMP.asc())
            .fetch()
    }

    private fun addComponentStatusIfChanged(
        component: ComponentType,
        environment: String,
        status: ComponentOnlineStatus?
    ) {
        val latestStatus = getLatestComponentStatus(component, environment)?.status

        if (status != null && latestStatus != status) {
            dsl.insertInto(Tables.COMPONENT_DOWNTIMES)
                .set(Tables.COMPONENT_DOWNTIMES.COMPONENT, component)
                .set(Tables.COMPONENT_DOWNTIMES.ENVIRONMENT, environment)
                .set(Tables.COMPONENT_DOWNTIMES.STATUS, status)
                .set(Tables.COMPONENT_DOWNTIMES.TIME_STAMP, timeUtils.now())
                .execute()

            Log.info("Component status changed. component=$component, environment=$environment, newStatus=$status, oldStatus=$latestStatus.")
        }
    }
}
