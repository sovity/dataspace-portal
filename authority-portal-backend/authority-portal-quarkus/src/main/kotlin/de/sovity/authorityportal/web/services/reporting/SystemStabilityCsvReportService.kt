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

package de.sovity.authorityportal.web.services.reporting

import de.sovity.authorityportal.db.jooq.enums.ComponentOnlineStatus
import de.sovity.authorityportal.db.jooq.enums.ComponentType
import de.sovity.authorityportal.db.jooq.tables.records.ComponentDowntimesRecord
import de.sovity.authorityportal.web.services.ComponentStatusService
import de.sovity.authorityportal.web.services.reporting.utils.CsvColumn
import de.sovity.authorityportal.web.services.reporting.utils.buildCsv
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.io.ByteArrayInputStream
import java.time.Duration
import java.time.OffsetDateTime

@ApplicationScoped
class SystemStabilityCsvReportService {

    @Inject
    lateinit var componentStatusService: ComponentStatusService

    data class SystemStabilityReportRow(
        val component: ComponentType,
        val environment: String,
        val beginDowntime: OffsetDateTime,
        val endDowntime: OffsetDateTime?,
        val duration: Duration?
    )

    val columns = listOf<CsvColumn<SystemStabilityReportRow>>(
        CsvColumn("Component") { it.component.toString() },
        CsvColumn("Environment") { it.environment },
        CsvColumn("Begin Downtime") { it.beginDowntime.toString() },
        CsvColumn("End Downtime") { it.endDowntime?.toString() ?: "" },
        CsvColumn("Duration") { it.duration?.toString() ?: "" }
    )

    fun generateSystemStabilityCsvReport(environmentId: String): ByteArrayInputStream {
        val rows = buildSystemStabilityReportRows(environmentId)
        return buildCsv(columns, rows)
    }

    internal fun buildSystemStabilityReportRows(environmentId: String): List<SystemStabilityReportRow> {
        val downtimeRecords = componentStatusService.getUpOrDownRecordsOrderByTimestampAsc(environmentId)

        return downtimeRecords.withIndex()
            .filter { (_, it) -> it.status == ComponentOnlineStatus.DOWN }
            .map { (i, it) ->
                val component = it.component
                val environment = it.environment
                val beginDowntime = it.timeStamp
                val endDowntime = findNextOnlineStatusRecord(downtimeRecords, i, component)
                val duration = endDowntime?.let { Duration.between(beginDowntime, it) }?.abs()

                SystemStabilityReportRow(
                    component = component,
                    environment = environment,
                    beginDowntime = beginDowntime,
                    endDowntime = endDowntime,
                    duration = duration
                )
            }
            .reversed()
    }

    private fun findNextOnlineStatusRecord(
        componentStatusLogPerEnvironment: List<ComponentDowntimesRecord>,
        currentIndex: Int,
        componentType: ComponentType
    ): OffsetDateTime? {
        return componentStatusLogPerEnvironment.drop(currentIndex).firstOrNull {
            it.status == ComponentOnlineStatus.UP && it.component == componentType
        }?.timeStamp
    }
}
