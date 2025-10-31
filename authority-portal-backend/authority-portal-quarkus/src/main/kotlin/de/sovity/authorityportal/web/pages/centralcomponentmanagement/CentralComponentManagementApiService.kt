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

package de.sovity.authorityportal.web.pages.centralcomponentmanagement

import de.sovity.authorityportal.api.model.CentralComponentCreateRequest
import de.sovity.authorityportal.api.model.CentralComponentDto
import de.sovity.authorityportal.api.model.IdResponse
import de.sovity.authorityportal.db.jooq.tables.records.ComponentRecord
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentService
import de.sovity.authorityportal.web.services.CentralComponentService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.thirdparty.daps.DapsClientService
import de.sovity.authorityportal.web.utils.TimeUtils
import de.sovity.authorityportal.web.utils.idmanagement.ClientIdUtils
import de.sovity.authorityportal.web.utils.idmanagement.DataspaceComponentIdUtils
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CentralComponentManagementApiService(
    val deploymentEnvironmentService: DeploymentEnvironmentService,
    val centralComponentService: CentralComponentService,
    val dataspaceComponentIdUtils: DataspaceComponentIdUtils,
    val clientIdUtils: ClientIdUtils,
    val dapsClientService: DapsClientService,
    val userService: UserService,
    val organizationService: OrganizationService,
    val timeUtils: TimeUtils
) {

    fun listCentralComponents(envId: String): List<CentralComponentDto> {
        deploymentEnvironmentService.assertValidEnvId(envId)
        val centralComponents = centralComponentService.getCentralComponentsByEnvironment(envId)

        return centralComponents.map { centralComponent ->
            val createdBy = centralComponent.createdBy?.let {
                userService.getUserOrThrow(it)
            }
            val organization = centralComponent.organizationId?.let {
                organizationService.getOrganizationOrThrow(it)
            }
            CentralComponentDto(
                centralComponentId = centralComponent.id,
                name = centralComponent.name,
                homepageUrl = centralComponent.homepageUrl,
                endpointUrl = centralComponent.endpointUrl,
                createdByUserFullName = (createdBy?.firstName ?: "Unknown") + " " + (createdBy?.lastName ?: ""),
                createdByOrgName = organization?.name ?: "Unknown",
                createdByOrganizationId = organization?.id ?: "Unknown"
            )
        }
    }

    fun registerCentralComponent(
        centralComponentCreateRequest: CentralComponentCreateRequest,
        userId: String,
        organizationId: String,
        envId: String
    ): IdResponse {
        deploymentEnvironmentService.assertValidEnvId(envId)

        val centralComponentId = dataspaceComponentIdUtils.generateDataspaceComponentId(organizationId)
        val clientId = clientIdUtils.generateFromConnectorId(centralComponentId)

        if (clientIdUtils.exists(clientId)) {
            Log.error("Component with this client-id already exists. connectorId=$centralComponentId, organizationId=$organizationId, userId=$userId, clientId=$clientId.")
            error("Component with this client-id already exists")
        }

        centralComponentService.createCentralComponent(
            centralComponentId = centralComponentId,
            organizationId = organizationId,
            environment = envId,
            clientId = clientId,
            centralComponentCreateRequest = centralComponentCreateRequest,
            createdBy = userId
        )

        val dapsClient = dapsClientService.forEnvironment(envId)
        dapsClient.createClient(clientId)
        dapsClient.addCertificate(clientId, centralComponentCreateRequest.certificate)
        dapsClient.configureMappers(clientId)

        Log.info("Central component registered. centralComponentId=$centralComponentId, organizationId=$organizationId, userId=$userId, clientId=$clientId.")
        return IdResponse(centralComponentId, timeUtils.now())
    }

    fun deleteCentralComponentByUser(centralComponentId: String, userId: String): IdResponse {
        val centralComponent = centralComponentService.getCentralComponentOrThrow(centralComponentId)

        deleteCentralComponent(centralComponent)
        Log.info("Central component deleted. centralComponentId=$centralComponentId, organizationId=${centralComponent.organizationId}, userId=$userId, clientId=${centralComponent.clientId}.")

        return IdResponse(centralComponentId, timeUtils.now())
    }

    fun deleteAllOrganizationCentralComponents(organizationId: String) {
        val components = centralComponentService.getCentralComponentsByOrganizationId(organizationId)
        components.forEach { deleteCentralComponent(it) }
    }

    private fun deleteCentralComponent(centralComponent: ComponentRecord) {
        centralComponentService.deleteCentralComponent(centralComponent.id)

        val dapsClient = dapsClientService.forEnvironment(centralComponent.environment)
        dapsClient.deleteClient(centralComponent.clientId)
    }
}
