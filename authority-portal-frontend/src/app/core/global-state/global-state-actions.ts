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
import {DeploymentEnvironmentDto} from '@sovity.de/authority-portal-client';
import {E2eDevUser} from '../../shared/dev-utils/e2e-dev-user-switcher/e2e-dev-user';

const tag = 'GlobalState';

export class RefreshUserInfo {
  static readonly type = `[${tag}] Load User`;
}

export class SwitchE2eDevUser {
  static readonly type = `[${tag}] Set Local Backend Basic Auth`;

  constructor(public readonly user: E2eDevUser) {}
}

export class RefreshDeploymentEnvironments {
  static readonly type = `[${tag}] Refresh Deployment Environments`;
}

export class SwitchEnvironment {
  static readonly type = `[${tag}] Switch Environment`;

  constructor(public readonly selectedEnvironment: DeploymentEnvironmentDto) {}
}
