/*
 * Copyright (c) 2024 sovity GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      sovity GmbH - initial implementation
 */

package de.sovity.authorityportal.web

import de.sovity.authorityportal.api.UiResource
import de.sovity.authorityportal.api.model.CaasAvailabilityResponse
import de.sovity.authorityportal.api.model.CentralComponentCreateRequest
import de.sovity.authorityportal.api.model.CentralComponentDto
import de.sovity.authorityportal.api.model.ComponentStatusOverview
import de.sovity.authorityportal.api.model.ConfigureProvidedConnectorWithCertificateRequest
import de.sovity.authorityportal.api.model.ConfigureProvidedConnectorWithJwksRequest
import de.sovity.authorityportal.api.model.ConnectorDetailsDto
import de.sovity.authorityportal.api.model.ConnectorOverviewResult
import de.sovity.authorityportal.api.model.CreateCaasRequest
import de.sovity.authorityportal.api.model.CreateConnectorRequest
import de.sovity.authorityportal.api.model.CreateConnectorResponse
import de.sovity.authorityportal.api.model.DeploymentEnvironmentDto
import de.sovity.authorityportal.api.model.IdResponse
import de.sovity.authorityportal.api.model.InviteOrganizationRequest
import de.sovity.authorityportal.api.model.InviteParticipantUserRequest
import de.sovity.authorityportal.api.model.OnboardingUserUpdateDto
import de.sovity.authorityportal.api.model.ProvidedConnectorOverviewResult
import de.sovity.authorityportal.api.model.RegistrationRequestDto
import de.sovity.authorityportal.api.model.ReserveConnectorRequest
import de.sovity.authorityportal.api.model.UpdateOrganizationDto
import de.sovity.authorityportal.api.model.UpdateOwnOrganizationDto
import de.sovity.authorityportal.api.model.UpdateUserDto
import de.sovity.authorityportal.api.model.UserDeletionCheck
import de.sovity.authorityportal.api.model.UserDetailDto
import de.sovity.authorityportal.api.model.UserInfo
import de.sovity.authorityportal.api.model.UserRegistrationStatusResult
import de.sovity.authorityportal.api.model.UserRoleDto
import de.sovity.authorityportal.api.model.organization.OnboardingOrganizationUpdateDto
import de.sovity.authorityportal.api.model.organization.OrganizationDeletionCheck
import de.sovity.authorityportal.api.model.organization.OrganizationDetailsDto
import de.sovity.authorityportal.api.model.organization.OrganizationOverviewResult
import de.sovity.authorityportal.api.model.organization.OwnOrganizationDetailsDto
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.web.auth.AuthUtils
import de.sovity.authorityportal.web.auth.LoggedInUser
import de.sovity.authorityportal.web.pages.ComponentStatusApiService
import de.sovity.authorityportal.web.pages.centralcomponentmanagement.CentralComponentManagementApiService
import de.sovity.authorityportal.web.pages.connectormanagement.CaasManagementApiService
import de.sovity.authorityportal.web.pages.connectormanagement.ConnectorManagementApiService
import de.sovity.authorityportal.web.pages.organizationmanagement.OrganizationDeletionApiService
import de.sovity.authorityportal.web.pages.organizationmanagement.OrganizationInfoApiService
import de.sovity.authorityportal.web.pages.organizationmanagement.OrganizationInvitationApiService
import de.sovity.authorityportal.web.pages.organizationmanagement.OrganizationRegistrationApiService
import de.sovity.authorityportal.web.pages.organizationmanagement.OrganizationUpdateApiService
import de.sovity.authorityportal.web.pages.registration.RegistrationApiService
import de.sovity.authorityportal.web.pages.usermanagement.UserDeactivationApiService
import de.sovity.authorityportal.web.pages.usermanagement.UserDeletionApiService
import de.sovity.authorityportal.web.pages.usermanagement.UserInfoApiService
import de.sovity.authorityportal.web.pages.usermanagement.UserInvitationApiService
import de.sovity.authorityportal.web.pages.usermanagement.UserRoleApiService
import de.sovity.authorityportal.web.pages.usermanagement.UserUpdateApiService
import de.sovity.authorityportal.web.pages.userregistration.UserRegistrationApiService
import io.quarkus.arc.Lock
import jakarta.annotation.security.PermitAll
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@PermitAll
@ApplicationScoped
class UiResourceImpl(
    val authUtils: AuthUtils,
    val loggedInUser: LoggedInUser,
    val userInfoApiService: UserInfoApiService,
    val userRoleApiService: UserRoleApiService,
    val userRegistrationApiService: UserRegistrationApiService,
    val userInvitationApiService: UserInvitationApiService,
    val userDeactivationApiService: UserDeactivationApiService,
    val userDeletionApiService: UserDeletionApiService,
    val organizationInfoApiService: OrganizationInfoApiService,
    val organizationRegistrationApiService: OrganizationRegistrationApiService,
    val organizationInvitationApiService: OrganizationInvitationApiService,
    val connectorManagementApiService: ConnectorManagementApiService,
    val registrationApiService: RegistrationApiService,
    val userUpdateApiService: UserUpdateApiService,
    val organizationUpdateApiService: OrganizationUpdateApiService,
    val centralComponentManagementApiService: CentralComponentManagementApiService,
    val caasManagementApiService: CaasManagementApiService,
    val componentStatusApiService: ComponentStatusApiService,
    val organizationDeletionApiService: OrganizationDeletionApiService
) : UiResource {

    // User info
    @Transactional
    override fun userInfo(): UserInfo {
        return userInfoApiService.userInfo(loggedInUser)
    }

    @Transactional
    override fun userDetails(userId: String): UserDetailDto {
        authUtils.requiresAuthenticated()
        authUtils.requires(
            authUtils.hasRole(Roles.UserRoles.AUTHORITY_USER) ||
                (authUtils.hasRole(Roles.UserRoles.PARTICIPANT_USER) && authUtils.isMemberOfSameOrganizationAs(userId)),
            userId
        )
        return userInfoApiService.userDetails(userId)
    }

    // Registration
    @Transactional
    override fun userRegistrationStatus(): UserRegistrationStatusResult {
        authUtils.requiresAuthenticated()
        return userRegistrationApiService.userRegistrationStatus(loggedInUser.userId)
    }

    // Organization management (Internal)
    @Transactional
    override fun changeParticipantRole(userId: String, role: UserRoleDto): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_ADMIN)
        authUtils.requiresTargetNotSelf(userId)
        authUtils.requiresMemberOfSameOrganizationAs(userId)
        return userRoleApiService.changeParticipantRole(
            userId = userId,
            roleDto = role,
            organizationId = loggedInUser.organizationId!!,
            adminUserId = loggedInUser.userId
        )
    }

    @Transactional
    override fun inviteUser(invitationInformation: InviteParticipantUserRequest): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return userInvitationApiService.inviteParticipantUser(
            invitationInformation,
            loggedInUser.organizationId!!,
            loggedInUser.userId
        )
    }

    @Transactional
    override fun deactivateParticipantUser(userId: String): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_ADMIN)
        authUtils.requiresTargetRegistrationStatus(userId, UserRegistrationStatus.ACTIVE)
        authUtils.requiresTargetNotSelf(userId)
        authUtils.requiresMemberOfSameOrganizationAs(userId)
        return userDeactivationApiService.deactivateUser(userId, loggedInUser.userId)
    }

    @Transactional
    override fun reactivateParticipantUser(userId: String): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_ADMIN)
        authUtils.requiresTargetRegistrationStatus(userId, UserRegistrationStatus.DEACTIVATED)
        authUtils.requiresMemberOfSameOrganizationAs(userId)
        return userDeactivationApiService.reactivateUser(userId, loggedInUser.userId)
    }

    // Organization management (Authority)
    @Transactional
    override fun changeApplicationRole(userId: String, role: UserRoleDto): IdResponse {
        authUtils.requiresAnyRole(
            Roles.UserRoles.AUTHORITY_ADMIN,
            Roles.UserRoles.OPERATOR_ADMIN,
            Roles.UserRoles.SERVICE_PARTNER_ADMIN
        )
        authUtils.requiresTargetNotSelf(userId)
        if (!authUtils.hasRole(Roles.UserRoles.AUTHORITY_ADMIN)) {
            authUtils.requiresMemberOfSameOrganizationAs(userId)
        }
        return userRoleApiService.changeApplicationRole(userId, role, loggedInUser.userId, loggedInUser.roles)
    }

    @Transactional
    override fun clearApplicationRole(userId: String): IdResponse {
        authUtils.requiresAnyRole(
            Roles.UserRoles.AUTHORITY_ADMIN,
            Roles.UserRoles.OPERATOR_ADMIN,
            Roles.UserRoles.SERVICE_PARTNER_ADMIN
        )
        authUtils.requiresTargetNotSelf(userId)
        if (!authUtils.hasRole(Roles.UserRoles.AUTHORITY_ADMIN)) {
            authUtils.requiresMemberOfSameOrganizationAs(userId)
        }
        return userRoleApiService.clearApplicationRole(userId, loggedInUser.userId)
    }

    @Transactional
    override fun deactivateAnyUser(userId: String): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_ADMIN)
        authUtils.requiresTargetRegistrationStatus(userId, UserRegistrationStatus.ACTIVE)
        authUtils.requiresTargetNotSelf(userId)
        return userDeactivationApiService.deactivateUser(userId, loggedInUser.userId)
    }

    @Transactional
    override fun reactivateAnyUser(userId: String): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_ADMIN)
        authUtils.requiresTargetRegistrationStatus(userId, UserRegistrationStatus.DEACTIVATED)
        return userDeactivationApiService.reactivateUser(userId, loggedInUser.userId)
    }

    @Transactional
    override fun checkUserDeletion(userId: String): UserDeletionCheck {
        if (!authUtils.hasRole(Roles.UserRoles.AUTHORITY_ADMIN)) {
            if (authUtils.hasRole(Roles.UserRoles.PARTICIPANT_ADMIN)) {
                authUtils.requiresMemberOfSameOrganizationAs(userId)
            } else {
                authUtils.requiresTargetSelf(userId)
            }
        }
        return userDeletionApiService.checkUserDeletion(userId)
    }

    @Transactional
    override fun deleteUser(userId: String, successorUserId: String?): IdResponse {
        if (!authUtils.hasRole(Roles.UserRoles.AUTHORITY_ADMIN)) {
            if (authUtils.hasRole(Roles.UserRoles.PARTICIPANT_ADMIN)) {
                authUtils.requiresMemberOfSameOrganizationAs(userId)
            } else {
                authUtils.requiresTargetSelf(userId)
            }
        }
        return userDeletionApiService.handleUserDeletion(userId, successorUserId, loggedInUser.userId)
    }

    @Transactional
    override fun organizationsOverviewForAuthority(environmentId: String): OrganizationOverviewResult {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_USER)
        return organizationInfoApiService.organizationsOverview(environmentId)
    }

    @Transactional
    override fun organizationsOverviewForProvidingConnectors(environmentId: String): OrganizationOverviewResult {
        authUtils.requiresAnyRole(Roles.UserRoles.SERVICE_PARTNER_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return organizationInfoApiService.organizationsOverviewForProvidingConnectors(
            loggedInUser.organizationId!!,
            environmentId
        )
    }

    @Transactional
    override fun reserveProvidedConnector(environmentId: String, connectorReserveRequest: ReserveConnectorRequest): CreateConnectorResponse {
        authUtils.requiresRole(Roles.UserRoles.SERVICE_PARTNER_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return connectorManagementApiService.reserveProvidedConnector(
            loggedInUser.organizationId!!,
            loggedInUser.userId,
            environmentId,
            connectorReserveRequest
        )
    }

    @Transactional
    override fun getConnector(connectorId: String): ConnectorDetailsDto {
        authUtils.requiresAnyRole(Roles.UserRoles.AUTHORITY_USER, Roles.UserRoles.OPERATOR_ADMIN)
        return connectorManagementApiService.getAuthorityConnectorDetails(connectorId)
    }

    @Transactional
    override fun getAllConnectors(environmentId: String): ConnectorOverviewResult {
        authUtils.requiresAnyRole(Roles.UserRoles.AUTHORITY_USER, Roles.UserRoles.OPERATOR_ADMIN)
        return connectorManagementApiService.listAllConnectors(environmentId)
    }

    @Transactional
    override fun ownOrganizationDetails(environmentId: String): OwnOrganizationDetailsDto {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_USER)
        authUtils.requiresMemberOfAnyOrganization()
        return organizationInfoApiService.getOwnOrganizationInformation(loggedInUser.organizationId!!, environmentId)
    }

    @Transactional
    override fun organizationDetailsForAuthority(organizationId: String, environmentId: String): OrganizationDetailsDto {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_USER)
        return organizationInfoApiService.getOrganizationInformation(organizationId, environmentId)
    }

    @Transactional
    override fun organizationDetails(organizationId: String, environmentId: String): OrganizationDetailsDto {
        authUtils.requiresAnyRole(Roles.UserRoles.SERVICE_PARTNER_ADMIN, Roles.UserRoles.OPERATOR_ADMIN)
        return organizationInfoApiService.getOrganizationInformation(organizationId, environmentId)
    }

    @Transactional
    override fun getProvidedConnectors(environmentId: String): ProvidedConnectorOverviewResult {
        authUtils.requiresRole(Roles.UserRoles.SERVICE_PARTNER_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return connectorManagementApiService.listServiceProvidedConnectors(
            loggedInUser.organizationId!!,
            environmentId
        )
    }

    @Transactional
    override fun getProvidedConnectorDetails(connectorId: String): ConnectorDetailsDto {
        authUtils.requiresRole(Roles.UserRoles.SERVICE_PARTNER_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return connectorManagementApiService.getConnectorDetails(
            connectorId,
            loggedInUser.organizationId!!,
            loggedInUser.userId
        )
    }

    @Transactional
    override fun deleteProvidedConnector(connectorId: String): IdResponse {
        authUtils.requiresAnyRole(Roles.UserRoles.SERVICE_PARTNER_ADMIN, Roles.UserRoles.OPERATOR_ADMIN)

        if (!authUtils.hasRole(Roles.UserRoles.OPERATOR_ADMIN)) {
            authUtils.requiresMemberOfProviderOrganization(connectorId)
        } else {
            authUtils.requiresMemberOfAnyOrganization()
        }

        return connectorManagementApiService.deleteConnectorById(
            connectorId,
            loggedInUser.organizationId!!,
            loggedInUser.userId
        )
    }

    @Transactional
    override fun inviteOrganization(invitationInformation: InviteOrganizationRequest): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_USER)
        return organizationInvitationApiService.inviteOrganization(invitationInformation, loggedInUser.userId)
    }

    @Transactional
    override fun approveOrganization(organizationId: String): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_USER)
        return organizationRegistrationApiService.approveOrganization(organizationId, loggedInUser.userId)
    }

    @Transactional
    override fun rejectOrganization(organizationId: String): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_USER)
        return organizationRegistrationApiService.rejectOrganization(organizationId, loggedInUser.userId)
    }

    // Connector management
    @Transactional
    override fun ownOrganizationConnectors(environmentId: String): ConnectorOverviewResult {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_USER)
        authUtils.requiresMemberOfAnyOrganization()
        return connectorManagementApiService.listOrganizationConnectors(loggedInUser.organizationId!!, environmentId)
    }

    @Transactional
    override fun ownOrganizationConnectorDetails(connectorId: String): ConnectorDetailsDto {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_USER)
        authUtils.requiresMemberOfAnyOrganization()
        return connectorManagementApiService.ownOrganizationConnectorDetails(
            connectorId,
            loggedInUser.organizationId!!,
            loggedInUser.userId
        )
    }

    @Transactional
    override fun createOwnConnector(environmentId: String, connector: CreateConnectorRequest): CreateConnectorResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_CURATOR)
        authUtils.requiresMemberOfAnyOrganization()
        return connectorManagementApiService.createOwnConnector(
            connector,
            loggedInUser.organizationId!!,
            loggedInUser.userId,
            environmentId
        )
    }

    @Transactional
    override fun deleteOwnConnector(connectorId: String): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_CURATOR)
        authUtils.requiresMemberOfOwningOrganization(connectorId)
        return connectorManagementApiService.deleteConnectorById(
            connectorId,
            loggedInUser.organizationId!!,
            loggedInUser.userId
        )
    }

    @Transactional
    override fun configureProvidedConnectorWithCertificate(
        organizationId: String,
        connectorId: String,
        environmentId: String,
        connector: ConfigureProvidedConnectorWithCertificateRequest
    ): CreateConnectorResponse {
        authUtils.requiresRole(Roles.UserRoles.SERVICE_PARTNER_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return connectorManagementApiService.configureProvidedConnectorWithCertificate(
            connector,
            connectorId,
            loggedInUser.organizationId!!,
            loggedInUser.userId,
            environmentId
        )
    }

    @Transactional
    override fun configureProvidedConnectorWithJwks(
        organizationId: String,
        connectorId: String,
        environmentId: String,
        connector: ConfigureProvidedConnectorWithJwksRequest
    ): CreateConnectorResponse {
        authUtils.requiresRole(Roles.UserRoles.SERVICE_PARTNER_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return connectorManagementApiService.configureProvidedConnectorWithJwks(
            connector,
            connectorId,
            loggedInUser.organizationId!!,
            loggedInUser.userId,
            environmentId
        )
    }

    @Lock
    override fun createCaas(environmentId: String, caasRequest: CreateCaasRequest): CreateConnectorResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_CURATOR)
        authUtils.requiresMemberOfAnyOrganization()
        return caasManagementApiService.createCaas(
            loggedInUser.organizationId!!,
            loggedInUser.userId,
            caasRequest,
            environmentId
        )
    }

    @Transactional
    override fun checkFreeCaasUsage(environmentId: String): CaasAvailabilityResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_CURATOR)
        authUtils.requiresMemberOfAnyOrganization()
        return caasManagementApiService.getCaasAvailabilityForOrganization(loggedInUser.organizationId!!, environmentId)
    }

    @Transactional
    override fun deploymentEnvironmentList(): List<DeploymentEnvironmentDto> {
        authUtils.requiresAuthenticated()
        return connectorManagementApiService.getAllDeploymentEnvironment()
    }

    @Transactional
    override fun registerUser(registrationRequest: RegistrationRequestDto): IdResponse {
        authUtils.requiresUnauthenticated()
        return registrationApiService.registerUserAndOrganization(registrationRequest)
    }

    @Transactional
    override fun getCentralComponents(environmentId: String): List<CentralComponentDto> {
        authUtils.requiresRole(Roles.UserRoles.OPERATOR_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return centralComponentManagementApiService.listCentralComponents(environmentId)
    }

    @Transactional
    override fun createCentralComponent(
        environmentId: String,
        componentRegistrationRequest: CentralComponentCreateRequest
    ): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.OPERATOR_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return centralComponentManagementApiService.registerCentralComponent(
            componentRegistrationRequest,
            loggedInUser.userId,
            loggedInUser.organizationId!!,
            environmentId
        )
    }

    @Transactional
    override fun deleteCentralComponent(centralComponentId: String): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.OPERATOR_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return centralComponentManagementApiService.deleteCentralComponentByUser(
            centralComponentId,
            loggedInUser.userId
        )
    }

    @Transactional
    override fun updateOnboardingUser(onboardingUserUpdateDto: OnboardingUserUpdateDto): IdResponse {
        authUtils.requiresRegistrationStatus(UserRegistrationStatus.ONBOARDING)
        return userUpdateApiService.updateOnboardingUserDetails(loggedInUser.userId, onboardingUserUpdateDto)
    }

    @Transactional
    override fun updateOnboardingOrganization(onboardingOrganizationUpdateDto: OnboardingOrganizationUpdateDto): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_ADMIN)
        authUtils.requiresOrganizationRegistrationStatus(OrganizationRegistrationStatus.ONBOARDING)
        return organizationUpdateApiService.onboardOrganization(
            loggedInUser.organizationId!!,
            onboardingOrganizationUpdateDto
        )
    }

    @Transactional
    override fun updateUser(userId: String, updateUserDto: UpdateUserDto): IdResponse {
        authUtils.requiresSelfOrRole(userId, Roles.UserRoles.AUTHORITY_ADMIN)
        return userUpdateApiService.updateUserDetails(userId, updateUserDto)
    }

    @Transactional
    override fun updateOwnOrganizationDetails(organizationDto: UpdateOwnOrganizationDto): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.PARTICIPANT_ADMIN)
        authUtils.requiresMemberOfAnyOrganization()
        return organizationUpdateApiService.updateOwnOrganization(loggedInUser.organizationId!!, organizationDto)
    }

    @Transactional
    override fun updateOrganizationDetails(organizationId: String, organizationDto: UpdateOrganizationDto): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_ADMIN)
        return organizationUpdateApiService.updateOrganization(organizationId, organizationDto)
    }

    @Transactional
    override fun getComponentsStatus(environmentId: String): ComponentStatusOverview {
        authUtils.requiresAuthenticated()
        authUtils.requiresMemberOfAnyOrganization()
        if (authUtils.hasRole(Roles.UserRoles.AUTHORITY_USER)) {
            return componentStatusApiService.getComponentsStatus(environmentId)
        }
        return componentStatusApiService.getComponentsStatusForOrganizationId(environmentId, loggedInUser.organizationId!!)
    }

    @Transactional
    override fun checkOrganizationDeletion(organizationId: String): OrganizationDeletionCheck {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_ADMIN)
        return organizationDeletionApiService.checkOrganizationDeletion(organizationId)
    }

    @Transactional
    override fun deleteOrganization(organizationId: String): IdResponse {
        authUtils.requiresRole(Roles.UserRoles.AUTHORITY_ADMIN)
        return organizationDeletionApiService.deleteOrganizationAndDependencies(organizationId, loggedInUser.userId)
    }
}
