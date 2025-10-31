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
  DeploymentEnvironmentDto,
  OrganizationOverviewEntryDto,
} from '@sovity.de/authority-portal-client';
import {Fetched} from 'src/app/core/utils/fetched';

export interface ReserveProvidedConnectorPageState {
  state: 'editing' | 'submitting' | 'success' | 'error';
  organizationList: Fetched<OrganizationOverviewEntryDto[]>;
  connectorId: string | null;
  deploymentEnvironment: DeploymentEnvironmentDto | null;
}

export const DEFAULT_RESERVE_PROVIDED_CONNECTOR_PAGE_STATE: ReserveProvidedConnectorPageState =
  {
    state: 'editing',
    organizationList: Fetched.empty(),
    connectorId: null,
    deploymentEnvironment: null,
  };
