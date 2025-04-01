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
import {Observable, takeUntil} from 'rxjs';
import {ignoreElements, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {UserDetailDto, UserRoleDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {CustomRxjsOperators} from 'src/app/core/services/custom-rxjs-operators';
import {Fetched} from 'src/app/core/utils/fetched';
import {UserDetailConfig} from '../../../shared/business/shared-user-detail/shared-user-detail.model';
import {buildUserRoleUpdateConfigFromUserInfo} from '../../../shared/business/shared-user-detail/user-role-update-config';
import {UserDeleteDialogService} from '../../../shared/business/user-delete-dialog/user-delete-dialog.service';
import {
  HeaderBarAction,
  HeaderBarConfig,
} from '../../../shared/common/header-bar/header-bar.model';
import {BreadcrumbService} from '../../../shared/common/portal-layout/breadcrumb/breadcrumb.service';
import {Reset} from './control-center-organization-member-detail-page-action';
import {
  ControlCenterOrganizationMemberDetailPageState,
  DEFAULT_CONTROL_CENTER_ORGANIZATION_MEMBER_DETAIL_PAGE_STATE,
} from './control-center-organization-member-detail-page-state';

type Ctx = StateContext<ControlCenterOrganizationMemberDetailPageState>;

@State<ControlCenterOrganizationMemberDetailPageState>({
  name: 'ControlCenterOrganizationMemberDetailPageState',
  defaults: DEFAULT_CONTROL_CENTER_ORGANIZATION_MEMBER_DETAIL_PAGE_STATE,
})
@Injectable()
export class ControlCenterOrganizationMemberDetailPageStateImpl {
  constructor(
    private apiService: ApiService,
    private globalStateUtils: GlobalStateUtils,
    private breadcrumbService: BreadcrumbService,
    private userDeleteDialogService: UserDeleteDialogService,
    private router: Router,
    private customRxjsOperators: CustomRxjsOperators,
  ) {}

  @Action(Reset, {cancelUncompleted: true})
  onReset(ctx: Ctx, action: Reset): Observable<never> {
    return this.apiService.getUserDetailDto(action.userId).pipe(
      Fetched.wrap({failureMessage: 'Failed to fetch user details'}),
      tap((user) => {
        user.ifReady((userDetails) => {
          if (userDetails.firstName || userDetails.lastName) {
            this.breadcrumbService.addReplacement(
              userDetails.userId,
              `${userDetails.firstName} ${userDetails.lastName}`,
            );
          }
        });
        ctx.patchState({
          user,
          headerBarConfig: user
            .map((data) =>
              this.buildHeaderBarConfig(ctx, data, action.componentLifetime$),
            )
            .orElse(null),
          userDetailConfig: user
            .map((userDetails) =>
              this.buildUserDetailConfig(
                ctx,
                userDetails,
                action.componentLifetime$,
              ),
            )
            .orElse(null),
        });
      }),
      takeUntil(action.componentLifetime$),
      ignoreElements(),
    );
  }

  private buildHeaderBarConfig(
    ctx: Ctx,
    user: UserDetailDto,
    componentLifetime$: Observable<any>,
  ): HeaderBarConfig {
    let headerActions: HeaderBarAction[] = [];

    if (this.globalStateUtils.userId === user.userId) {
      headerActions.push({
        label: 'Edit',
        action: () => this.router.navigate(['/control-center/my-profile/edit']),
        permissions: [UserRoleDto.User],
      });
      headerActions.push({
        label: 'Delete user',
        action: () => this.onDeleteUserClick(ctx, componentLifetime$),
        permissions: [UserRoleDto.User],
      });
    } else {
      headerActions.push({
        label: 'Delete user',
        action: () => this.onDeleteUserClick(ctx, componentLifetime$),
        permissions: [UserRoleDto.Admin],
      });

      if (user.registrationStatus === 'ACTIVE') {
        headerActions.push({
          label: 'Deactivate user',
          action: () => this.onDeactivateUserClick(ctx, componentLifetime$),
          permissions: [UserRoleDto.Admin],
        });
      }

      if (user.registrationStatus === 'DEACTIVATED') {
        headerActions.push({
          label: 'Reactivate user',
          action: () => this.onReactivateUserClick(ctx, componentLifetime$),
          permissions: [UserRoleDto.Admin],
        });
      }
    }

    return {
      title: `${user.firstName} ${user.lastName}`,
      subtitle: `Member Of ${user.organizationName}`,
      headerActions,
    };
  }

  private buildUserDetailConfig(
    ctx: Ctx,
    user: UserDetailDto,
    componentLifetime$: Observable<any>,
  ): UserDetailConfig {
    return {
      pageFor: 'INTERNAL_VIEW',
      userId: user.userId,
      user,
      usageType: 'CONTROL_CENTER_PAGE',
      roles: buildUserRoleUpdateConfigFromUserInfo({
        currentUser: this.globalStateUtils.userInfo,
        target: user,
        onRoleUpdateSuccessful: () => this.refresh(ctx, componentLifetime$),
      }),
    };
  }

  private onReactivateUserClick(ctx: Ctx, componentLifetime$: Observable<any>) {
    const user = ctx.getState().user.data;
    this.apiService
      .reactivateUser(user.userId)
      .pipe(
        this.customRxjsOperators.withBusyLock(ctx),
        this.customRxjsOperators.withToastResultHandling('Reactivating user'),
        tap(() => this.refresh(ctx, componentLifetime$)),
        takeUntil(componentLifetime$),
      )
      .subscribe();
  }

  private onDeactivateUserClick(ctx: Ctx, componentLifetime$: Observable<any>) {
    const user = ctx.getState().user.data;
    this.apiService
      .deactivateUser(user.userId)
      .pipe(
        this.customRxjsOperators.withBusyLock(ctx),
        this.customRxjsOperators.withToastResultHandling('Deactivating user'),
        tap(() => this.refresh(ctx, componentLifetime$)),
        takeUntil(componentLifetime$),
      )
      .subscribe();
  }

  private onDeleteUserClick(ctx: Ctx, componentLifetime$: Observable<any>) {
    const state = ctx.getState();
    let user = state.user.data;
    this.userDeleteDialogService.showDeleteUserModal(
      {
        userId: user.userId,
        userFullName: user.firstName + ' ' + user.lastName,
        userOrganizationName: user.organizationName,
        onDeleteSuccess: () => {
          this.router.navigate(['/control-center/users-and-roles']);
        },
      },
      componentLifetime$,
    );
  }

  private refresh(
    ctx: StateContext<ControlCenterOrganizationMemberDetailPageState>,
    componentLifetime$: Observable<any>,
  ) {
    ctx.dispatch(
      new Reset(ctx.getState().user.data.userId, componentLifetime$),
    );
  }
}
