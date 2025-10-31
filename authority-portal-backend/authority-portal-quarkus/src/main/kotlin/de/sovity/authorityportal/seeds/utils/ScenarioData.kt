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

package de.sovity.authorityportal.seeds.utils

import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.ConnectorContractOffersExceeded
import de.sovity.authorityportal.db.jooq.enums.ConnectorDataOffersExceeded
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus
import de.sovity.authorityportal.db.jooq.enums.ConnectorType
import de.sovity.authorityportal.db.jooq.enums.CrawlerEventStatus
import de.sovity.authorityportal.db.jooq.enums.CrawlerEventType
import de.sovity.authorityportal.db.jooq.enums.OrganizationLegalIdType
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.enums.UserOnboardingType
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.db.jooq.tables.records.ComponentRecord
import de.sovity.authorityportal.db.jooq.tables.records.ConnectorRecord
import de.sovity.authorityportal.db.jooq.tables.records.ContractOfferRecord
import de.sovity.authorityportal.db.jooq.tables.records.CrawlerEventLogRecord
import de.sovity.authorityportal.db.jooq.tables.records.DataOfferRecord
import de.sovity.authorityportal.db.jooq.tables.records.DataOfferViewCountRecord
import de.sovity.authorityportal.db.jooq.tables.records.OrganizationRecord
import de.sovity.authorityportal.db.jooq.tables.records.UserRecord
import de.sovity.edc.ce.api.common.model.DataSourceAvailability
import de.sovity.edc.ce.api.common.model.UiAsset
import de.sovity.edc.ce.api.common.model.UiPolicy
import de.sovity.edc.ce.api.common.model.UiPolicyExpression
import org.jooq.DSLContext
import org.jooq.JSONB
import java.time.OffsetDateTime
import java.util.UUID


/**
 * Helper class for building Test Data for both Integration Tests and Local E2E Development
 */
class ScenarioData {

    // lists of objects for each table to be inserted
    private val users = mutableListOf<UserRecord>()
    private val organizations = mutableListOf<OrganizationRecord>()
    private val connectors = mutableListOf<ConnectorRecord>()
    private val components = mutableListOf<ComponentRecord>()
    private val dataOffers = mutableListOf<DataOfferRecord>()
    private val dataOfferViews = mutableListOf<DataOfferViewCountRecord>()
    private val contractOffers = mutableListOf<ContractOfferRecord>()
    private val crawlerEventLogEntries = mutableListOf<CrawlerEventLogRecord>()

    companion object {
        fun uninstall(dsl: DSLContext) {
            dsl.deleteFrom(Tables.CRAWLER_EVENT_LOG).execute()
            dsl.deleteFrom(Tables.CONTRACT_OFFER).execute()
            dsl.deleteFrom(Tables.DATA_OFFER_VIEW_COUNT).execute()
            dsl.deleteFrom(Tables.DATA_OFFER).execute()
            dsl.deleteFrom(Tables.COMPONENT).execute()
            dsl.deleteFrom(Tables.CONNECTOR).execute()
            dsl.update(Tables.USER)
                .set(Tables.USER.ORGANIZATION_ID, null as String?)
                .execute()
            dsl.deleteFrom(Tables.ORGANIZATION).execute()
            dsl.deleteFrom(Tables.USER).execute()
        }
    }

    fun install(dsl: DSLContext) {
        val userOrgMap = users.associate { it.id to it.organizationId }
        users.forEach {
            it.organizationId = null
        }

        dsl.batchInsert(users).execute()
        dsl.batchInsert(organizations).execute()

        users.forEach {
            it.organizationId = userOrgMap[it.id]
        }
        dsl.batchUpdate(users).execute()

        dsl.batchInsert(connectors).execute()
        dsl.batchInsert(components).execute()
        dsl.batchInsert(dataOffers).execute()
        dsl.batchInsert(dataOfferViews).execute()
        dsl.batchInsert(contractOffers).execute()
        dsl.batchInsert(crawlerEventLogEntries).execute()
    }

    fun user(userId: Int, orgId: Int?, applyer: (UserRecord) -> Unit = {}) {
        UserRecord().also {
            it.id = dummyDevUserUuid(userId)
            it.organizationId = orgId?.let { id -> dummyDevOrganizationId(id) }
            it.registrationStatus = UserRegistrationStatus.ACTIVE
            it.email = "user$userId@org$orgId.null"
            it.firstName = "Firstname"
            it.lastName = "Lastname"
            it.jobTitle = "Job Title"
            it.phone = "+0 000 000 000"
            it.onboardingType = UserOnboardingType.SELF_REGISTRATION
            it.invitedBy = null
            it.createdAt = dummyDate(0)
            applyer(it)
            users.add(it)
        }
    }

    fun organization(orgId: Int, createdByUserId: Int, applyer: (OrganizationRecord) -> Unit = {}) {
        OrganizationRecord().also {
            it.id = dummyDevOrganizationId(orgId)
            it.name = dummyDevOrganizationName(orgId)
            it.address = "Address"
            it.url = "https://url"
            it.createdBy = dummyDevUserUuid(createdByUserId)
            it.registrationStatus = OrganizationRegistrationStatus.ACTIVE
            it.businessUnit = "BU"
            it.billingAddress = "Billing Address"
            it.taxId = "Tax ID"
            it.commerceRegisterNumber = null
            it.commerceRegisterLocation = null
            it.mainContactName = "Main Contact Name"
            it.mainContactEmail = "main@contact"
            it.mainContactPhone = "+0 000 000 000"
            it.techContactName = "Tech Contact Name"
            it.techContactEmail = "tech@contact"
            it.techContactPhone = "+0 000 000 000"
            it.legalIdType = OrganizationLegalIdType.TAX_ID
            it.description = "Description"
            it.industry = "Industry"
            it.createdAt = dummyDate(0)
            applyer(it)
            organizations.add(it)
        }
    }

    fun connector(connectorId: Int, orgId: Int, createdByUserId: Int, applyer: (ConnectorRecord) -> Unit = {}) {
        val organizationId = dummyDevOrganizationId(orgId)
        val fullConnectorId = dummyDevConnectorId(orgId, connectorId)
        ConnectorRecord().also {
            it.connectorId = fullConnectorId
            it.organizationId = organizationId
            it.providerOrganizationId = organizationId
            it.type = ConnectorType.OWN
            it.environment = "test"
            it.clientId = "clientId"
            it.name = "Connector"
            it.location = "Location"
            it.frontendUrl = "https://connector"
            it.createdBy = dummyDevUserUuid(createdByUserId)
            it.managementUrl = "https://connector/management"
            it.endpointUrl = "https://connector/dsp"
            it.jwksUrl = "https://jwks"
            it.caasStatus = null
            it.lastRefreshAttemptAt = dummyDate(0)
            it.lastSuccessfulRefreshAt = dummyDate(0)
            it.onlineStatus = ConnectorOnlineStatus.ONLINE
            it.dataOffersExceeded = ConnectorDataOffersExceeded.OK
            it.contractOffersExceeded = ConnectorContractOffersExceeded.OK
            it.createdAt = OffsetDateTime.now()
            applyer(it)
            connectors.add(it)
        }
    }

    fun component(componentId: Int, orgId: Int, createdByUserId: Int, applyer: (ComponentRecord) -> Unit = {}) {
        val fullComponentId = dummyDevConnectorId(orgId, componentId)
        val organizationId = dummyDevOrganizationId(orgId)
        ComponentRecord().also {
            it.id = fullComponentId
            it.organizationId = organizationId
            it.name = "Component name"
            it.homepageUrl = "https://component"
            it.endpointUrl = "https://component/dsp"
            it.environment = "test"
            it.clientId = "clientId"
            it.createdBy = dummyDevUserUuid(createdByUserId)
            it.createdAt = OffsetDateTime.now()
            applyer(it)
            components.add(it)
        }
    }

    fun dataOffer(
        connectorId: Int,
        orgId: Int,
        assetId: Int,
        viewCount: Int = 0,
        dataOfferApplier: (DataOfferRecord) -> Unit = {},
        assetApplier: (UiAsset) -> Unit = {},
    ) {
        val fullConnectorId = dummyDevConnectorId(orgId, connectorId)
        val objectMapper = ObjectMapper()

        val uiAsset = UiAsset().also {
            it.dataSourceAvailability = DataSourceAvailability.LIVE

            it.assetId = dummyDevAssetId(assetId)
            it.title = "Asset $assetId"
            it.description = "Asset description"
            it.descriptionShortText = "shortDescription"
            it.keywords = listOf("keyword")
            assetApplier(it)
        }

        DataOfferRecord().also {
            it.connectorId = fullConnectorId
            it.uiAssetJson = JSONB.valueOf(objectMapper.writeValueAsString(uiAsset))
            it.createdAt = OffsetDateTime.now()
            it.updatedAt = OffsetDateTime.now()


            it.assetId = uiAsset.assetId
            it.assetTitle = uiAsset.title
            it.descriptionNoMarkdown = uiAsset.description?.let{ d -> "$d no markdown" } ?: ""
            it.shortDescriptionNoMarkdown = uiAsset.descriptionShortText ?: ""
            it.keywords = uiAsset.keywords ?: emptyList()
            it.keywordsCommaJoined = uiAsset.keywords?.joinToString(",") ?: ""
            it.dataModel = uiAsset.dataModel ?: ""
            dataOfferApplier(it)
            dataOffers.add(it)
        }

        repeat(viewCount) {
            DataOfferViewCountRecord().also {
                it.connectorId = fullConnectorId
                it.assetId = dummyDevAssetId(assetId)
                it.date = OffsetDateTime.now()
                dataOfferViews.add(it)
            }
        }
    }

    fun contractOffer(connectorId: Int, orgId: Int, assetId: Int, contractOfferId: Int, applyer: (ContractOfferRecord) -> Unit = {}) {
        val fullConnectorId = dummyDevConnectorId(orgId, connectorId)
        val objectMapper = ObjectMapper()

        val uiPolicy = UiPolicy().also {
            it.expression = UiPolicyExpression.empty()
            it.errors = emptyList()
            it.policyJsonLd
        }

        ContractOfferRecord().also {
            it.connectorId = fullConnectorId
            it.contractOfferId = dummyDevContractOfferId(contractOfferId)
            it.assetId = dummyDevAssetId(assetId)
            it.uiPolicyJson = JSONB.valueOf(objectMapper.writeValueAsString(uiPolicy))
            it.createdAt = OffsetDateTime.now()
            it.updatedAt = OffsetDateTime.now()
            applyer(it)
            contractOffers.add(it)
        }
    }

    fun crawlerLogEntry(connectorId: Int, orgId: Int, applyer: (CrawlerEventLogRecord) -> Unit = {}) {
        val fullConnectorId = dummyDevConnectorId(orgId, connectorId)

        CrawlerEventLogRecord().also {
            it.id = UUID.randomUUID()
            it.connectorId = fullConnectorId
            it.environment = "test"
            it.createdAt = OffsetDateTime.now()
            it.userMessage = ""
            it.event = CrawlerEventType.CONNECTOR_UPDATED
            it.eventStatus = CrawlerEventStatus.OK
            it.errorStack = null
            it.assetId = null
            applyer(it)
            crawlerEventLogEntries.add(it)
        }
    }
}
