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
import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {ignoreElements, switchMap, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {
  OwnOrganizationDetailsDto,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {GlobalStateUtils} from '../../../core/global-state/global-state-utils';
import {HeaderBarConfig} from '../../../shared/common/header-bar/header-bar.model';
import {Reset} from './control-center-organization-profile-page-action';
import {
  ControlCenterOrganizationProfilePageState,
  DEFAULT_CONTROL_CENTER_ORGANIZATION_PROFILE_PAGE_STATE,
} from './control-center-organization-profile-page-state';

type Ctx = StateContext<ControlCenterOrganizationProfilePageState>;

@State<ControlCenterOrganizationProfilePageState>({
  name: 'ControlCenterOrganizationProfilePageState',
  defaults: DEFAULT_CONTROL_CENTER_ORGANIZATION_PROFILE_PAGE_STATE,
})
@Injectable()
export class ControlCenterOrganizationProfilePageStateImpl {
  constructor(
    private apiService: ApiService,
    private router: Router,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  @Action(Reset)
  onReset(ctx: Ctx): Observable<never> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((environmentId) =>
        this.apiService.getOwnOrganizationDetails(environmentId),
      ),
      Fetched.wrap({failureMessage: 'Failed to fetch user details'}),
      tap((organization) => {
        ctx.patchState({
          organization,
          headerBarConfig: organization
            .map((data) => this.buildHeaderBarConfig(data))
            .orElse(null),
        });
      }),
      ignoreElements(),
    );
  }

  private buildHeaderBarConfig(
    organization: OwnOrganizationDetailsDto,
  ): HeaderBarConfig {
    return {
      title: organization.name,
      subtitle: 'Your Organization Profile',
      headerActions: [
        {
          label: 'Edit',
          action: () =>
            this.router.navigate(['/control-center/my-organization/edit']),
          permissions: [UserRoleDto.Admin],
        },
      ],
    };
  }
}
