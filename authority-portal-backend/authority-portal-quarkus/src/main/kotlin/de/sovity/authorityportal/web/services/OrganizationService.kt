package de.sovity.authorityportal.web.services

import de.sovity.authorityportal.api.model.CreateOrganizationRequest
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.tables.records.OrganizationRecord
import de.sovity.authorityportal.web.model.CreateOrganizationData
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.jooq.DSLContext
import java.time.OffsetDateTime

@ApplicationScoped
class OrganizationService {

    @Inject
    lateinit var dsl: DSLContext

    fun getOrganizationOrThrow(mdsId: String): OrganizationRecord {
        return getOrganization(mdsId) ?: error("Organization with id $mdsId not found")
    }

    private fun getOrganization(mdsId: String): OrganizationRecord? {
        val o = Tables.ORGANIZATION

        return dsl.selectFrom(o)
            .where(o.MDS_ID.eq(mdsId))
            .fetchOne()
    }

    fun getOrganizations(): List<OrganizationRecord> {
        val o = Tables.ORGANIZATION

        return dsl.selectFrom(o)
            .fetch()
    }

    fun createOrganization(
        userId: String,
        mdsId: String,
        organization: CreateOrganizationRequest,
        registrationStatus: OrganizationRegistrationStatus
    ) {
        dsl.newRecord(Tables.ORGANIZATION).also {
            it.mdsId = mdsId
            it.name = organization.name
            it.address = organization.address
            it.taxId = organization.taxId
            it.url = organization.url
            it.mainContactEmail = organization.securityEmail
            it.createdBy = userId
            it.registrationStatus = registrationStatus
            it.createdAt = OffsetDateTime.now()

            it.insert()
        }
    }

    fun createOrganization(
        userId: String,
        mdsId: String,
        organizationData: CreateOrganizationData,
        registrationStatus: OrganizationRegistrationStatus
    ) {
        dsl.newRecord(Tables.ORGANIZATION).also {
            it.mdsId = mdsId
            it.name = organizationData.name
            it.url = organizationData.url
            it.businessUnit = organizationData.businessUnit
            it.address = organizationData.address
            it.billingAddress = organizationData.billingAddress
            it.taxId = organizationData.taxId
            it.commerceRegisterNumber = organizationData.commerceRegisterNumber
            it.commerceRegisterLocation = organizationData.commerceRegisterLocation
            it.mainContactName = organizationData.mainContactName
            it.mainContactEmail = organizationData.mainContactEmail
            it.mainContactPhone = organizationData.mainContactPhone
            it.techContactName = organizationData.techContactName
            it.techContactEmail = organizationData.techContactEmail
            it.techContactPhone = organizationData.techContactPhone
            it.createdBy = userId
            it.registrationStatus = registrationStatus
            it.createdAt = OffsetDateTime.now()

            it.insert()
        }
    }
}
