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

package de.sovity.authorityportal.web.environment

import de.sovity.authorityportal.api.model.CentralComponentCreateRequest
import de.sovity.authorityportal.web.services.CentralComponentService
import de.sovity.authorityportal.web.thirdparty.daps.DapsClientService
import de.sovity.authorityportal.web.utils.idmanagement.ClientIdUtils
import io.quarkus.logging.Log
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes

@ApplicationScoped
class CentralComponentInit(
    val deploymentEnvironmentService: DeploymentEnvironmentService,
    val centralComponentService: CentralComponentService,
    val clientIdUtils: ClientIdUtils,
    val dapsClientService: DapsClientService,
) {

    fun onStartup(@Observes event: StartupEvent) {
        val environments = deploymentEnvironmentService.deploymentEnvironmentConfiguration.environments()

        environments.forEach { (envId, environment) ->
            environment.centralComponents().forEach { centralComponentId, component ->
                registerCentralComponent(
                    centralComponentId = centralComponentId,
                    homepageUrl = component.homepageUrl().orElse(null),
                    endpointUrl = component.endpointUrl(),
                    certificate = component.certificate(),
                    envId = envId,
                    clientId = component.clientId(),
                )
            }
        }
    }

    private fun registerCentralComponent(
        centralComponentId: String,
        homepageUrl: String?,
        endpointUrl: String,
        certificate: String,
        envId: String,
        clientId: String,
    ) {
        if (clientIdUtils.exists(clientId)) {
            Log.info("Central component or connector with this client-id already exists in the DsP DB. Skipping "
                + "registration of central component. centralComponentId=$centralComponentId, clientId=$clientId.")
            return
        }

        centralComponentService.createCentralComponent(
            centralComponentId = centralComponentId,
            environment = envId,
            clientId = clientId,
            centralComponentCreateRequest = CentralComponentCreateRequest(
                name = clientId,
                homepageUrl = homepageUrl,
                endpointUrl = endpointUrl,
                certificate = certificate
            ),
            organizationId = null,
            createdBy = null
        )

        val dapsClient = dapsClientService.forEnvironment(envId)
        dapsClient.deleteClient(clientId)
        dapsClient.createClient(clientId)
        dapsClient.addCertificate(clientId, certificate)
        dapsClient.configureMappers(clientId)

        Log.info("Central component registered. centralComponentId=$centralComponentId, clientId=$clientId.")
    }
}
