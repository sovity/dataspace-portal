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

import de.sovity.authorityportal.api.model.ConnectorStatusDto
import de.sovity.authorityportal.db.jooq.enums.ConnectorType
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentService
import de.sovity.authorityportal.web.pages.connectormanagement.toDto
import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.reporting.utils.CsvColumn
import de.sovity.authorityportal.web.services.reporting.utils.buildCsv
import jakarta.enterprise.context.ApplicationScoped
import java.io.ByteArrayInputStream

@ApplicationScoped
class ConnectorParticipantCsvReportService(
    val connectorService: ConnectorService,
    val deploymentEnvironmentService: DeploymentEnvironmentService,
    val organizationService: OrganizationService,
) {

    data class ParticipantConnectorReportRow(
        val connectorId: String,
        val connectorName: String,
        val connectorType: ConnectorType,
        val environment: String,
        val status: ConnectorStatusDto,
        val frontendUrl: String,
        val endpointUrl: String,
        val managementUrl: String,
        val hostedByOrganizationId: String?,
        val hostedByOrganizationName: String?,
    )

    val columns = listOf<CsvColumn<ParticipantConnectorReportRow>>(
        CsvColumn("Connector ID") { it.connectorId },
        CsvColumn("Name") { it.connectorName },
        CsvColumn("Type") { it.connectorType.toString() },
        CsvColumn("Environment") { it.environment },
        CsvColumn("Status") { it.status.toString() },
        CsvColumn("Frontend URL") { it.frontendUrl },
        CsvColumn("Endpoint URL") { it.endpointUrl },
        CsvColumn("Management API URL") { it.managementUrl },
        CsvColumn("Hosted By Organization ID") { it.hostedByOrganizationId ?: "" },
        CsvColumn("Hosted By Name") { it.hostedByOrganizationId ?: "" }
    )

    fun generateParticipantConnectorCsvReport(organizationId: String, environmentId: String): ByteArrayInputStream {
        deploymentEnvironmentService.assertValidEnvId(environmentId)
        val rows = buildParticipantConnectorReportRows(organizationId, environmentId)
        return buildCsv(columns, rows)
    }

    private fun buildParticipantConnectorReportRows(
        organizationId: String,
        environmentId: String
    ): List<ParticipantConnectorReportRow> {
        val connectors = connectorService.getConnectorsByOrganizationIdAndEnvironment(organizationId, environmentId)
        val organizationNames = organizationService.getAllOrganizationNames()

        return connectors.map {
            ParticipantConnectorReportRow(
                connectorId = it.connectorId,
                connectorName = it.name,
                connectorType = it.type,
                environment = it.environment,
                status = it.onlineStatus.toDto(),
                frontendUrl = it.frontendUrl,
                endpointUrl = it.endpointUrl,
                managementUrl = it.managementUrl,
                hostedByOrganizationId = it.providerOrganizationId,
                hostedByOrganizationName = organizationNames[it.providerOrganizationId]
            )
        }
    }

}
