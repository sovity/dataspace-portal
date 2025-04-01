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
import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {UserDetailConfig} from 'src/app/shared/business/shared-user-detail/shared-user-detail.model';
import {buildUserRoleUpdateConfigFromUserInfo} from '../../../../shared/business/shared-user-detail/user-role-update-config';
import {UserDetailPageConfig} from '../../authority-organization-detail-page/authority-organization-detail-page.model';
import {
  RefreshOrganizationUser,
  SetOrganizationUserId,
} from '../../state/authority-organization-detail-page-actions';
import {
  AuthorityOrganizationUserDetailState,
  DEFAULT_AUTHORITY_ORGANIZATION_USER_DETAIL_STATE,
} from '../../state/authority-organization-detail-page-state';
import {AuthorityOrganizationDetailPageStateImpl} from '../../state/authority-organization-detail-page-state-impl';

@Component({
  selector: 'app-organization-user-detail',
  templateUrl: './organization-user-detail.component.html',
})
export class OrganizationUserDetailComponent implements OnInit, OnDestroy {
  @Input() userDetailPageConfig!: UserDetailPageConfig;

  userDetailConfig!: UserDetailConfig;
  state = DEFAULT_AUTHORITY_ORGANIZATION_USER_DETAIL_STATE;
  ngOnDestroy$ = new Subject();

  constructor(
    private store: Store,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  ngOnInit(): void {
    this.setOrganizationUserId(
      this.userDetailPageConfig.organizationId,
      this.userDetailPageConfig.userId,
    );
    this.refresh();
    this.startListeningToState();
  }

  startListeningToState() {
    this.store
      .select<AuthorityOrganizationUserDetailState>(
        AuthorityOrganizationDetailPageStateImpl.openedUserDetailState,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
        if (state.user.isReady)
          this.userDetailConfig = {
            userId: state.userId,
            user: state.user.data,
            pageFor: 'AUTHORITY_VIEW',
            usageType: 'DETAIL_PAGE',
            roles: buildUserRoleUpdateConfigFromUserInfo({
              currentUser: this.globalStateUtils.userInfo,
              target: state.user.data,
              onRoleUpdateSuccessful: () => {
                this.store.dispatch(new RefreshOrganizationUser());
              },
            }),
          };
      });
  }

  setOrganizationUserId(organizationId: string, userId: string) {
    this.store.dispatch(new SetOrganizationUserId(organizationId, userId));
  }

  refresh() {
    this.store.dispatch(RefreshOrganizationUser);
  }

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
