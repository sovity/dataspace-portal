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
  CentralComponentCreateRequest,
  CentralComponentDto,
  IdResponse,
} from '@sovity.de/authority-portal-client';

let centralComponents: Record<string, CentralComponentDto[]> = {
  development: [
    {
      centralComponentId: 'MDSL1234XX.C0001XX',
      name: 'Broker',
      homepageUrl: 'https://broker.dev.my-dataspace.sovity.io',
      endpointUrl: 'https://broker.dev.my-dataspace.sovity.io/backend/api/dsp',
      createdByUserFullName: 'Your Name',
      createdByOrgName: 'Your Org Name',
      createdByOrganizationId: 'MDSL1234XX',
    },
    {
      centralComponentId: 'MDSL1234XX.C0002XX',
      name: 'Broker MDS 2.0 Test',
      homepageUrl: 'https://broker2.dev.my-dataspace.sovity.io',
      endpointUrl: 'https://broker2.dev.my-dataspace.sovity.io/backend/api/dsp',
      createdByUserFullName: 'Your Name',
      createdByOrgName: 'Your Org Name',
      createdByOrganizationId: 'MDSL1234XX',
    },
  ],
};

export const centralComponentList = (
  environmentId: string,
): CentralComponentDto[] => centralComponents[environmentId] ?? [];

export const createCentralComponent = (
  request: CentralComponentCreateRequest,
  environmentId: string,
): IdResponse => {
  const newCentralComponent: CentralComponentDto = {
    centralComponentId:
      'MDSL1234XX.C' + Math.random().toString().substring(2).substring(0, 6),
    name: request.name,
    endpointUrl: request.endpointUrl,
    homepageUrl: request.homepageUrl,
    createdByOrganizationId: 'MDSL1234XX',
    createdByOrgName: 'Your Org Name',
    createdByUserFullName: 'Your Name',
  };

  updateEnv(environmentId, (list) => [...list, newCentralComponent]);

  return {id: newCentralComponent.centralComponentId, changedDate: new Date()};
};

export const deleteCentralComponent = (
  centralComponentId: string,
): IdResponse => {
  updateAllEnvs((list) =>
    list.filter((it) => it.centralComponentId !== centralComponentId),
  );

  return {id: centralComponentId, changedDate: new Date()};
};

/**
 * Updates the list of central components under the given environment id.
 * @param envId environment id
 * @param mapper list mapper
 */
const updateEnv = (
  envId: string,
  mapper: (it: CentralComponentDto[]) => CentralComponentDto[],
): void => {
  centralComponents = {
    ...centralComponents,
    [envId]: mapper(centralComponents[envId] ?? []),
  };
};

/**
 * Updates the lists of central components of all deployment environments
 * @param mapper list mapper
 */
const updateAllEnvs = (
  mapper: (it: CentralComponentDto[]) => CentralComponentDto[],
): void => {
  centralComponents = Object.fromEntries(
    Object.entries(centralComponents).map(([envId, list]) => [
      envId,
      mapper(list),
    ]),
  );
};
