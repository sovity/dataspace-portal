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
  OrganizationDetailsDto,
  UserDetailDto,
} from '@sovity.de/authority-portal-client';
import {Fetched} from 'src/app/core/utils/fetched';

export interface AuthorityOrganizationUserDetailState {
  userId: string;
  organizationId: string;
  user: Fetched<UserDetailDto>;
  busy: boolean;
}

export interface AuthorityOrganizationDetailState {
  organizationId: string;
  organization: Fetched<OrganizationDetailsDto>;
  busy: boolean;
}

export interface AuthorityOrganizationDetailPageState {
  organizationDetail: AuthorityOrganizationDetailState;
  openedUserDetail: AuthorityOrganizationUserDetailState;
}

export const DEFAULT_AUTHORITY_ORGANIZATION_USER_DETAIL_STATE: AuthorityOrganizationUserDetailState =
  {
    userId: '',
    organizationId: '',
    user: Fetched.empty(),
    busy: false,
  };

export const DEFAULT_AUTHORITY_ORGANIZATION_DETAIL_STATE: AuthorityOrganizationDetailState =
  {
    organizationId: '',
    organization: Fetched.empty(),
    busy: false,
  };

export const DEFAULT_AUTHORITY_ORGANIZATION_DETAIL_PAGE_STATE: AuthorityOrganizationDetailPageState =
  {
    organizationDetail: DEFAULT_AUTHORITY_ORGANIZATION_DETAIL_STATE,
    openedUserDetail: DEFAULT_AUTHORITY_ORGANIZATION_USER_DETAIL_STATE,
  };
