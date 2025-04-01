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

import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.dataoffer.DataOfferQuery
import de.sovity.authorityportal.web.services.reporting.utils.CsvColumn
import de.sovity.authorityportal.web.services.reporting.utils.buildCsv
import jakarta.enterprise.context.ApplicationScoped
import java.io.ByteArrayInputStream

@ApplicationScoped
class DataOfferCsvReportService(
    val connectorService: ConnectorService,
    val organizationService: OrganizationService,
    val dataOfferQuery: DataOfferQuery
) {

    data class DataOfferReportRow(
        val dataOfferId: String,
        val dataOfferName: String,
        val organizationId: String,
        val organizationName: String,
        val status: String,
        val dataSourceAvailability: String
    )

    val columns = listOf<CsvColumn<DataOfferReportRow>>(
        CsvColumn("Data Offer ID") { it.dataOfferId },
        CsvColumn("Data Offer Name") { it.dataOfferName },
        CsvColumn("Organization ID") { it.organizationId },
        CsvColumn("Organization Name") { it.organizationName },
        CsvColumn("Status") { it.status },
        CsvColumn("Data Source Type") { it.dataSourceAvailability }
    )

    fun generateDataOffersCsvReport(environmentId: String): ByteArrayInputStream {
        val rows = buildDataOfferReportRows(environmentId)
        return buildCsv(columns, rows)
    }

    private fun buildDataOfferReportRows(environmentId: String): List<DataOfferReportRow> {
        val connectorEndpoints = connectorService.getConnectorsByEnvironment(environmentId).map { it.endpointUrl }
        val organizationNames = organizationService.getAllOrganizationNames()
        val dataOffers = dataOfferQuery.getDataOffersForConnectorIdsAndEnvironment(environmentId, connectorEndpoints)

        return dataOffers.map {
            DataOfferReportRow(
                dataOfferId = it.dataOfferId,
                dataOfferName = it.dataOfferName,
                organizationId = it.organizationId,
                organizationName = organizationNames[it.organizationId] ?: "",
                status = it.onlineStatus.toString(),
                dataSourceAvailability = mapDataSourceAvailabilityToReadableFormat(it.dataSourceAvailability)
            )
        }
    }

    private fun mapDataSourceAvailabilityToReadableFormat(dataSourceAvailability: String): String {
        return when (dataSourceAvailability) {
            "LIVE" -> "Available"
            "ON_REQUEST" -> "On Request"
            else -> dataSourceAvailability
        }
    }
}
