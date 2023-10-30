package de.sovity.authorityportal.web.pages.organizationmanagement

import de.sovity.authorityportal.api.model.MemberInfo
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.tables.records.OrganizationRecord
import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserDetailService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class OrganizationInfoApiServiceTest {

    @InjectMocks
    lateinit var organizationInfoApiService: OrganizationInfoApiService

    @Mock
    lateinit var userDetailService: UserDetailService

    @Mock
    lateinit var connectorService: ConnectorService

    @Mock
    lateinit var organizationService: OrganizationService

    private val mdsId = "testMdsId"
    private val environmentId = "testEnvironmentId"
    private val organizationRecord: OrganizationRecord = mock(OrganizationRecord::class.java)
    private val memberInfos = listOf(mock(MemberInfo::class.java))
    private val connectorCount = 5

    @BeforeEach
    fun before() {
        `when`(organizationRecord.registrationStatus).thenReturn(OrganizationRegistrationStatus.ACTIVE)
        `when`(organizationService.getOrganizationOrThrow(mdsId)).thenReturn(organizationRecord)
        `when`(userDetailService.getOrganizationMembers(mdsId)).thenReturn(memberInfos)
    }

    @Test
    fun testOrganizationDetails() {
        // arrange
        `when`(connectorService.getConnectorCountByMdsId(mdsId, environmentId)).thenReturn(connectorCount)

        // act
        val result = organizationInfoApiService.getOrganizationInformation(mdsId, environmentId)

        // assert
        assertEquals(OrganizationRegistrationStatus.ACTIVE.toDto(), result.registrationStatus)
        assertEquals(memberInfos, result.memberInfos)
        assertEquals(memberInfos.size, result.memberCount)
        assertEquals(connectorCount, result.connectorCount)
    }

    @Test
    fun testOwnOrganizationDetails() {
        // act
        val result = organizationInfoApiService.ownOrganizationDetails(mdsId)

        // assert
        assertEquals(OrganizationRegistrationStatus.ACTIVE.toDto(), result.registrationStatus)
        assertEquals(memberInfos, result.memberInfos)
    }
}
