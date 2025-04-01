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
import {MatDialog} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {filter, ignoreElements, switchMap, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {
  OwnOrganizationDetailsDto,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {GlobalStateUtils} from '../../../../core/global-state/global-state-utils';
import {HeaderBarConfig} from '../../../../shared/common/header-bar/header-bar.model';
import {ParticipantInviteNewUserComponent} from '../../participant-invite-new-user/participant-invite-new-user.component';
import {Reset} from './control-center-organization-members-page-action';
import {
  ControlCenterOrganizationMembersPageState,
  DEFAULT_CONTROL_CENTER_ORGANIZATION_MEMBERS_PAGE_STATE,
} from './control-center-organization-members-page-state';

type Ctx = StateContext<ControlCenterOrganizationMembersPageState>;

@State<ControlCenterOrganizationMembersPageState>({
  name: 'ControlCenterOrganizationMembersPageState',
  defaults: DEFAULT_CONTROL_CENTER_ORGANIZATION_MEMBERS_PAGE_STATE,
})
@Injectable()
export class ControlCenterOrganizationMembersPageStateImpl {
  constructor(
    private apiService: ApiService,
    private dialog: MatDialog,
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
            .map((data) => this.buildHeaderBarConfig(ctx, data))
            .orElse(null),
        });
      }),
      ignoreElements(),
    );
  }

  private buildHeaderBarConfig(
    ctx: Ctx,
    organization: OwnOrganizationDetailsDto,
  ): HeaderBarConfig {
    return {
      title: organization.name,
      subtitle: 'Your Organization Members',
      headerActions: [
        {
          label: 'Invite user',
          action: () => this.onShowInviteUserDialog(ctx),
          permissions: [UserRoleDto.Admin],
        },
      ],
    };
  }

  private onShowInviteUserDialog(ctx: Ctx): void {
    this.dialog
      .open(ParticipantInviteNewUserComponent, {
        width: window.innerWidth > 640 ? '60%' : '100%',
      })
      .afterClosed()
      .pipe(filter((it) => !!it))
      .subscribe(() => {
        ctx.dispatch(Reset);
      });
  }
}
