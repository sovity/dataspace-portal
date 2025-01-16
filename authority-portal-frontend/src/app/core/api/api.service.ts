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
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {
  CaasAvailabilityResponse,
  CentralComponentCreateRequest,
  CentralComponentDto,
  ComponentStatusOverview,
  ConfigureProvidedConnectorWithCertificateRequest,
  ConfigureProvidedConnectorWithJwksRequest,
  ConnectorDetailsDto,
  ConnectorOverviewResult,
  CreateCaasRequest,
  CreateConnectorRequest,
  CreateConnectorResponse,
  DeploymentEnvironmentDto,
  IdResponse,
  InviteOrganizationRequest,
  InviteParticipantUserRequest,
  OnboardingOrganizationUpdateDto,
  OnboardingUserUpdateDto,
  OrganizationDetailsDto,
  OrganizationOverviewResult,
  OwnOrganizationDetailsDto,
  ProvidedConnectorOverviewResult,
  RegistrationRequestDto,
  ReserveConnectorRequest,
  UiApi,
  UpdateOrganizationDto,
  UpdateUserDto,
  UserDetailDto,
  UserInfo,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {toObservable} from '../utils/rxjs-utils';
import {ApiClientFactory} from './api-client-factory';

@Injectable()
export class ApiService {
  constructor(private apiClientFactory: ApiClientFactory) {}

  userProfile(): Observable<UserInfo> {
    return toObservable(() => this.api().userInfo());
  }

  updateUser(
    userId: string,
    updateUserDto: UpdateUserDto,
  ): Observable<IdResponse> {
    return toObservable(() => this.api().updateUser({userId, updateUserDto}));
  }

  updateParticipantRole(userId: string, role: UserRoleDto) {
    return toObservable(() =>
      this.api().changeParticipantRole({userId, body: role}),
    );
  }
  updateApplicationRole(userId: string, role: UserRoleDto) {
    return toObservable(() =>
      this.api().changeApplicationRole({userId, body: role as UserRoleDto}),
    );
  }

  clearApplicationRole(userId: string) {
    return toObservable(() => this.api().clearApplicationRole({userId}));
  }

  deactivateAnyUser(userId: string) {
    return toObservable(() => this.api().deactivateAnyUser({userId}));
  }

  deactivateUser(userId: string) {
    return toObservable(() => this.api().deactivateParticipantUser({userId}));
  }

  checkUserDeletion(userId: string) {
    return toObservable(() => this.api().checkUserDeletion({userId}));
  }

  deleteUser(userId: string, successorUserId?: string) {
    return toObservable(() => this.api().deleteUser({userId, successorUserId}));
  }

  checkParticipantUserDeletion(userId: string) {
    return toObservable(() => this.api().checkUserDeletion({userId}));
  }

  deleteParticipantUser(userId: string, successorUserId?: string) {
    return toObservable(() => this.api().deleteUser({userId, successorUserId}));
  }

  reactivateAnyUser(userId: string) {
    return toObservable(() => this.api().reactivateAnyUser({userId}));
  }

  reactivateUser(userId: string) {
    return toObservable(() => this.api().reactivateParticipantUser({userId}));
  }

  getUserDetailDto(userId: string): Observable<UserDetailDto> {
    return toObservable(() => this.api().userDetails({userId}));
  }

  registerOrganization(
    registrationRequestDto: RegistrationRequestDto,
  ): Observable<IdResponse> {
    return toObservable(() =>
      this.api().registerUser({registrationRequestDto}),
    );
  }

  getOrganizationsForAuthority(
    environmentId: string,
  ): Observable<OrganizationOverviewResult> {
    return toObservable(() =>
      this.api().organizationsOverviewForAuthority({environmentId}),
    );
  }

  getOrganizationDetailsForAuthority(
    organizationId: string,
    environmentId: string,
  ): Observable<OrganizationDetailsDto> {
    return toObservable(() =>
      this.api().organizationDetailsForAuthority({
        organizationId,
        environmentId,
      }),
    );
  }

  getOrganizationsForProvidingConnectors(
    environmentId: string,
  ): Observable<OrganizationOverviewResult> {
    return toObservable(() =>
      this.api().organizationsOverviewForProvidingConnectors({environmentId}),
    );
  }

  getOrganizationDetailsForApplicationRoles(
    organizationId: string,
    environmentId: string,
  ): Observable<OrganizationDetailsDto> {
    return toObservable(() =>
      this.api().organizationDetails({organizationId, environmentId}),
    );
  }

  getOwnOrganizationDetails(
    environmentId: string,
  ): Observable<OwnOrganizationDetailsDto> {
    return toObservable(() =>
      this.api().ownOrganizationDetails({environmentId}),
    );
  }

  updateOwnOrganizationDetails(
    updateOrganizationDto: UpdateOrganizationDto,
  ): Observable<IdResponse> {
    return toObservable(() =>
      this.api().updateOwnOrganizationDetails({updateOrganizationDto}),
    );
  }

  getOrganizationUser(userId: string): Observable<UserDetailDto> {
    return toObservable(() => this.api().userDetails({userId}));
  }

  approveOrganization(organizationId: string): Observable<IdResponse> {
    return toObservable(() => this.api().approveOrganization({organizationId}));
  }

  rejectOrganization(organizationId: string): Observable<IdResponse> {
    return toObservable(() => this.api().rejectOrganization({organizationId}));
  }

  // Connectors
  // Own Connectors
  getOwnOrganizationConnectors(
    environmentId: string,
  ): Observable<ConnectorOverviewResult> {
    return toObservable(() =>
      this.api().ownOrganizationConnectors({environmentId}),
    );
  }

  // All Connectors
  getAllConnectors(environmentId: string): Observable<ConnectorOverviewResult> {
    return toObservable(() => this.api().getAllConnectors({environmentId}));
  }

  getConnector(connectorId: string): Observable<ConnectorDetailsDto> {
    return toObservable(() => this.api().getConnector({connectorId}));
  }

  getOwnOrganizationConnectorDetails(
    connectorId: string,
  ): Observable<ConnectorDetailsDto> {
    return toObservable(() =>
      this.api().ownOrganizationConnectorDetails({connectorId}),
    );
  }

  inviteOrganization(
    request: InviteOrganizationRequest,
  ): Observable<IdResponse> {
    return toObservable(() =>
      this.api().inviteOrganization({inviteOrganizationRequest: request}),
    );
  }

  createOwnConnector(
    createConnectorRequest: CreateConnectorRequest,
    environmentId: string,
  ): Observable<CreateConnectorResponse> {
    return toObservable(() =>
      this.api().createOwnConnector({createConnectorRequest, environmentId}),
    );
  }

  reserveProvidedConnector(
    connector: ReserveConnectorRequest,
    environmentId: string,
  ): Observable<CreateConnectorResponse> {
    return toObservable(() =>
      this.api().reserveProvidedConnector({
        reserveConnectorRequest: connector,
        environmentId,
      }),
    );
  }

  configureProvidedConnectorWithCertificate(
    connector: ConfigureProvidedConnectorWithCertificateRequest,
    organizationId: string,
    connectorId: string,
    environmentId: string,
  ): Observable<CreateConnectorResponse> {
    return toObservable(() =>
      this.api().configureProvidedConnectorWithCertificate({
        configureProvidedConnectorWithCertificateRequest: connector,
        organizationId,
        connectorId,
        environmentId,
      }),
    );
  }

  configureProvidedConnectorWithJwks(
    connector: ConfigureProvidedConnectorWithJwksRequest,
    organizationId: string,
    connectorId: string,
    environmentId: string,
  ): Observable<CreateConnectorResponse> {
    return toObservable(() =>
      this.api().configureProvidedConnectorWithJwks({
        configureProvidedConnectorWithJwksRequest: connector,
        organizationId,
        connectorId,
        environmentId,
      }),
    );
  }

  createCaas(
    createCaasRequest: CreateCaasRequest,
    environmentId: string,
  ): Observable<CreateConnectorResponse> {
    return toObservable(() =>
      this.api().createCaas({createCaasRequest, environmentId}),
    );
  }

  deleteOwnConnector(connectorId: string): Observable<IdResponse> {
    return toObservable(() => this.api().deleteOwnConnector({connectorId}));
  }

  getProvidedConnectors(
    environmentId: string,
  ): Observable<ProvidedConnectorOverviewResult> {
    return toObservable(() =>
      this.api().getProvidedConnectors({environmentId}),
    );
  }

  getProvidedConnectorDetails(
    connectorId: string,
  ): Observable<ConnectorDetailsDto> {
    return toObservable(() =>
      this.api().getProvidedConnectorDetails({connectorId}),
    );
  }

  deleteProvidedConnector(connectorId: string): Observable<IdResponse> {
    return toObservable(() =>
      this.api().deleteProvidedConnector({connectorId}),
    );
  }

  inviteUser(request: InviteParticipantUserRequest): Observable<IdResponse> {
    return toObservable(() =>
      this.api().inviteUser({inviteParticipantUserRequest: request}),
    );
  }

  getDeploymentEnvironments(): Observable<DeploymentEnvironmentDto[]> {
    return toObservable(() => this.api().deploymentEnvironmentList());
  }

  onboardingUser(request: OnboardingUserUpdateDto) {
    return toObservable(() =>
      this.api().updateOnboardingUser({onboardingUserUpdateDto: request}),
    );
  }

  onboardingOrganization(request: OnboardingOrganizationUpdateDto) {
    return toObservable(() =>
      this.api().updateOnboardingOrganization({
        onboardingOrganizationUpdateDto: request,
      }),
    );
  }

  getCentralComponents(
    environmentId: string,
  ): Observable<CentralComponentDto[]> {
    return toObservable(() => this.api().getCentralComponents({environmentId}));
  }

  createCentralComponent(
    environmentId: string,
    centralComponentCreateRequest: CentralComponentCreateRequest,
  ): Observable<IdResponse> {
    return toObservable(() =>
      this.api().createCentralComponent({
        environmentId,
        centralComponentCreateRequest,
      }),
    );
  }

  deleteCentralComponent(centralComponentId: string): Observable<IdResponse> {
    return toObservable(() =>
      this.api().deleteCentralComponent({
        centralComponentId,
      }),
    );
  }

  checkFreeCaasUsage(
    environmentId: string,
  ): Observable<CaasAvailabilityResponse> {
    return toObservable(() => this.api().checkFreeCaasUsage({environmentId}));
  }

  getComponentStatus(
    environmentId: string,
  ): Observable<ComponentStatusOverview> {
    return toObservable(() => this.api().getComponentsStatus({environmentId}));
  }

  checkOrganizationDeletion(organizationId: string) {
    return toObservable(() =>
      this.api().checkOrganizationDeletion({organizationId}),
    );
  }

  deleteOrganization(organizationId: string): Observable<IdResponse> {
    return toObservable(() => this.api().deleteOrganization({organizationId}));
  }

  private api(): UiApi {
    return this.apiClientFactory.newAuthorityPortalClient().uiApi;
  }
}
