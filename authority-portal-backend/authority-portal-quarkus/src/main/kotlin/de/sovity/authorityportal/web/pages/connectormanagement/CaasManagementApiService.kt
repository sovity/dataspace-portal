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

import de.sovity.authorityportal.api.model.CaasAvailabilityResponse
import de.sovity.authorityportal.api.model.CreateCaasRequest
import de.sovity.authorityportal.api.model.CreateConnectorResponse
import de.sovity.authorityportal.db.jooq.enums.CaasStatus
import de.sovity.authorityportal.db.jooq.tables.records.OrganizationRecord
import de.sovity.authorityportal.db.jooq.tables.records.UserRecord
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentService
import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.thirdparty.caas.CaasClient
import de.sovity.authorityportal.web.thirdparty.caas.model.CaasPortalDeploymentDto
import de.sovity.authorityportal.web.utils.PersonNameUtils
import de.sovity.authorityportal.web.utils.TimeUtils
import de.sovity.authorityportal.web.utils.idmanagement.ClientIdUtils
import de.sovity.authorityportal.web.utils.idmanagement.DataspaceComponentIdUtils
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import kotlin.jvm.optionals.getOrNull

@ApplicationScoped
class CaasManagementApiService(
    val organizationService: OrganizationService,
    val dataspaceComponentIdUtils: DataspaceComponentIdUtils,
    val caasClient: CaasClient,
    val connectorService: ConnectorService,
    val deploymentEnvironmentService: DeploymentEnvironmentService,
    val clientIdUtils: ClientIdUtils,
    val userService: UserService,
    val timeUtils: TimeUtils,
    @ConfigProperty(name = "authority-portal.caas.sovity.limit-per-organization") val caasLimitPerOrganizationId: String,
    @ConfigProperty(name = "quarkus.oidc-client.sovity.client-enabled") val isCaasClientEnabled: Boolean
) {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun createCaas(
        organizationId: String,
        userId: String,
        caasRequest: CreateCaasRequest,
        environmentId: String
    ): CreateConnectorResponse {
        val curatorOrganization = organizationService.getOrganizationOrThrow(organizationId)
        val curatorUser = userService.getUserOrThrow(userId)
        val connectorId = dataspaceComponentIdUtils.generateDataspaceComponentId(organizationId)
        val clientId = clientIdUtils.generateFromConnectorId(connectorId)
        val providerOrgId = organizationService.getOrganizationIdByName("sovity GmbH")

        val apDeploymentDto = buildAuthorityPortalDeploymentDto(
            curatorOrganization = curatorOrganization,
            caasRequest = caasRequest,
            connectorId = connectorId,
            environmentId = environmentId,
            clientId = clientId,
            curatorUser = curatorUser
        )

        val configAssertion = assertValidConfig(apDeploymentDto, organizationId, environmentId)
        if (!configAssertion.valid) {
            Log.error(configAssertion.message)
            return CreateConnectorResponse.error(configAssertion.message, timeUtils.now())
        }

        caasClient.requestCaas(apDeploymentDto)

        connectorService.createCaas(
            connectorId = connectorId,
            clientId = clientId,
            organizationId = organizationId,
            name = caasRequest.connectorTitle,
            createdBy = userId,
            status = CaasStatus.PROVISIONING,
            environmentId = environmentId,
            providerOrganizationId = providerOrgId
        )

        return CreateConnectorResponse.ok(connectorId, clientId, timeUtils.now())
    }

    fun getCaasAvailabilityForOrganization(organizationId: String, environmentId: String): CaasAvailabilityResponse {
        if (!isCaasClientEnabled) {
            return CaasAvailabilityResponse(0, 0)
        }

        val caasLimit = caasLimitPerOrganizationId.toInt()
        val caasCount = connectorService.getCaasCountByOrganizationIdAndEnvironment(organizationId, environmentId)

        return CaasAvailabilityResponse(caasLimit, caasCount)
    }

    private fun assertValidConfig(
        apDeploymentDto: CaasPortalDeploymentDto,
        organizationId: String,
        environmentId: String
    ): ConfigAssertion {
        if (!connectorService.assertCaasRegistrationLimit(organizationId, environmentId)) {
            return ConfigAssertion(false, "Connector limit reached for Organization ID: $organizationId")
        }
        if (!caasClient.validateSubdomain(apDeploymentDto.subdomain.trim())) {
            return ConfigAssertion(
                false,
                "Subdomain ${apDeploymentDto.subdomain} is not available! Please choose a different one."
            )
        }
        return ConfigAssertion(true, "")
    }

    private fun buildAuthorityPortalDeploymentDto(
        curatorOrganization: OrganizationRecord,
        caasRequest: CreateCaasRequest,
        connectorId: String,
        environmentId: String,
        clientId: String,
        curatorUser: UserRecord
    ): CaasPortalDeploymentDto {
        val securityContactName = PersonNameUtils.splitName(curatorOrganization.techContactName)
        return CaasPortalDeploymentDto(
            connectorId = connectorId,
            subdomain = caasRequest.connectorSubdomain.trim(),
            clientId = clientId,
            connectorTitle = caasRequest.connectorTitle.trim(),
            connectorDescription = caasRequest.connectorDescription.trim(),
            participantOrganizationUrl = curatorOrganization.url,
            participantOrganizationLegalName = curatorOrganization.name,
            clearingHouseUrl = deploymentEnvironmentService.findByIdOrThrow(environmentId).loggingHouse().getOrNull()?.url(),
            brokerUrl = "https://this-field-is-deprecated",
            dapsJwksUrl = buildDapsJwksUrl(environmentId),
            dapsTokenUrl = buildDapsTokenUrl(environmentId),
            securityContactFirstName = securityContactName.firstName,
            securityContactLastName = securityContactName.lastName,
            securityContactEmail = curatorOrganization.techContactEmail,
            securityContactPhone = curatorOrganization.techContactPhone,
            userContactFirstName = curatorUser.firstName,
            userContactLastName = curatorUser.lastName,
            userContactEmail = curatorUser.email
        )
    }

    private fun buildDapsJwksUrl(environmentId: String): String {
        val daps = deploymentEnvironmentService.findByIdOrThrow(environmentId).daps()
        return daps.url() + "/realms/${daps.realmName()}/protocol/openid-connect/certs"
    }

    private fun buildDapsTokenUrl(environmentId: String): String {
        val daps = deploymentEnvironmentService.findByIdOrThrow(environmentId).daps()
        return daps.url() + "/realms/${daps.realmName()}/protocol/openid-connect/token"
    }

    data class ConfigAssertion(val valid: Boolean, val message: String)
}
