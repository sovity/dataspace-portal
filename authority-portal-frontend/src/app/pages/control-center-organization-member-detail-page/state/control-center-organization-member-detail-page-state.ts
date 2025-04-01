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
import {UserDetailDto} from '@sovity.de/authority-portal-client';
import {Fetched} from 'src/app/core/utils/fetched';
import {UserDetailConfig} from '../../../shared/business/shared-user-detail/shared-user-detail.model';
import {HeaderBarConfig} from '../../../shared/common/header-bar/header-bar.model';

export interface ControlCenterOrganizationMemberDetailPageState {
  user: Fetched<UserDetailDto>;
  userDetailConfig: UserDetailConfig | null;
  headerBarConfig: HeaderBarConfig | null;
  busy: boolean;
}

export const DEFAULT_CONTROL_CENTER_ORGANIZATION_MEMBER_DETAIL_PAGE_STATE: ControlCenterOrganizationMemberDetailPageState =
  {
    user: Fetched.empty(),
    userDetailConfig: null,
    headerBarConfig: null,
    busy: false,
  };
