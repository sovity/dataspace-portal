package de.sovity.authorityportal.web.pages.connectormanagement

import de.sovity.authorityportal.api.model.CaasAvailabilityResponse
import de.sovity.authorityportal.api.model.CreateCaasRequest
import de.sovity.authorityportal.api.model.CreateConnectorResponse
import de.sovity.authorityportal.db.jooq.enums.CaasStatus
import de.sovity.authorityportal.db.jooq.tables.records.OrganizationRecord
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentService
import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.thirdparty.caas.CaasClient
import de.sovity.authorityportal.web.thirdparty.caas.model.CaasPortalDeploymentDto
import de.sovity.authorityportal.web.utils.PersonNameUtils
import de.sovity.authorityportal.web.utils.idmanagement.ClientIdUtils
import de.sovity.authorityportal.web.utils.idmanagement.DataspaceComponentIdUtils
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class CaasManagementApiService {

    @Inject
    lateinit var organizationService: OrganizationService

    @Inject
    lateinit var dataspaceComponentIdUtils: DataspaceComponentIdUtils

    @Inject
    lateinit var caasClient: CaasClient

    @Inject
    lateinit var connectorService: ConnectorService

    @Inject
    lateinit var deploymentEnvironmentService: DeploymentEnvironmentService

    @Inject
    lateinit var clientIdUtils: ClientIdUtils

    @ConfigProperty(name = "authority-portal.caas.sovity.limit-per-mdsid")
    lateinit var caasLimitPerMdsId: String

    fun createCaas(
        mdsId: String,
        userId: String,
        caasRequest: CreateCaasRequest,
        environmentId: String
    ): CreateConnectorResponse {
        val curatorOrganization = organizationService.getOrganizationOrThrow(mdsId)
        val connectorId = dataspaceComponentIdUtils.generateDataspaceComponentId(mdsId)
        val clientId = clientIdUtils.generateFromConnectorId(connectorId)

        val apDeploymentDto = buildAuthorityPortalDeploymentDto(curatorOrganization, caasRequest, connectorId, environmentId, clientId)

        val configAssertion = assertValidConfig(apDeploymentDto, mdsId, environmentId)
        if (!configAssertion.valid) {
            Log.error(configAssertion.message)
            return CreateConnectorResponse.error(configAssertion.message)
        }

        caasClient.requestCaas(apDeploymentDto)

        connectorService.createCaas(
            connectorId = connectorId,
            clientId = clientId,
            mdsId = mdsId,
            name = caasRequest.connectorTitle,
            createdBy = userId,
            status = CaasStatus.PROVISIONING,
            environmentId = environmentId
        )

        return CreateConnectorResponse.ok(connectorId)
    }

    fun getFreeCaasUsageForOrganization(mdsId: String, environmentId: String): CaasAvailabilityResponse {
        val caasLimit = caasLimitPerMdsId.toInt()
        val caasCount = connectorService.getCaasCountByMdsId(mdsId, environmentId)

        return CaasAvailabilityResponse(caasLimit, caasCount)
    }

    private fun assertValidConfig(apDeploymentDto: CaasPortalDeploymentDto, mdsId: String, environmentId: String): ConfigAssertion {
        if (!connectorService.assertCaasRegistrationLimit(mdsId, environmentId)) {
            return ConfigAssertion(false, "Connector limit reached for mdsId: $mdsId")
        }
        if (!caasClient.validateSubdomain(apDeploymentDto.subdomain)) {
            return ConfigAssertion(false, "Subdomain ${apDeploymentDto.subdomain} is not available! mdsId: $mdsId")
        }
        return ConfigAssertion(true, "")
    }

    private fun buildAuthorityPortalDeploymentDto(curatorOrganization: OrganizationRecord, caasRequest: CreateCaasRequest, connectorId: String, environmentId: String, clientId: String): CaasPortalDeploymentDto {
        val securityContactName = PersonNameUtils.splitName(curatorOrganization.techContactName)
        val userContactName = PersonNameUtils.splitName(curatorOrganization.mainContactName)
        return CaasPortalDeploymentDto(
            connectorId = connectorId,
            subdomain = caasRequest.connectorSubdomain,
            clientId = clientId,
            connectorTitle = caasRequest.connectorTitle,
            connectorDescription = caasRequest.connectorDescription,
            participantOrganizationUrl = curatorOrganization.url,
            participantOrganizationLegalName = curatorOrganization.name,
            clearingHouseUrl = deploymentEnvironmentService.findByIdOrThrow(environmentId).loggingHouse().url(),
            brokerUrl = deploymentEnvironmentService.findByIdOrThrow(environmentId).broker().url(),
            dapsTokenUrl = buildDapsTokenUrl(environmentId),
            dapsJwksUrl = buildDapsJwksUrl(environmentId),
            securityContactFirstName = securityContactName.firstName,
            securityContactLastName = securityContactName.lastName,
            securityContactEmail = curatorOrganization.techContactEmail,
            securityContactPhone = curatorOrganization.techContactPhone,
            userContactFirstName = userContactName.firstName,
            userContactLastName = userContactName.lastName,
            userContactEmail = curatorOrganization.mainContactEmail
        )
    }

    private fun buildDapsTokenUrl(environmentId: String): String {
        val daps = deploymentEnvironmentService.findByIdOrThrow(environmentId).daps()
        return daps.url() + "/realms/${daps.realmName()}/protocol/openid-connect/token"
    }

    private fun buildDapsJwksUrl(environmentId: String): String {
        val daps = deploymentEnvironmentService.findByIdOrThrow(environmentId).daps()
        return daps.url() + "/realms/${daps.realmName()}/protocol/openid-connect/certs"
    }

    data class ConfigAssertion(val valid: Boolean, val message: String)
}
