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

package de.sovity.authorityportal.web.pages.connectormanagement

import de.sovity.authorityportal.api.model.ConfigureProvidedConnectorWithCertificateRequest
import de.sovity.authorityportal.api.model.ConfigureProvidedConnectorWithJwksRequest
import de.sovity.authorityportal.api.model.ConnectorDetailsDto
import de.sovity.authorityportal.api.model.ConnectorOverviewEntryDto
import de.sovity.authorityportal.api.model.ConnectorOverviewResult
import de.sovity.authorityportal.api.model.CreateConnectorRequest
import de.sovity.authorityportal.api.model.CreateConnectorResponse
import de.sovity.authorityportal.api.model.DeploymentEnvironmentDto
import de.sovity.authorityportal.api.model.IdResponse
import de.sovity.authorityportal.api.model.ProvidedConnectorOverviewEntryDto
import de.sovity.authorityportal.api.model.ProvidedConnectorOverviewResult
import de.sovity.authorityportal.api.model.ReserveConnectorRequest
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus
import de.sovity.authorityportal.db.jooq.enums.ConnectorType
import de.sovity.authorityportal.db.jooq.tables.records.ConnectorRecord
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentDtoService
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentService
import de.sovity.authorityportal.web.model.CreateConnectorParams
import de.sovity.authorityportal.web.model.UpdateProvidedConnectorParams
import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.thirdparty.caas.CaasClient
import de.sovity.authorityportal.web.thirdparty.daps.DapsClientService
import de.sovity.authorityportal.web.utils.TimeUtils
import de.sovity.authorityportal.web.utils.idmanagement.ClientIdUtils
import de.sovity.authorityportal.web.utils.idmanagement.DataspaceComponentIdUtils
import de.sovity.authorityportal.web.utils.unauthorized
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import java.net.MalformedURLException
import java.net.URI

@ApplicationScoped
class ConnectorManagementApiService(
    val deploymentEnvironmentService: DeploymentEnvironmentService,
    val deploymentEnvironmentDtoService: DeploymentEnvironmentDtoService,
    val dataspaceComponentIdUtils: DataspaceComponentIdUtils,
    val clientIdUtils: ClientIdUtils,
    val connectorService: ConnectorService,
    val organizationService: OrganizationService,
    val dapsClientService: DapsClientService,
    val caasClient: CaasClient,
    val timeUtils: TimeUtils
) {

    fun ownOrganizationConnectorDetails(connectorId: String, organizationId: String, userId: String): ConnectorDetailsDto =
        getConnectorDetails(connectorId, organizationId, userId)

    fun getConnectorDetails(connectorId: String, organizationId: String, userId: String): ConnectorDetailsDto {
        val connector = connectorService.getConnectorDetailOrThrow(connectorId)

        if (!connectorId.contains(organizationId) && connector.hostOrganizationId != organizationId) {
            Log.error("Requested connector does not belong to the organization and is not hosted by it. connectorId=$connectorId, organizationId=$organizationId, userId=$userId.")
            error("Connector ID does not match with organization or host organization")
        }

        return buildConnectorDetailsDto(connector)
    }

    fun getAuthorityConnectorDetails(connectorId: String): ConnectorDetailsDto {
        val connector = connectorService.getConnectorDetailOrThrow(connectorId)
        return buildConnectorDetailsDto(connector)
    }

    private fun buildConnectorDetailsDto(connector: ConnectorService.ConnectorDetailRs): ConnectorDetailsDto {
        return ConnectorDetailsDto(
            connectorId = connector.connectorId,
            type = connector.type.toDto(),
            organizationName = connector.orgName,
            organizationId = connector.organizationId,
            hostOrganizationName = if (connector.type == ConnectorType.CAAS) "sovity GmbH" else connector.hostName,
            hostOrganizationId = connector.hostOrganizationId,
            environment = deploymentEnvironmentDtoService.findByIdOrThrow(connector.environment),
            connectorName = connector.connectorName,
            location = connector.location,
            frontendUrl = connector.frontendUrl,
            endpointUrl = connector.endpointUrl,
            managementUrl = connector.managementUrl,
            status = buildConnectorStatusFromConnectorDetails(connector),
            clientId = connector.clientId
        )
    }

    fun listOrganizationConnectors(organizationId: String, environmentId: String): ConnectorOverviewResult {
        deploymentEnvironmentService.assertValidEnvId(environmentId)

        val connectors = connectorService.getConnectorsByOrganizationIdAndEnvironment(organizationId, environmentId)
        return buildConnectorOverview(connectors)
    }

    fun listAllConnectors(environmentId: String): ConnectorOverviewResult {
        deploymentEnvironmentService.assertValidEnvId(environmentId)

        val connectors = connectorService.getConnectorsByEnvironment(environmentId)
        return buildConnectorOverview(connectors, showFrontendUrl = false)
    }

    private fun buildConnectorOverview(
        connectors: List<ConnectorRecord>,
        showFrontendUrl: Boolean = true
    ): ConnectorOverviewResult {
        val orgNames = organizationService.getAllOrganizationNames()

        val connectorDtos = connectors.map {
            ConnectorOverviewEntryDto(
                id = it.connectorId,
                organizationName = orgNames[it.organizationId] ?: "",
                hostName = if (it.type == ConnectorType.CAAS) "sovity GmbH" else orgNames[it.providerOrganizationId] ?: "",
                type = it.type.toDto(),
                environment = deploymentEnvironmentDtoService.findByIdOrThrow(it.environment),
                name = it.name,
                status = if (it.type == ConnectorType.CAAS) it.caasStatus.toDto() else it.onlineStatus.toDto(),
                frontendUrl = if (showFrontendUrl) it.frontendUrl else null
            )
        }
        return ConnectorOverviewResult(connectorDtos)
    }

    fun listServiceProvidedConnectors(organizationId: String, environmentId: String): ProvidedConnectorOverviewResult {
        deploymentEnvironmentService.assertValidEnvId(environmentId)

        val connectors = connectorService.getConnectorsByHostOrganizationId(organizationId, environmentId)
        val orgNames = organizationService.getAllOrganizationNames()

        val connectorDtos = connectors.map {
            ProvidedConnectorOverviewEntryDto(
                id = it.connectorId,
                customerOrgName = orgNames[it.organizationId] ?: "",
                type = it.type.toDto(),
                environment = deploymentEnvironmentDtoService.findByIdOrThrow(it.environment),
                name = it.name,
                status = buildConnectorStatusFromConnectorRecord(it),
                frontendUrl = it.frontendUrl
            )
        }

        return ProvidedConnectorOverviewResult(connectorDtos)
    }

    fun createOwnConnector(
        connector: CreateConnectorRequest,
        organizationId: String,
        userId: String,
        deploymentEnvId: String = "test"
    ): CreateConnectorResponse {
        deploymentEnvironmentService.assertValidEnvId(deploymentEnvId)

        val createConnectorParams = CreateConnectorParams(
            name = connector.name,
            location = connector.location,
            frontendUrl = removeUrlTrailingSlash(connector.frontendUrl),
            endpointUrl = removeUrlTrailingSlash(connector.endpointUrl),
            managementUrl = removeUrlTrailingSlash(connector.managementUrl),
            certificate = connector.certificate,
            jwksUrl = null
        )

        validateUrlConfiguration(createConnectorParams.frontendUrl, createConnectorParams.endpointUrl, createConnectorParams.managementUrl)?.let { return it }

        val connectorId = dataspaceComponentIdUtils.generateDataspaceComponentId(organizationId)
        val clientId = clientIdUtils.generateFromConnectorId(connectorId)

        if (clientIdUtils.exists(clientId, deploymentEnvId)) {
            Log.error("Connector with this certificate already exists. connectorId=$connectorId, organizationId=$organizationId, userId=$userId, clientId=$clientId, deploymentEnvId=$deploymentEnvId.")
            return CreateConnectorResponse.error("Connector with this certificate already exists.", timeUtils.now())
        }

        connectorService.createOwnConnector(
            connectorId = connectorId,
            organizationId = organizationId,
            environment = deploymentEnvId,
            clientId = clientId,
            createConnectorParams = createConnectorParams,
            createdBy = userId
        )

        registerConnectorAtDaps(
            clientId = clientId,
            connectorId = connectorId,
            certificate = createConnectorParams.certificate,
            jwksUrl = createConnectorParams.jwksUrl,
            deploymentEnvId = deploymentEnvId
        )

        Log.info("Connector for own organization registered. connectorId=$connectorId, organizationId=$organizationId, userId=$userId.")
        return CreateConnectorResponse.ok(connectorId, clientId, timeUtils.now())
    }

    fun reserveProvidedConnector(
        providerOrganizationId: String,
        userId: String,
        deploymentEnvId: String,
        connector: ReserveConnectorRequest
    ): CreateConnectorResponse {
        deploymentEnvironmentService.assertValidEnvId(deploymentEnvId)

        val connectorId = dataspaceComponentIdUtils.generateDataspaceComponentId(connector.customerOrganizationId)
        val clientId = clientIdUtils.generateFromConnectorId(connectorId)

        if (clientIdUtils.exists(clientId, deploymentEnvId)) {
            Log.error("Connector with this client ID already exists. connectorId=$connectorId, userId=$userId, clientId=$clientId, deploymentEnvId=$deploymentEnvId.")
            return CreateConnectorResponse.error("Connector with this client ID already exists.", timeUtils.now())
        }

        connectorService.createReservedConnector(
            connectorId = connectorId,
            clientId = clientId,
            organizationId = connector.customerOrganizationId,
            providerOrganizationId = providerOrganizationId,
            name = connector.name,
            location = connector.location,
            environment = deploymentEnvId,
            createdBy = userId
        )

        return CreateConnectorResponse.ok(connectorId, clientId, timeUtils.now())
    }

    fun configureProvidedConnectorWithCertificate(
        connector: ConfigureProvidedConnectorWithCertificateRequest,
        connectorId: String,
        providerOrganizationId: String,
        userId: String,
        deploymentEnvId: String
    ): CreateConnectorResponse {
        val connectorData = UpdateProvidedConnectorParams(
            frontendUrl = removeUrlTrailingSlash(connector.frontendUrl),
            endpointUrl = removeUrlTrailingSlash(connector.endpointUrl),
            managementUrl = removeUrlTrailingSlash(connector.managementUrl),
            certificate = connector.certificate,
            jwksUrl = null
        )
        return finalizeProvidedConnectorConfig(connectorData, connectorId, providerOrganizationId, userId, deploymentEnvId)
    }

    fun configureProvidedConnectorWithJwks(
        connector: ConfigureProvidedConnectorWithJwksRequest,
        connectorId: String,
        providerOrganizationId: String,
        userId: String,
        deploymentEnvId: String
    ): CreateConnectorResponse {
        val connectorData = UpdateProvidedConnectorParams(
            frontendUrl = removeUrlTrailingSlash(connector.frontendUrl),
            endpointUrl = removeUrlTrailingSlash(connector.endpointUrl),
            managementUrl = removeUrlTrailingSlash(connector.managementUrl),
            certificate = null,
            jwksUrl = connector.jwksUrl
        )
        return finalizeProvidedConnectorConfig(connectorData, connectorId, providerOrganizationId, userId, deploymentEnvId)
    }

    private fun finalizeProvidedConnectorConfig(
        connectorParams: UpdateProvidedConnectorParams,
        connectorId: String,
        providerOrganizationId: String,
        userId: String,
        deploymentEnvId: String = "test"
    ): CreateConnectorResponse {
        deploymentEnvironmentService.assertValidEnvId(deploymentEnvId)

        val existingConnector = connectorService.getConnectorByIdOrThrow(connectorId)

        assertProvidedConnectorConfigurable(existingConnector, providerOrganizationId)
        validateUrlConfiguration(connectorParams.frontendUrl, connectorParams.endpointUrl, connectorParams.managementUrl)?.let { return it }

        existingConnector.apply {
            frontendUrl = connectorParams.frontendUrl
            endpointUrl = connectorParams.endpointUrl
            managementUrl = connectorParams.managementUrl
            jwksUrl = connectorParams.jwksUrl
            type = ConnectorType.PROVIDED
            update()
        }

        registerConnectorAtDaps(
            clientId = existingConnector.clientId,
            connectorId = connectorId,
            certificate = connectorParams.certificate,
            jwksUrl = connectorParams.jwksUrl,
            deploymentEnvId = deploymentEnvId
        )

        Log.info("Connector for foreign organization configured and registered in DAPS. connectorId=$connectorId, customerOrganizationId=${existingConnector.organizationId}, userId=$userId.")
        return CreateConnectorResponse.ok(connectorId, existingConnector.clientId, timeUtils.now())
    }

    private fun assertProvidedConnectorConfigurable(connector: ConnectorRecord, userOrganizationId: String) {
        if (connector.providerOrganizationId != userOrganizationId) {
            Log.error("User is not allowed to configure this connector. connectorId=${connector.connectorId}, userOrganizationId=$userOrganizationId.")
            unauthorized("User is not allowed to configure this connector.")
        }

        if (connector.type != ConnectorType.CONFIGURING) {
            Log.error("Connector is not in configuring state. connectorId=${connector.connectorId}.")
            unauthorized("Connector is not in configuring state.")
        }
    }

    private fun isValidUrlConfiguration(
        frontendUrlString: String?,
        endpointUrlString: String?,
        managementUrlString: String?
    ): Boolean {
        if (frontendUrlString == null || endpointUrlString == null || managementUrlString == null) {
            return false
        }

        return try {
            listOf(frontendUrlString, endpointUrlString, managementUrlString)
                .map { URI.create(it).scheme }
                .all { it == "https" }
        } catch (e: MalformedURLException) {
            false
        }
    }

    private fun removeUrlTrailingSlash(url: String): String {
        return url.trim().removeSuffix("/")
    }

    fun deleteConnectorById(
        connectorId: String,
        organizationId: String,
        userId: String
    ): IdResponse {
        val connector = connectorService.getConnectorOrThrow(connectorId)

        deleteConnector(connector)
        Log.info("Connector unregistered. connectorId=$connectorId, organizationId=$organizationId, userId=$userId.")

        return IdResponse(connectorId, timeUtils.now())
    }

    fun deleteAllOrganizationConnectors(organizationId: String) {
        val connectors = connectorService.getConnectorsByOrganizationId(organizationId)
        connectors.forEach { deleteConnector(it) }
    }

    private fun deleteConnector(connector: ConnectorRecord) {
        val deploymentEnvId = connector.environment

        deploymentEnvironmentService.assertValidEnvId(deploymentEnvId)

        if (connector.type == ConnectorType.CAAS) {
            caasClient.deleteCaas(listOf(connector.connectorId))
        }

        dapsClientService.forEnvironment(deploymentEnvId).deleteClient(connector.clientId)
        connectorService.deleteConnectorFromDb(connector.connectorId)
    }

    fun getAllDeploymentEnvironment(): List<DeploymentEnvironmentDto> {
        return deploymentEnvironmentDtoService.findAll()
    }

    private fun registerConnectorAtDaps(
        clientId: String,
        connectorId: String,
        certificate: String?,
        jwksUrl: String?,
        deploymentEnvId: String
    ) {
        val dapsClient = dapsClientService.forEnvironment(deploymentEnvId)
        dapsClient.createClient(clientId)

        if (!certificate.isNullOrEmpty()) {
            dapsClient.addCertificate(clientId, certificate)
        } else if (!jwksUrl.isNullOrEmpty()) {
            dapsClient.addJwksUrl(clientId, jwksUrl)
        } else {
            error("Either certificate or JWKS URL must be provided, connectorId=$connectorId, clientId=$clientId.")
        }

        dapsClient.configureMappers(clientId)
    }

    private fun buildConnectorStatusFromConnectorDetails(connector: ConnectorService.ConnectorDetailRs) =
        if (connector.type == ConnectorType.CAAS) {
            connector.caasStatus!!.toDto()
        } else {
            filterDeadConnectorStatus(connector.onlineStatus!!).toDto()
        }

    private fun buildConnectorStatusFromConnectorRecord(it: ConnectorRecord) =
        if (it.type == ConnectorType.CAAS) {
            it.caasStatus.toDto()
        } else {
            filterDeadConnectorStatus(it.onlineStatus).toDto()
        }

    private fun validateUrlConfiguration(frontendUrl: String?, endpointUrl: String?, managementUrl: String?): CreateConnectorResponse? {
        if (isValidUrlConfiguration(frontendUrl, endpointUrl, managementUrl)) return null
        else {
            Log.error("Connector URL is not valid. frontendUrl=${frontendUrl}, endpointUrl=${endpointUrl}, managementUrl=${managementUrl}.")
            return CreateConnectorResponse.error("Connector URL is not valid.", timeUtils.now())
        }
    }

    private fun filterDeadConnectorStatus(status: ConnectorOnlineStatus) =
        if (status == ConnectorOnlineStatus.DEAD) {
            ConnectorOnlineStatus.OFFLINE
        } else {
            status
        }
}
