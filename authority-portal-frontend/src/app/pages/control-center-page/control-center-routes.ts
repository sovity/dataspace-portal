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
import {Route} from '@angular/router';
import {UserRoleDto} from '@sovity.de/authority-portal-client';
import {ControlCenterOrganizationEditPageComponent} from '../control-center-organization-edit-page/control-center-organization-edit-page/control-center-organization-edit-page.component';
import {ControlCenterOrganizationMemberDetailPageComponent} from '../control-center-organization-member-detail-page/control-center-organization-member-detail-page/control-center-organization-member-detail-page.component';
import {ControlCenterOrganizationMembersPageComponent} from '../control-center-organization-members-page/control-center-organization-members-page/control-center-organization-members-page.component';
import {ControlCenterOrganizationProfilePageComponent} from '../control-center-organization-profile-page/control-center-organization-profile-page/control-center-organization-profile-page.component';
import {ControlCenterUserEditPageComponent} from '../control-center-user-edit-page/control-center-user-edit-page/control-center-user-edit-page.component';
import {ControlCenterUserProfilePageComponent} from '../control-center-user-profile-page/control-center-user-profile-page/control-center-user-profile-page.component';
import {PageNotFoundPageComponent} from '../empty-pages/page-not-found-page/page-not-found-page/page-not-found-page.component';

export type ControlCenterRoute = Route & {
  data:
    | {
        title: string;
      }
    | {
        excludeFromTabs: true;
        requiredRoles?: UserRoleDto[];
      };
};

export const CONTROL_CENTER_ROUTES: ControlCenterRoute[] = [
  {
    path: 'my-profile',
    component: ControlCenterUserProfilePageComponent,
    data: {
      title: 'My Profile',
    },
  },
  {
    path: 'my-profile/edit',
    component: ControlCenterUserEditPageComponent,
    data: {
      excludeFromTabs: true,
    },
  },
  {
    path: 'my-organization',
    component: ControlCenterOrganizationProfilePageComponent,
    data: {
      title: 'My Organization',
    },
  },
  {
    path: 'my-organization/edit',
    component: ControlCenterOrganizationEditPageComponent,
    data: {
      excludeFromTabs: true,
      requiredRoles: ['ADMIN'],
    },
  },
  {
    path: 'users-and-roles',
    component: ControlCenterOrganizationMembersPageComponent,
    data: {
      title: 'Users and Roles',
    },
  },
  {
    path: 'users-and-roles/:userId',
    component: ControlCenterOrganizationMemberDetailPageComponent,
    data: {
      excludeFromTabs: true,
    },
  },
  {
    path: '**',
    component: PageNotFoundPageComponent,
    pathMatch: 'full',
    data: {
      excludeFromTabs: true,
    },
  },
];
