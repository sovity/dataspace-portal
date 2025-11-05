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

package de.sovity.authorityportal.web.pages.organizationmanagement

import de.sovity.authorityportal.api.model.organization.OrganizationDetailsDto
import de.sovity.authorityportal.api.model.organization.OrganizationLegalIdTypeDto
import de.sovity.authorityportal.api.model.organization.OrganizationOverviewEntryDto
import de.sovity.authorityportal.api.model.organization.OrganizationOverviewResult
import de.sovity.authorityportal.api.model.organization.OwnOrganizationDetailsDto
import de.sovity.authorityportal.db.jooq.tables.records.OrganizationRecord
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentService
import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserDetailService
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.services.dataoffer.DataOfferQuery
import de.sovity.authorityportal.web.services.dataoffer.DataOfferQuery.DataOfferCountRs
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrganizationInfoApiService(
    val organizationService: OrganizationService,
    val deploymentEnvironmentService: DeploymentEnvironmentService,
    val userDetailService: UserDetailService,
    val userService: UserService,
    val connectorService: ConnectorService,
    val dataOfferQuery: DataOfferQuery
) {

    fun organizationsOverview(environmentId: String): OrganizationOverviewResult {
        val organizations = organizationService.getOrganizations()
        val connectorCounts = dataOfferQuery.getConnectorCountsByOrganizationIdsForEnvironment(environmentId)
        val dataOfferCounts = dataOfferQuery.countAllOrganizationDataOffers(environmentId)
        val userCounts = userService.getUserCountsByOrganizationIds()
        val dtos = organizations.map {
            buildOrganizationOverviewEntryDto(
                organization = it,
                userCounts = userCounts,
                connectorCounts = connectorCounts,
                dataOfferCounts = dataOfferCounts,
            )
        }.sortedBy { it.name }
        return OrganizationOverviewResult(dtos)
    }

    private fun buildOrganizationOverviewEntryDto(
        organization: OrganizationRecord,
        userCounts: Map<String, Int>,
        connectorCounts: Map<String, Int>,
        dataOfferCounts: List<DataOfferCountRs>,
    ): OrganizationOverviewEntryDto {
        val dataOfferCountsByOrganizationId = dataOfferCounts.associateBy { it.organizationId }
        val organizationId = organization.id
        val liveDataOfferCount = dataOfferCountsByOrganizationId[organizationId]?.liveOffers ?: 0
        val onRequestDataOfferCount = dataOfferCountsByOrganizationId[organizationId]?.onRequestOffers ?: 0
        return OrganizationOverviewEntryDto(
            id = organizationId,
            name = organization.name,
            mainContactEmail = organization.mainContactEmail,
            userCount = userCounts[organizationId] ?: 0,
            connectorCount = connectorCounts[organizationId] ?: 0,
            dataOfferCount = liveDataOfferCount + onRequestDataOfferCount,
            liveDataOfferCount = liveDataOfferCount,
            onRequestDataOfferCount = onRequestDataOfferCount,
            registrationStatus = organization.registrationStatus.toDto()
        )
    }

    fun getOwnOrganizationInformation(organizationId: String, environmentId: String): OwnOrganizationDetailsDto {
        val organizationDetails = getOrganizationInformation(organizationId, environmentId)
        return OwnOrganizationDetailsDto(
            id = organizationDetails.id,
            name = organizationDetails.name,
            registrationStatus = organizationDetails.registrationStatus,
            createdAt = organizationDetails.createdAt,
            createdByUserId = organizationDetails.createdByUserId,
            createdByFirstName = organizationDetails.createdByFirstName,
            createdByLastName = organizationDetails.createdByLastName,

            url = organizationDetails.url,
            description = organizationDetails.description,
            businessUnit = organizationDetails.businessUnit,
            industry = organizationDetails.industry,
            mainAddress = organizationDetails.mainAddress,
            billingAddress = organizationDetails.billingAddress,
            legalIdType = organizationDetails.legalIdType,
            legalId = organizationDetails.legalId,
            commerceRegisterLocation = organizationDetails.commerceRegisterLocation,

            mainContactName = organizationDetails.mainContactName,
            mainContactEmail = organizationDetails.mainContactEmail,
            mainContactPhone = organizationDetails.mainContactPhone,
            techContactName = organizationDetails.techContactName,
            techContactEmail = organizationDetails.techContactEmail,
            techContactPhone = organizationDetails.techContactPhone,

            memberList = organizationDetails.memberList
        )
    }

    fun getOrganizationInformation(organizationId: String, environmentId: String): OrganizationDetailsDto {
        deploymentEnvironmentService.assertValidEnvId(environmentId)

        return getOrganizationDetailsDto(organizationId).also {
            val dataOffers = dataOfferQuery.countOrganizationDataOffers(environmentId, organizationId)

            it.connectorCount = connectorService.getConnectorCountByOrganizationIdAndEnvironment(organizationId, environmentId)
            it.liveDataOfferCount = dataOffers?.liveOffers ?: 0
            it.onRequestDataOfferCount = dataOffers?.onRequestOffers ?: 0
            it.dataOfferCount = it.liveDataOfferCount + it.onRequestDataOfferCount
        }
    }

    private fun getOrganizationDetailsDto(organizationId: String): OrganizationDetailsDto {
        val organization = organizationService.getOrganizationOrThrow(organizationId)
        val organizationAdmin = organization.createdBy?.let { userDetailService.getUserData(it) }
        val legalIdType = organization.legalIdType?.toDto()
        val memberList = userDetailService.getOrganizationMembers(organizationId)

        return OrganizationDetailsDto(
            id = organization.id,
            name = organization.name,
            registrationStatus = organization.registrationStatus.toDto(),
            createdAt = organization.createdAt,
            createdByUserId = organizationAdmin?.userId ?: "",
            createdByFirstName = organizationAdmin?.firstName ?: "",
            createdByLastName = organizationAdmin?.lastName ?: "",

            url = organization.url,
            description = organization.description,
            businessUnit = organization.businessUnit,
            industry = organization.industry,
            mainAddress = organization.address,
            billingAddress = organization.billingAddress,
            legalIdType = legalIdType,
            legalId = when (legalIdType) {
                OrganizationLegalIdTypeDto.TAX_ID -> organization.taxId
                OrganizationLegalIdTypeDto.COMMERCE_REGISTER_INFO -> organization.commerceRegisterNumber
                else -> null
            },
            commerceRegisterLocation = when (legalIdType) {
                OrganizationLegalIdTypeDto.COMMERCE_REGISTER_INFO -> organization.commerceRegisterLocation
                else -> null
            },

            mainContactName = organization.mainContactName,
            mainContactEmail = organization.mainContactEmail,
            mainContactPhone = organization.mainContactPhone,
            techContactName = organization.techContactName,
            techContactEmail = organization.techContactEmail,
            techContactPhone = organization.techContactPhone,

            memberList = memberList,
            memberCount = memberList.size,
            connectorCount = 0,
            dataOfferCount = 0,
            liveDataOfferCount = 0,
            onRequestDataOfferCount = 0
        )
    }
}
