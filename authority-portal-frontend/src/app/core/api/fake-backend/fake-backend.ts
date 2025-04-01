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
import {
  CaasAvailabilityResponseToJSON,
  CatalogPageQueryFromJSON,
  CatalogPageResultToJSON,
  CentralComponentCreateRequestFromJSON,
  CentralComponentDtoToJSON,
  ComponentStatusOverviewToJSON,
  ConfigureProvidedConnectorWithCertificateRequestFromJSON,
  ConfigureProvidedConnectorWithJwksRequestFromJSON,
  ConnectorDetailsDtoToJSON,
  ConnectorOverviewResultToJSON,
  CreateCaasRequestFromJSON,
  CreateConnectorRequestFromJSON,
  CreateConnectorResponseToJSON,
  DataOfferDetailPageQueryFromJSON,
  DataOfferDetailPageResultToJSON,
  DeploymentEnvironmentDtoToJSON,
  FetchAPI,
  IdResponseToJSON,
  InviteOrganizationRequestFromJSON,
  InviteParticipantUserRequestFromJSON,
  OnboardingOrganizationUpdateDtoFromJSON,
  OnboardingUserUpdateDtoFromJSON,
  OrganizationDetailsDtoToJSON,
  OrganizationOverviewResultToJSON,
  OwnOrganizationDetailsDtoToJSON,
  ProvidedConnectorOverviewResultToJSON,
  RegistrationRequestDtoFromJSON,
  ReserveConnectorRequestFromJSON,
  UpdateOrganizationDtoFromJSON,
  UpdateUserDtoFromJSON,
  UserDeletionCheckToJSON,
  UserDetailDtoToJSON,
  UserInfoToJSON,
} from '@sovity.de/authority-portal-client';
import {getCatalogPage, getDataOfferDetailPage} from './impl/catalog-fake-impl';
import {
  centralComponentList,
  createCentralComponent,
  deleteCentralComponent,
} from './impl/central-component-fake';
import {deploymentEnvironmentList} from './impl/deployment-environment-list-fake';
import {getComponentStatus} from './impl/fake-component-statuses';
import {
  checkFreeCaasUsage,
  configureProvidedConnectorWithCertificate,
  configureProvidedConnectorWithJwks,
  createCaas,
  createOwnConnector,
  deleteOwnConnector,
  deleteProvidedConnector,
  getFullConnectorDetails,
  getListOfAllConnectorsForTable,
  getListOfConnectorsForTable,
  getListOfOwnConnectorsForTable,
  getOwnConnectorDetail,
  getProvidedConnectorDetails,
  listSpConnectors,
  reserveProvidedConnector,
} from './impl/fake-connectors';
import {
  approveOrganization,
  getListOfOrganizationsForTable,
  getOrganizationDetails,
  getOwnOrganizationDetails,
  inviteOrganization,
  onboardOrganization,
  rejectOrganization,
  updateOwnOrganization,
} from './impl/fake-organizations';
import {
  cascadeDeleteUser,
  changeApplicationRoles,
  changeParticipantRole,
  checkUserDeletion,
  clearApplicationRoles,
  deactivateUser,
  getUserInfo,
  getUserOrThrow,
  inviteUser,
  onboardUser,
  reactivateUser,
  updateUser,
} from './impl/fake-users';
import {
  createOrganization,
  registerOrganization,
} from './impl/registration-process-fake';
import {getBody, getMethod, getUrl} from './utils/request-utils';
import {buildOkFn} from './utils/response-utils';
import {UrlInterceptor} from './utils/url-interceptor';

export const AUTHORITY_PORTAL_FAKE_BACKEND: FetchAPI = async (
  input: RequestInfo,
  init?: RequestInit,
): Promise<Response> => {
  const {url, queryParams} = getUrl(
    input,
    'http://authority-portal.fake-backend/api/',
  );
  const method = getMethod(init);
  const body = getBody(init);
  const environmentId = queryParams.get('environmentId');

  const ok = buildOkFn(method, url, queryParams, body);
  const failed = async (status: number) =>
    new Response(JSON.stringify('failed'), {status});

  return new UrlInterceptor(url, method)

    .url('user-info')
    .on('GET', () => {
      const result = getUserInfo();
      return ok(UserInfoToJSON(result), false);
    })

    .url('authority/organizations')
    .on('GET', () => {
      const result = getListOfOrganizationsForTable();
      return ok(OrganizationOverviewResultToJSON(result));
    })

    .url('authority/organizations/invite')
    .on('POST', () => {
      const request = InviteOrganizationRequestFromJSON(body);
      const result = inviteOrganization(request);

      return ok(IdResponseToJSON(result));
    })

    .url('authority/organizations/*/connectors')
    .on('GET', (organizationId: string) => {
      const result = getListOfConnectorsForTable(organizationId);
      return ok(ConnectorOverviewResultToJSON(result));
    })

    .url('authority/organizations/*/approve')
    .on('PUT', (organizationId) => {
      const result = approveOrganization(organizationId);
      return ok(IdResponseToJSON(result));
    })

    .url('authority/organizations/*/reject')
    .on('PUT', (organizationId) => {
      const result = rejectOrganization(organizationId);
      return ok(IdResponseToJSON(result));
    })

    .url('authority/organizations/*')
    .on('GET', (organizationId) => {
      const result = getOrganizationDetails(organizationId);
      return ok(OrganizationDetailsDtoToJSON(result));
    })

    .url('authority/users/*/deactivate')
    .on('PUT', (userId) => {
      deactivateUser(userId);
      return ok(userId);
    })

    .url('authority/users/*/reactivate')
    .on('PUT', (userId) => {
      reactivateUser(userId);
      return ok(userId);
    })

    .url('authority/users/*/check-delete')
    .on('GET', (userId) => {
      const userDeletionCheck = checkUserDeletion(userId);
      return ok(UserDeletionCheckToJSON(userDeletionCheck));
    })

    .url('authority/users/*')
    .on('DELETE', (userId) => {
      const successorUserId = queryParams.get('successorUserId');
      return ok(IdResponseToJSON(cascadeDeleteUser(userId, successorUserId)));
    })

    .url('authority/connectors')
    .on('GET', () => {
      const result = getListOfAllConnectorsForTable();
      return ok(ConnectorOverviewResultToJSON(result));
    })

    .url('authority/connectors/*')
    .on('GET', (connectorId: string) => {
      const result = getFullConnectorDetails(connectorId);
      return ok(ConnectorDetailsDtoToJSON(result));
    })

    .url('organizations/my-org/connectors')
    .on('GET', () => {
      const result = getListOfOwnConnectorsForTable();
      return ok(ConnectorOverviewResultToJSON(result));
    })

    .url('organizations/my-org/connectors/create-on-premise')
    .on('POST', () => {
      const request = CreateConnectorRequestFromJSON(body);
      const result = createOwnConnector(request);

      return ok(CreateConnectorResponseToJSON(result));
    })

    .url('organizations/my-org/connectors/request-caas')
    .on('POST', () => {
      const request = CreateCaasRequestFromJSON(body);
      const result = createCaas(request, environmentId!);

      return ok(CreateConnectorResponseToJSON(result));
    })

    .url('organizations/my-org/connectors/check-free-caas-usage')
    .on('GET', () => {
      const result = checkFreeCaasUsage({environmentId: environmentId!});

      return ok(CaasAvailabilityResponseToJSON(result));
    })

    .url('organizations/my-org/connectors/*')
    .on('GET', (connectorId: string) => {
      const result = getOwnConnectorDetail(connectorId);
      return ok(ConnectorDetailsDtoToJSON(result));
    })
    .on('DELETE', (connectorId: string) => {
      const result = deleteOwnConnector({connectorId});
      return ok(IdResponseToJSON(result));
    })

    .url('application/connectors')
    .on('GET', () => {
      const result = listSpConnectors();
      return ok(ProvidedConnectorOverviewResultToJSON(result));
    })

    .url('application/connectors/*')
    .on('GET', (connectorId: string) => {
      const result = getProvidedConnectorDetails(connectorId);
      return ok(ConnectorDetailsDtoToJSON(result));
    })
    .on('DELETE', (connectorId: string) => {
      const result = deleteProvidedConnector({connectorId});
      return ok(IdResponseToJSON(result));
    })

    .url('organizations/my-org/users/invite')
    .on('POST', () => {
      const request = InviteParticipantUserRequestFromJSON(body);
      const result = inviteUser(request);

      return ok(IdResponseToJSON(result));
    })

    .url('organizations/my-org/users/*/role')
    .on('PUT', (userId: string) => {
      const result = changeParticipantRole({body: body, userId});

      return ok(IdResponseToJSON(result));
    })

    .url('authority/users/*/roles')
    .on('PUT', (userId: string) => {
      const result = changeApplicationRoles({userRoleDto: body, userId});

      return ok(IdResponseToJSON(result));
    })

    .url('authority/users/*/roles')
    .on('DELETE', (userId: string) => {
      const result = clearApplicationRoles({userId});

      return ok(IdResponseToJSON(result));
    })

    .url('organizations/my-org/users/*/deactivate')
    .on('PUT', (userId) => {
      const result = deactivateUser(userId);
      return ok(IdResponseToJSON(result));
    })

    .url('organizations/my-org/users/*/reactivate')
    .on('PUT', (userId) => {
      const result = reactivateUser(userId);
      return ok(IdResponseToJSON(result));
    })

    .url('organizations/my-org/users/*/deactivate')
    .on('PUT', (userId) => {
      const result = deactivateUser(userId);
      return ok(IdResponseToJSON(result));
    })

    .url('organizations/my-org/users/*/reactivate')
    .on('PUT', (userId) => {
      const result = reactivateUser(userId);
      return ok(IdResponseToJSON(result));
    })

    .url('users/*/check-delete')
    .on('GET', (userId) => {
      const userDeletionCheck = checkUserDeletion(userId);
      return ok(UserDeletionCheckToJSON(userDeletionCheck));
    })

    .url('users/*')
    .on('DELETE', (userId) => {
      const successorUserId = queryParams.get('successorUserId');
      return ok(IdResponseToJSON(cascadeDeleteUser(userId, successorUserId)));
    })

    .url('organizations/*/connectors/*')
    .on('GET', (organizationId: string, connectorId: string) => {
      const result = getFullConnectorDetails(connectorId);
      return ok(ConnectorDetailsDtoToJSON(result));
    })
    .on('DELETE', (organizationId, connectorId) => {
      throw new Error('TODO');
    })

    .url('registration/organization')
    .on('POST', () => {
      const request = RegistrationRequestDtoFromJSON(body);
      const result = createOrganization(request);
      return ok(IdResponseToJSON(result));
    })

    .url('registration')
    .on('POST', () => {
      const request = RegistrationRequestDtoFromJSON(body);
      const result = registerOrganization(request);
      return ok(IdResponseToJSON(result));
    })

    .url('users/*')
    .on('GET', (userId) => {
      const result = getUserOrThrow(userId);
      return ok(UserDetailDtoToJSON(result));
    })
    .on('PUT', (userId) => {
      const request = UpdateUserDtoFromJSON(body);
      const result = updateUser(userId, request);
      return ok(IdResponseToJSON(result));
    })

    .url('deployment-environments')
    .on('GET', () => {
      const result = deploymentEnvironmentList();
      return ok(result.map(DeploymentEnvironmentDtoToJSON));
    })

    .url('authority/central-components')
    .on('GET', () => {
      const result = centralComponentList(environmentId!);
      return ok(result.map(CentralComponentDtoToJSON));
    })
    .on('POST', () => {
      const request = CentralComponentCreateRequestFromJSON(body);
      const result = createCentralComponent(request, environmentId!);
      return ok(IdResponseToJSON(result));
    })

    .url('authority/central-components/*')
    .on('DELETE', (centralComponentId) => {
      const result = deleteCentralComponent(centralComponentId);
      return ok(IdResponseToJSON(result));
    })

    .url('organizations/my-org')
    .on('GET', () => {
      const result = getOwnOrganizationDetails();
      return ok(OwnOrganizationDetailsDtoToJSON(result));
    })
    .on('PUT', () => {
      const request = UpdateOrganizationDtoFromJSON(body);
      const result = updateOwnOrganization(request);
      return ok(IdResponseToJSON(result));
    })

    .url('application/organizations')
    .on('GET', () => {
      const result = getListOfOrganizationsForTable();
      return ok(OrganizationOverviewResultToJSON(result));
    })

    .url('application/organizations/*')
    .on('GET', () => {
      const result = getListOfOrganizationsForTable();
      return ok(OrganizationOverviewResultToJSON(result));
    })

    .url('registration/me/update')
    .on('POST', () => {
      const request = OnboardingUserUpdateDtoFromJSON(body);
      const result = onboardUser(request);

      return ok(IdResponseToJSON(result));
    })

    .url('registration/my-org/update')
    .on('POST', () => {
      const request = OnboardingOrganizationUpdateDtoFromJSON(body);
      const result = onboardOrganization(request);

      return ok(IdResponseToJSON(result));
    })

    .url('component-statuses')
    .on('GET', () => {
      const result = getComponentStatus(environmentId!);
      return ok(ComponentStatusOverviewToJSON(result));
    })

    .url('service-partner/reserve-connector')
    .on('POST', () => {
      const request = ReserveConnectorRequestFromJSON(body);
      const result = reserveProvidedConnector(request);
      return ok(CreateConnectorResponseToJSON(result));
    })

    .url('organizations/*/connectors/*/configure-service-provided')
    .on('PUT', (organizationId, connectorId) => {
      const request =
        ConfigureProvidedConnectorWithCertificateRequestFromJSON(body);
      const result = configureProvidedConnectorWithCertificate(
        request,
        connectorId,
      );
      return ok(CreateConnectorResponseToJSON(result));
    })

    .url('organizations/*/connectors/*/configure-service-provided/with-jwks')
    .on('PUT', (organizationId, connectorId) => {
      const request = ConfigureProvidedConnectorWithJwksRequestFromJSON(body);
      const result = configureProvidedConnectorWithJwks(request, connectorId);
      return ok(CreateConnectorResponseToJSON(result));
    })

    .url('catalog/catalog-page')
    .on('POST', () => {
      const query = CatalogPageQueryFromJSON(body);
      const result = getCatalogPage(query, environmentId!);
      return ok(CatalogPageResultToJSON(result));
    })

    .url('catalog/data-offer-detail-page')
    .on('POST', () => {
      const query = DataOfferDetailPageQueryFromJSON(body);
      const result = getDataOfferDetailPage(query, environmentId!);
      return result ? ok(DataOfferDetailPageResultToJSON(result)) : failed(404);
    })

    .url('service-partner/providable-organizations')
    .on('GET', () => {
      const result = getListOfOrganizationsForTable();
      return ok(OrganizationOverviewResultToJSON(result));
    })

    .tryMatch();
};
