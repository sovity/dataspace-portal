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
  CaasAvailabilityResponse,
  CheckFreeCaasUsageRequest,
  ConfigureProvidedConnectorWithCertificateRequest,
  ConfigureProvidedConnectorWithJwksRequest,
  ConnectorDetailsDto,
  ConnectorOverviewResult,
  ConnectorStatusDto,
  ConnectorTypeDto,
  CreateCaasRequest,
  CreateConnectorRequest,
  CreateConnectorResponse,
  CreateConnectorStatusDto,
  DeleteOwnConnectorRequest,
  IdResponse,
  ProvidedConnectorOverviewEntryDto,
  ProvidedConnectorOverviewResult,
  ReserveConnectorRequest,
} from '@sovity.de/authority-portal-client';
import {Patcher, patchObj} from 'src/app/core/utils/object-utils';
import {fakeEnv} from './fake-environments';
import {TEST_ORGANIZATIONS} from './fake-organizations';
import {getUserInfo} from './fake-users';

export let TEST_CONNECTORS: ConnectorDetailsDto[] = [
  {
    connectorId: 'BPNL1111AA.AP12I3U',
    type: ConnectorTypeDto.Own,
    organizationName: 'Example Organization',
    organizationId: 'BPNL1111AA',
    hostOrganizationName: 'Example Host',
    hostOrganizationId: 'BPNL1111AA',
    environment: fakeEnv('test'),
    connectorName: 'Example Connector 1',
    location: 'Germany, EU',
    frontendUrl: 'https://xample.test1/connector',
    endpointUrl: 'https://xample.test1/connector/api/v1/dsp',
    managementUrl: 'https://xample.test1/connector/api/management',
    status: 'ONLINE',
    clientId: 'client-id',
  },
  {
    connectorId: 'BPNL1111AA.AP23H5W',
    type: ConnectorTypeDto.Own,
    organizationName: 'Example Organization',
    organizationId: 'BPNL1111AA',
    hostOrganizationName: 'Example Host',
    hostOrganizationId: 'BPNL1111AA',
    environment: fakeEnv('test'),
    connectorName: 'Example Connector 2',
    location: 'Germany, EU',
    frontendUrl: 'https://xample.test1/connector',
    endpointUrl: 'https://xample.test1/connector/api/v1/dsp',
    managementUrl: 'https://xample.test1/connector/api/management',
    status: 'OFFLINE',
    clientId: 'client-id',
  },
  {
    connectorId: 'BPNL1111AA.AP42I3L',
    type: ConnectorTypeDto.Caas,
    organizationName: 'Example Organization',
    organizationId: 'BPNL1111AA',
    hostOrganizationName: 'Example Host',
    hostOrganizationId: 'BPNL1111AA',
    environment: fakeEnv('test'),
    connectorName: 'Example Connector 3',
    location: 'Germany, EU',
    frontendUrl: 'https://xample.test1/connector',
    endpointUrl: 'https://xample.test1/connector/api/v1/dsp',
    managementUrl: 'https://xample.test1/connector/api/management',
    status: 'INIT',
    clientId: 'client-id',
  },
  {
    connectorId: 'BPNL1111AA.AP35I6Y',
    type: ConnectorTypeDto.Provided,
    organizationName: 'Example Organization',
    organizationId: 'BPNL1111AA',
    hostOrganizationName: 'Service Partner Organization',
    hostOrganizationId: 'BPNL7777AA',
    environment: fakeEnv('test'),
    connectorName: 'Provided Connector 1',
    location: 'Germany, EU',
    frontendUrl: 'https://xample.test1/connector',
    endpointUrl: 'https://xample.test1/connector/api/v1/dsp',
    managementUrl: 'https://xample.test1/connector/api/management',
    status: 'ONLINE',
    clientId: 'client-id',
  },
  {
    connectorId: 'BPNL2222BB.CP59I8U',
    type: ConnectorTypeDto.Own,
    organizationName: 'Example Organization',
    organizationId: 'BPNL2222BB',
    hostOrganizationName: 'Example Host',
    hostOrganizationId: 'BPNL2222BB',
    environment: fakeEnv('test'),
    connectorName: 'Example Connector 1',
    location: 'Germany, EU',
    frontendUrl: 'https://xample.test1/connector',
    endpointUrl: 'https://xample.test1/connector/api/v1/dsp',
    managementUrl: 'https://xample.test1/connector/api/management',
    status: 'ONLINE',
    clientId: 'client-id',
  },
  {
    connectorId: 'BPNL2222BB.CFIWWBD',
    type: ConnectorTypeDto.Own,
    organizationId: 'BPNL2222BB',
    organizationName: 'Example Organization',
    hostOrganizationId: 'BPNL2222BB',
    hostOrganizationName: 'Example Organization',
    environment: fakeEnv('test'),
    connectorName: 'Example Connector 2',
    location: 'Germany, EU',
    frontendUrl: 'https://xample.test2/connector',
    endpointUrl: 'https://xample.test2/connector/api/v1/dsp',
    managementUrl: 'https://xample.test2/connector/api/management',
    status: 'ONLINE',
    clientId: 'client-id',
  },
  {
    connectorId: 'BPNL2222BB.CWAQ71U',
    organizationId: 'BPNL2222BB',
    organizationName: 'Example Organization',
    hostOrganizationId: 'BPNL2222BB',
    hostOrganizationName: 'Example Organization',
    type: ConnectorTypeDto.Own,
    environment: fakeEnv('test'),
    connectorName: 'Example Connector',
    location: 'Germany, EU',
    frontendUrl: 'https://xample.test3/connector',
    endpointUrl: 'https://xample.test3/connector/api/v1/dsp',
    managementUrl: 'https://xample.test3/connector/api/management',
    status: 'OFFLINE',
    clientId: 'client-id',
  },
];

export const getListOfConnectorsForTable = (
  organizationId: string,
): ConnectorOverviewResult => {
  return {
    connectors: TEST_CONNECTORS.filter(
      (c) => c.organizationId === organizationId,
    ).map((c) => {
      return {
        id: c.connectorId,
        organizationName: c.organizationName,
        hostOrganizationName: c.hostOrganizationName,
        type: c.type,
        environment: c.environment,
        name: c.connectorName,
        status: c.status,
        frontendUrl: c.frontendUrl,
      };
    }),
  };
};

export const listSpConnectors = (): ProvidedConnectorOverviewResult => {
  return {
    connectors: TEST_CONNECTORS.filter(
      (c) => c.type === 'PROVIDED' || c.type === 'CONFIGURING',
    ).map((c): ProvidedConnectorOverviewEntryDto => {
      return {
        id: c.connectorId,
        customerOrgName: c.organizationName,
        frontendUrl: c.frontendUrl,
        type: c.type,
        environment: c.environment,
        name: c.connectorName,
        status: c.status,
      };
    }),
  };
};

export const getListOfOwnConnectorsForTable = (): ConnectorOverviewResult => {
  const organizationId = getUserInfo().organizationId;
  return {
    connectors: TEST_CONNECTORS.filter(
      (c) => c.organizationId === organizationId,
    ).map((c) => {
      return {
        id: c.connectorId,
        organizationName: c.organizationName,
        hostOrganizationName: c.hostOrganizationName,
        type: c.type,
        environment: c.environment,
        name: c.connectorName,
        status: c.status,
        frontendUrl: c.frontendUrl,
      };
    }),
  };
};

export const getOwnConnectorDetail = (
  connectorId: string,
): ConnectorDetailsDto => {
  const organizationId = getUserInfo().organizationId;
  return TEST_CONNECTORS.filter(
    (c) => c.organizationId === organizationId && c.connectorId === connectorId,
  )[0];
};

export const getListOfAllConnectorsForTable = (): ConnectorOverviewResult => {
  return {
    connectors: TEST_CONNECTORS.map((c) => {
      return {
        id: c.connectorId,
        organizationName: c.organizationName,
        hostOrganizationName: c.hostOrganizationName,
        type: c.type,
        environment: c.environment,
        name: c.connectorName,
        status: c.status,
        frontendUrl: c.frontendUrl,
      };
    }),
  };
};

export const getFullConnectorDetails = (
  connectorId: string,
): ConnectorDetailsDto => {
  return TEST_CONNECTORS.filter((c) => c.connectorId === connectorId)[0];
};

export const getProvidedConnectorDetails = (
  connectorId: string,
): ConnectorDetailsDto => {
  return TEST_CONNECTORS.filter((c) => c.connectorId === connectorId)[0];
};

export const deleteProvidedConnector = (
  request: DeleteOwnConnectorRequest,
): IdResponse => {
  TEST_CONNECTORS = TEST_CONNECTORS.filter(
    (c) => c.connectorId !== request.connectorId,
  );

  return {id: request.connectorId, changedDate: new Date()};
};

export const createOwnConnector = (
  request: CreateConnectorRequest,
): CreateConnectorResponse => {
  const organizationId = getUserInfo().organizationId;
  const organizationName = getUserInfo().organizationName;
  const randomId = generateRandomId(organizationId);
  const status = 'OFFLINE';
  updateConnectorStatus(randomId);

  TEST_CONNECTORS.push({
    connectorId: randomId,
    organizationId: organizationId,
    organizationName: organizationName,
    hostOrganizationId: organizationId,
    hostOrganizationName: organizationName,
    type: ConnectorTypeDto.Own,
    environment: fakeEnv('test'),
    connectorName: request.name,
    location: request.location,
    frontendUrl: request.frontendUrl,
    endpointUrl: request.endpointUrl,
    managementUrl: request.managementUrl,
    status: status,
    clientId: 'client-id',
  });

  return {
    id: randomId,
    changedDate: new Date(),
    status: CreateConnectorStatusDto.Ok,
    clientId: 'client-id',
  };
};

export const createCaas = (
  request: CreateCaasRequest,
  environmentId: string,
): CreateConnectorResponse => {
  const organizationId = getUserInfo().organizationId;
  const organizationName = getUserInfo().organizationName;
  const randomId = generateRandomId(organizationId);
  const status = 'INIT';
  updateConnectorStatus(randomId);

  TEST_CONNECTORS.push({
    connectorId: randomId,
    organizationId: organizationId,
    organizationName: organizationName,
    hostOrganizationId: organizationId,
    hostOrganizationName: organizationName,
    type: ConnectorTypeDto.Caas,
    environment: fakeEnv(environmentId),
    connectorName: request.connectorTitle,
    location: 'Germany, EU',
    frontendUrl: `https://${request.connectorSubdomain}.sovity.caas/connector`,
    endpointUrl: `https://${request.connectorSubdomain}.sovity.caas/connector/api/v1/dsp`,
    managementUrl: `https://${request.connectorSubdomain}.sovity.caas/connector/api/management`,
    status: status,
    clientId: 'client-id',
  });
  return {
    id: randomId,
    changedDate: new Date(),
    status: CreateConnectorStatusDto.Ok,
    clientId: 'client-id',
  };
};

export const checkFreeCaasUsage = (
  request: CheckFreeCaasUsageRequest,
): CaasAvailabilityResponse => {
  return {
    current: TEST_CONNECTORS.find(
      (x) =>
        x.type === ConnectorTypeDto.Caas &&
        x.environment.title === request.environmentId,
    )
      ? 1
      : 0,
    limit: 1,
  };
};

export const reserveProvidedConnector = (
  request: ReserveConnectorRequest,
): CreateConnectorResponse => {
  const hostOrganizationId = getUserInfo().organizationId;
  const hostOrgName = getUserInfo().organizationName;
  const status = 'OFFLINE';

  const clientOrgName = TEST_ORGANIZATIONS.find(
    (it) => it.id === request.customerOrganizationId,
  )?.name;

  const randomId = generateRandomId(request.customerOrganizationId);
  updateConnectorStatus(randomId);

  TEST_CONNECTORS.push({
    connectorId: randomId,
    organizationId: request.customerOrganizationId,
    organizationName: clientOrgName ?? '',
    hostOrganizationId: hostOrganizationId,
    hostOrganizationName: hostOrgName,
    type: ConnectorTypeDto.Configuring,
    environment: fakeEnv('test'),
    connectorName: request.name,
    location: request.location,
    status: status,
    clientId: 'client-id',
  });

  return {
    id: randomId,
    changedDate: new Date(),
    status: CreateConnectorStatusDto.Ok,
    clientId: 'client-id',
  };
};

export const configureProvidedConnectorWithCertificate = (
  request: ConfigureProvidedConnectorWithCertificateRequest,
  connectorId: string,
): CreateConnectorResponse => {
  updateConnectorStatus(connectorId);
  return configureProvidedConnector(
    connectorId,
    request.frontendUrl,
    request.endpointUrl,
    request.managementUrl,
  );
};

export const configureProvidedConnectorWithJwks = (
  request: ConfigureProvidedConnectorWithJwksRequest,
  connectorId: string,
): CreateConnectorResponse => {
  updateConnectorStatus(connectorId);
  return configureProvidedConnector(
    connectorId,
    request.frontendUrl,
    request.endpointUrl,
    request.managementUrl,
  );
};

const configureProvidedConnector = (
  connectorId: string,
  frontendUrl: string,
  endpointUrl: string,
  managementUrl: string,
): CreateConnectorResponse => {
  const connector = TEST_CONNECTORS.find(
    (it) => it.connectorId === connectorId,
  );

  if (!connector) {
    return {
      id: '',
      changedDate: new Date(),
      status: CreateConnectorStatusDto.Error,
      message: 'Connector not found',
    };
  } else {
    connector.frontendUrl = frontendUrl;
    connector.endpointUrl = endpointUrl;
    connector.managementUrl = managementUrl;
    connector.type = ConnectorTypeDto.Provided;

    return {
      id: connectorId,
      changedDate: new Date(),
      status: CreateConnectorStatusDto.Ok,
      clientId: 'client-id',
    };
  }
};

export const deleteOwnConnector = (
  request: DeleteOwnConnectorRequest,
): IdResponse => {
  TEST_CONNECTORS = TEST_CONNECTORS.filter(
    (c) => c.connectorId !== request.connectorId,
  );

  return {id: request.connectorId, changedDate: new Date()};
};

const generateRandomId = (organizationId: string): string => {
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let result = '';
  for (let i = 0; i < 7; i++) {
    result += characters.charAt(Math.floor(Math.random() * characters.length));
  }
  result = organizationId + '.' + result;

  // if such id exists, generate another one
  if (TEST_CONNECTORS.filter((c) => c.connectorId === result).length > 0) {
    return generateRandomId(organizationId);
  } else {
    return result;
  }
};

const updateConnector = (
  connectorId: string,
  patcher: Patcher<ConnectorDetailsDto> = () => ({}),
): void => {
  TEST_CONNECTORS = TEST_CONNECTORS.map((it) =>
    it.connectorId === connectorId ? patchObj(it, patcher) : it,
  );
};

const updateConnectorStatus = (
  connectorId: string,
  status: ConnectorStatusDto = 'ONLINE',
  timeout: number = 5000,
): void => {
  setTimeout(() => {
    updateConnector(connectorId, () => ({status}));
  }, timeout);
};
