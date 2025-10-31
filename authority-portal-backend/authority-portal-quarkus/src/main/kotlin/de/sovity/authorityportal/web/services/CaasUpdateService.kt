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

import de.sovity.authorityportal.db.jooq.enums.CaasStatus
import de.sovity.authorityportal.db.jooq.tables.records.ConnectorRecord
import de.sovity.authorityportal.web.pages.connectormanagement.toDb
import de.sovity.authorityportal.web.thirdparty.caas.CaasClient
import de.sovity.authorityportal.web.thirdparty.caas.model.CaasStatusDto
import de.sovity.authorityportal.web.thirdparty.caas.model.CaasStatusResponse
import de.sovity.authorityportal.web.thirdparty.daps.DapsClientService
import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jooq.DSLContext

@ApplicationScoped
class CaasUpdateService(
    val dsl: DSLContext,
    val connectorService: ConnectorService,
    val caasClient: CaasClient,
    val dapsClientService: DapsClientService,

    @ConfigProperty(name = "quarkus.oidc-client.sovity.client-enabled")
    val isCaasClientEnabled: Boolean
) {

    @Scheduled(every = "30s")
    fun scheduledCaasStatusUpdate() {
        if (isCaasClientEnabled) {
            val connectors = connectorService.getAllCaas()
            val connectorStatusList = caasClient.getCaasStatus(connectors.map { it.connectorId })
            val connectorStatusMap = buildConnectorStatusMap(connectorStatusList, connectors)

            updateCaasStatus(connectorStatusMap)
            dsl.batchUpdate(connectorStatusMap.keys).execute()
        }
    }

    private fun updateCaasStatus(connectorStatusMap: Map<ConnectorRecord, CaasStatusResponse>) {
        connectorStatusMap.forEach { (connector, caasStatusResponse) ->
            updateConnectorUrls(connector, caasStatusResponse)

            if ((connector.caasStatus == CaasStatus.PROVISIONING || connector.caasStatus == CaasStatus.AWAITING_RUNNING)
                && caasStatusResponse.status == CaasStatusDto.RUNNING
            ) {
                registerCaasAtDaps(connector)
                Log.info("CaaS has been registered at DAPS. connectorId=${connector.connectorId}.")
            }

            connector.caasStatus = caasStatusResponse.status.toDb()
        }
    }

    private fun updateConnectorUrls(connector: ConnectorRecord, caasStatusResponse: CaasStatusResponse) {
        connector.frontendUrl = caasStatusResponse.frontendUrl
        connector.endpointUrl = caasStatusResponse.connectorEndpointUrl
        connector.managementUrl = caasStatusResponse.managementApiUrl
        connector.jwksUrl = caasStatusResponse.connectorJwksUrl
    }

    private fun buildConnectorStatusMap(
        connectorStatusList: List<CaasStatusResponse>,
        connectors: List<ConnectorRecord>
    ): Map<ConnectorRecord, CaasStatusResponse> {
        val connectorStatusMap = connectorStatusList
            .associateBy { it.connectorId }
            .mapNotNull { (connectorId, connectorStatusList) ->
                connectors.find { it.connectorId == connectorId }?.let { it to connectorStatusList }
            }
            .toMap()
        return connectorStatusMap
    }

    private fun registerCaasAtDaps(connector: ConnectorRecord) {
        try {
            val dapsClient = dapsClientService.forEnvironment(connector.environment)
            dapsClient.createClient(connector.clientId)
            dapsClient.addJwksUrl(connector.clientId, connector.jwksUrl)
            dapsClient.configureMappers(connector.clientId)
        } catch (e: Exception) {
            Log.error(
                "Error registering CaaS at DAPS. connectorId=${connector.connectorId}, organizationId=${connector.organizationId}.",
                e
            )
            error("Error registering CaaS at DAPS. connectorId=${connector.connectorId}, organizationId=${connector.organizationId}.")
        }

    }
}
