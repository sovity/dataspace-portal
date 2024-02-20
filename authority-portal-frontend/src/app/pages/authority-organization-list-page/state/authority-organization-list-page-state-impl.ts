import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ignoreElements, map, switchMap, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {OrganizationOverviewEntryDto} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {ApiService} from '../../../core/api/api.service';
import {Fetched} from '../../../core/utils/fetched';
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
