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
import {Component, OnDestroy, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {Router} from '@angular/router';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {MemberInfo} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {BreadcrumbService} from '../../../shared/common/portal-layout/breadcrumb/breadcrumb.service';
import {Reset} from './state/control-center-organization-members-page-action';
import {
  ControlCenterOrganizationMembersPageState,
  DEFAULT_CONTROL_CENTER_ORGANIZATION_MEMBERS_PAGE_STATE,
} from './state/control-center-organization-members-page-state';
import {ControlCenterOrganizationMembersPageStateImpl} from './state/control-center-organization-members-page-state-impl';

@Component({
  selector: 'app-control-center-organization-members-page',
  templateUrl: './control-center-organization-members-page.component.html',
})
export class ControlCenterOrganizationMembersPageComponent
  implements OnInit, OnDestroy
{
  state: ControlCenterOrganizationMembersPageState =
    DEFAULT_CONTROL_CENTER_ORGANIZATION_MEMBERS_PAGE_STATE;

  constructor(
    private store: Store,
    private router: Router,
    private breadcrumbService: BreadcrumbService,
    private globalStateUtils: GlobalStateUtils,
    private titleService: Title,
  ) {
    this.titleService.setTitle('Users and Roles');
  }

  ngOnInit(): void {
    this.refresh();
    this.startListeningToState();
    this.startRefreshingOnEnvChange();
  }

  refresh(): void {
    this.store.dispatch(Reset);
  }

  startListeningToState(): void {
    this.store
      .select<ControlCenterOrganizationMembersPageState>(
        ControlCenterOrganizationMembersPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  onSelectUser(user: MemberInfo): void {
    if (user.firstName || user.lastName) {
      this.breadcrumbService.addReplacement(
        user.userId,
        `${user.firstName} ${user.lastName}`,
      );
    }
    this.router.navigate(['control-center/users-and-roles', user.userId]);
  }

  startRefreshingOnEnvChange() {
    this.globalStateUtils.onDeploymentEnvironmentChangeSkipFirst({
      ngOnDestroy$: this.ngOnDestroy$,
      onChanged: () => {
        this.refresh();
      },
    });
  }

  ngOnDestroy$ = new Subject();
  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
