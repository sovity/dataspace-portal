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
import {ActivatedRoute} from '@angular/router';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {Reset} from '../state/control-center-organization-member-detail-page-action';
import {
  ControlCenterOrganizationMemberDetailPageState,
  DEFAULT_CONTROL_CENTER_ORGANIZATION_MEMBER_DETAIL_PAGE_STATE,
} from '../state/control-center-organization-member-detail-page-state';
import {ControlCenterOrganizationMemberDetailPageStateImpl} from '../state/control-center-organization-member-detail-page-state-impl';

@Component({
  selector: 'app-control-center-organization-member-detail-page',
  templateUrl:
    './control-center-organization-member-detail-page.component.html',
})
export class ControlCenterOrganizationMemberDetailPageComponent
  implements OnInit, OnDestroy
{
  state: ControlCenterOrganizationMemberDetailPageState =
    DEFAULT_CONTROL_CENTER_ORGANIZATION_MEMBER_DETAIL_PAGE_STATE;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private titleService: Title,
  ) {
    this.titleService.setTitle('Member Details');
  }

  ngOnInit(): void {
    this.startRefreshingOnRouteChanges();
    this.startListeningToState();
  }

  startRefreshingOnRouteChanges(): void {
    this.activatedRoute.params.subscribe((params) => {
      this.store.dispatch(new Reset(params.userId, this.ngOnDestroy$));
    });
  }

  startListeningToState(): void {
    this.store
      .select<ControlCenterOrganizationMemberDetailPageState>(
        ControlCenterOrganizationMemberDetailPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  ngOnDestroy$ = new Subject();
  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
