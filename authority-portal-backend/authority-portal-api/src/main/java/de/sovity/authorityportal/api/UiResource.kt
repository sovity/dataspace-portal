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
package de.sovity.authorityportal.api

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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType

@Path("/api/")
@Tag(name = "Ui", description = "Authority Portal UI API Endpoints.")
interface UiResource {
    // User info
    @GET
    @Path("/user-info")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get the current user's info.")
    fun userInfo(): UserInfo

    @GET
    @Path("/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get the user's details.")
    fun userDetails(@PathParam("userId") userId: String): UserDetailDto

    // Registration
    @GET
    @Path("/registration/registration-status")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get a user's own registration status.")
    fun userRegistrationStatus(): UserRegistrationStatusResult

    // Organization management (Internal)
    @PUT
    @Path("/organizations/my-org/users/{userId}/role")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Change a user's participant role (PARTICIPANT_*) within their organization.")
    fun changeParticipantRole(
        @PathParam("userId") userId: String,

        @Valid @NotNull(message = "Role cannot be null")
        role: UserRoleDto
    ): IdResponse

    @POST
    @Path("/organizations/my-org/users/invite")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Invite new user to a participating organization.")
    fun inviteUser(
        @Valid @NotNull(message = "Invitation information cannot be null")
        invitationInformation: InviteParticipantUserRequest
    ): IdResponse

    @PUT
    @Path("/organizations/my-org/users/{userId}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Deactivate a user of a participating organization.")
    fun deactivateParticipantUser(@PathParam("userId") userId: String): IdResponse

    @PUT
    @Path("/organizations/my-org/users/{userId}/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Reactivate a user of a participating organization.")
    fun reactivateParticipantUser(@PathParam("userId") userId: String): IdResponse

    // Organization management (Authority)
    @PUT
    @Path("/authority/users/{userId}/role")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Change a user's application role.")
    fun changeApplicationRole(
        @PathParam("userId") userId: String,

        @Valid @NotNull(message = "Role cannot be null")
        role: UserRoleDto
    ): IdResponse

    @DELETE
    @Path("/authority/users/{userId}/role")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Clear a user's application role.")
    fun clearApplicationRole(@PathParam("userId") userId: String): IdResponse

    @PUT
    @Path("/authority/users/{userId}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Deactivate a user of any organization.")
    fun deactivateAnyUser(@PathParam("userId") userId: String): IdResponse

    @PUT
    @Path("/authority/users/{userId}/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Reactivate a user of any organization.")
    fun reactivateAnyUser(@PathParam("userId") userId: String): IdResponse

    @GET
    @Path("/users/{userId}/check-delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Check under which conditions a user can be deleted from an organization.")
    fun checkUserDeletion(@PathParam("userId") userId: String): UserDeletionCheck

    @DELETE
    @Path("/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Delete user with respect to organizations and connectors associated with them.")
    fun deleteUser(
        @PathParam("userId")
        userId: String,

        @QueryParam("successorUserId")
        successorUserId: String?
    ): IdResponse

    @GET
    @Path("/authority/organizations")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get all participating organizations with their status.")
    fun organizationsOverviewForAuthority(
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): OrganizationOverviewResult

    @GET
    @Path("/service-partner/providable-organizations")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get all organizations a service partner can provide a connector to. (For Service Partners)")
    fun organizationsOverviewForProvidingConnectors(
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): OrganizationOverviewResult

    @POST
    @Path("/service-partner/reserve-connector")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates a reserved connector that can be registered later for another organization.")
    fun reserveProvidedConnector(
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String,

        @Valid @NotNull(message = "Connector reserve request cannot be null")
        connectorReserveRequest: ReserveConnectorRequest
    ): CreateConnectorResponse

    @GET
    @Path("/authority/connectors/{connectorId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get details of any connector.")
    fun getConnector(@PathParam("connectorId") connectorId: String): ConnectorDetailsDto

    @GET
    @Path("/authority/connectors")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get all connectors of all participating organizations.")
    fun getAllConnectors(
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): ConnectorOverviewResult

    @GET
    @Path("/organizations/my-org")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get details of own organization.")
    fun ownOrganizationDetails(
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): OwnOrganizationDetailsDto

    @GET
    @Path("/authority/organizations/{organizationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get details of any organization.")
    fun organizationDetailsForAuthority(
        @PathParam("organizationId") organizationId: String,

        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): OrganizationDetailsDto

    @GET
    @Path("/application/organizations/{organizationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get details of any organization. (For Service Partners and Operators)")
    fun organizationDetails(
        @PathParam("organizationId") organizationId: String,

        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): OrganizationDetailsDto

    @GET
    @Path("/application/connectors")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get all connectors provided by own organization. (For Service Partners)")
    fun getProvidedConnectors(
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): ProvidedConnectorOverviewResult

    @GET
    @Path("/application/connectors/{connectorId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get details of a connector provided by own organization. (For Service Partners)")
    fun getProvidedConnectorDetails(@PathParam("connectorId") connectorId: String): ConnectorDetailsDto

    @DELETE
    @Path("/application/connectors/{connectorId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Unregister a connector provided by own organization. (For Service Partners)")
    fun deleteProvidedConnector(@PathParam("connectorId") connectorId: String): IdResponse

    @POST
    @Path("/authority/organizations/invite")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Invite a new organization via a new Participant Admin.")
    fun inviteOrganization(
        @Valid @NotNull(message = "Invitation information cannot be null")
        invitationInformation: InviteOrganizationRequest
    ): IdResponse

    @PUT
    @Path("/authority/organizations/{organizationId}/approve")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Approve a newly registered organization.")
    fun approveOrganization(@PathParam("organizationId") organizationId: String): IdResponse

    @PUT
    @Path("/authority/organizations/{organizationId}/reject")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Reject a newly registered organization.")
    fun rejectOrganization(@PathParam("organizationId") organizationId: String): IdResponse

    // Connector management
    @GET
    @Path("/organizations/my-org/connectors")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get all connectors of a user's own organization.")
    fun ownOrganizationConnectors(
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): ConnectorOverviewResult

    @GET
    @Path("/organizations/my-org/connectors/{connectorId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get details of a user's own organization's connector.")
    fun ownOrganizationConnectorDetails(@PathParam("connectorId") connectorId: String): ConnectorDetailsDto

    @POST
    @Path("/organizations/my-org/connectors/create-on-premise")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Register a self-hosted connector.")
    fun createOwnConnector(
        @QueryParam("environmentId")
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        environmentId: String,

        @Valid @NotNull(message = "Connector cannot be null")
        connector: CreateConnectorRequest
    ): CreateConnectorResponse

    @DELETE
    @Path("/organizations/my-org/connectors/{connectorId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Unregister a self-hosted connector.")
    fun deleteOwnConnector(@PathParam("connectorId") connectorId: String): IdResponse

    @PUT
    @Path("/organizations/{organizationId}/connectors/{connectorId}/configure-service-provided")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Configure a connector for another organization as a service provider.")
    fun configureProvidedConnectorWithCertificate(
        @PathParam("organizationId") organizationId: String,
        @PathParam("connectorId") connectorId: String,

        @QueryParam("environmentId")
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        environmentId: String,

        @Valid @NotNull(message = "Connector cannot be null")
        connector: ConfigureProvidedConnectorWithCertificateRequest
    ): CreateConnectorResponse

    @PUT
    @Path("/organizations/{organizationId}/connectors/{connectorId}/configure-service-provided/with-jwks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Configure a connector for another organization as a service provider with JWKS URL instead of a certificate.")
    fun configureProvidedConnectorWithJwks(
        @PathParam("organizationId") organizationId: String,
        @PathParam("connectorId") connectorId: String,

        @QueryParam("environmentId")
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        environmentId: String,

        @Valid @NotNull(message = "Connector cannot be null")
        connector: ConfigureProvidedConnectorWithJwksRequest
    ): CreateConnectorResponse

    @POST
    @Path("/organizations/my-org/connectors/request-caas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Request a CaaS registration.")
    fun createCaas(
        @QueryParam("environmentId")
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        environmentId: String,

        @Valid @NotNull(message = "CaaS request cannot be null")
        caasRequest: CreateCaasRequest
    ): CreateConnectorResponse

    @GET
    @Path("/organizations/my-org/connectors/check-free-caas-usage")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Check if a CaaS registration is available for your organization.")
    fun checkFreeCaasUsage(
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): CaasAvailabilityResponse

    @GET
    @Path("/deployment-environments")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get a list of all deployment environment.")
    fun deploymentEnvironmentList(): List<DeploymentEnvironmentDto>

    @POST
    @Path("/registration")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Register user and organization.")
    fun registerUser(
        @Valid @NotNull(message = "Registration request cannot be null")
        registrationRequest: RegistrationRequestDto
    ): IdResponse

    @POST
    @Path("/registration/me/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Update user information during onboarding.")
    fun updateOnboardingUser(
        @Valid @NotNull(message = "Update user request cannot be null")
        onboardingUserUpdateDto: OnboardingUserUpdateDto
    ): IdResponse

    @POST
    @Path("/registration/my-org/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Update organization information during onboarding.")
    fun updateOnboardingOrganization(
        @Valid @NotNull(message = "Update organization request cannot be null")
        onboardingOrganizationUpdateDto: OnboardingOrganizationUpdateDto
    ): IdResponse

    @PUT
    @Path("/users/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Update user information")
    fun updateUser(
        @PathParam("userId") userId: String,

        @Valid @NotNull(message = "Update user request cannot be null")
        updateUserDto: UpdateUserDto
    ): IdResponse

    @PUT
    @Path("/organizations/my-org")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Update own organization information")
    fun updateOwnOrganizationDetails(
        @Valid @NotNull(message = "Update organization request cannot be null")
        organizationDto: UpdateOrganizationDto
    ): IdResponse

    @PUT
    @Path("/authority/organizations/{organizationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Update organization information")
    fun updateOrganizationDetails(
        @PathParam("organizationId") organizationId: String,

        @Valid @NotNull(message = "Update organization request cannot be null")
        organizationDto: UpdateOrganizationDto
    ): IdResponse

    @GET
    @Path("/authority/central-components")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "List central components (e.g. a broker or logging house)")
    fun getCentralComponents(
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        @QueryParam("environmentId")
        environmentId: String
    ): List<CentralComponentDto>

    @POST
    @Path("/authority/central-components")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Register a central component (e.g. a broker or logging house)")
    fun createCentralComponent(
        @QueryParam("environmentId")
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        environmentId: String,

        @Valid @NotNull(message = "Component cannot be null")
        componentRegistrationRequest: CentralComponentCreateRequest
    ): IdResponse

    @DELETE
    @Path("/authority/central-components/{centralComponentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Delete a central component (e.g. a broker or logging house)")
    fun deleteCentralComponent(@PathParam("centralComponentId") centralComponentId: String): IdResponse

    @GET
    @Path("/component-statuses")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get component status information for a specific environment.")
    fun getComponentsStatus(
        @QueryParam("environmentId")
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        environmentId: String
    ): ComponentStatusOverview

    @GET
    @Path("/authority/organizations/{organizationId}/check-delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Check under which conditions a organization can be deleted.")
    fun checkOrganizationDeletion(@PathParam("organizationId") organizationId: String): OrganizationDeletionCheck

    @DELETE
    @Path("/authority/organizations/{organizationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Delete organization with respect to users and connectors associated with them.")
    fun deleteOrganization(
        @PathParam("organizationId") organizationId: String,
    ): IdResponse
}
