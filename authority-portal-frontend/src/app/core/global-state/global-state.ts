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
  UserInfo,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {Fetched} from 'src/app/core/utils/fetched';
import {E2eDevUser} from '../../shared/dev-utils/e2e-dev-user-switcher/e2e-dev-user';
import {AuthorityPortalPageSet} from './routes/authority-portal-page-set';

export interface GlobalState {
  pageSet: AuthorityPortalPageSet;
  userInfo: Fetched<UserInfo>;
  roles: Set<UserRoleDto>;

  e2eDevUser: E2eDevUser | null;
  deploymentEnvironments: Fetched<DeploymentEnvironmentDto[]>;
  selectedEnvironment: DeploymentEnvironmentDto | null;
}

export const INITIAL_GLOBAL_STATE_MODEL: GlobalState = {
  pageSet: 'LOADING',
  userInfo: Fetched.empty(),
  roles: new Set(),
  e2eDevUser: null,
  deploymentEnvironments: Fetched.empty(),
  selectedEnvironment: null,
};
