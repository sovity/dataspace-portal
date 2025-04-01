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
import {Observable} from 'rxjs';
import {ignoreElements, map, switchMap, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {OrganizationOverviewEntryDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {Fetched} from 'src/app/core/utils/fetched';
import {
  CloseOrganizationDetail,
  RefreshOrganizations,
  ShowOrganizationDetail,
} from './authority-organization-list-page-actions';
import {
  AuthorityOrganizationListPageState,
  DEFAULT_AUTHORITY_ORGANIZATION_LIST_PAGE_STATE,
} from './authority-organization-list-page-state';

@State<AuthorityOrganizationListPageState>({
  name: 'AuthorityOrganizationListPageState',
  defaults: DEFAULT_AUTHORITY_ORGANIZATION_LIST_PAGE_STATE,
})
@Injectable()
export class AuthorityOrganizationListPageStateImpl {
  constructor(
    private apiService: ApiService,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  @Action(RefreshOrganizations, {cancelUncompleted: true})
  onRefreshOrganizations(
    ctx: StateContext<AuthorityOrganizationListPageState>,
  ): Observable<never> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((deploymentEnvironmentId) =>
        this.apiService.getOrganizationsForAuthority(deploymentEnvironmentId),
      ),
      map((result) => result.organizations),
      Fetched.wrap({failureMessage: 'Failed loading organizations'}),
      tap((organizations) => this.organizationsRefreshed(ctx, organizations)),
      ignoreElements(),
    );
  }

  @Action(ShowOrganizationDetail)
  onShowConnectorDetail(ctx: StateContext<AuthorityOrganizationListPageState>) {
    ctx.patchState({showDetail: true});
  }

  @Action(CloseOrganizationDetail)
  onCloseConnectorDetail(
    ctx: StateContext<AuthorityOrganizationListPageState>,
  ) {
    ctx.patchState({showDetail: false});
  }

  private organizationsRefreshed(
    ctx: StateContext<AuthorityOrganizationListPageState>,
    newOrganizations: Fetched<OrganizationOverviewEntryDto[]>,
  ) {
    ctx.patchState({organizations: newOrganizations});
  }
}
