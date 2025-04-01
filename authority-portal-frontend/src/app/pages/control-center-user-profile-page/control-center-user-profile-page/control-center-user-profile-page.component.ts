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
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {Reset} from '../state/control-center-user-profile-page-action';
import {
  ControlCenterUserProfilePageState,
  DEFAULT_CONTROL_CENTER_USER_PROFILE_PAGE_STATE,
} from '../state/control-center-user-profile-page-state';
import {ControlCenterUserProfilePageStateImpl} from '../state/control-center-user-profile-page-state-impl';

@Component({
  selector: 'app-control-center-user-profile-page',
  templateUrl: './control-center-user-profile-page.component.html',
})
export class ControlCenterUserProfilePageComponent
  implements OnInit, OnDestroy
{
  state: ControlCenterUserProfilePageState =
    DEFAULT_CONTROL_CENTER_USER_PROFILE_PAGE_STATE;

  constructor(
    private store: Store,
    private globalStateUtils: GlobalStateUtils,
    private titleService: Title,
  ) {
    this.titleService.setTitle('My Profile');
  }

  ngOnInit(): void {
    this.refresh();
    this.startListeningToState();
    this.startRefreshingOnEnvChange();
  }

  refresh(): void {
    this.store.dispatch(new Reset(this.ngOnDestroy$));
  }

  startListeningToState(): void {
    this.store
      .select<ControlCenterUserProfilePageState>(
        ControlCenterUserProfilePageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
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
