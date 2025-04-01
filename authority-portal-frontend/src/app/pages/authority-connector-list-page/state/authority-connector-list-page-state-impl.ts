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
import {EMPTY, Observable} from 'rxjs';
import {
  catchError,
  filter,
  finalize,
  ignoreElements,
  map,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs/operators';
import {Action, Actions, State, StateContext} from '@ngxs/store';
import {ConnectorOverviewEntryDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {ErrorService} from 'src/app/core/services/error.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {
  CloseConnectorDetail,
  DeleteConnector,
  GetConnectors,
  GetConnectorsSilent,
  ShowConnectorDetail,
} from './authority-connector-list-page-actions';
import {
  AuthorityConnectorListPageState,
  DEFAULT_AUTHORITY_CONNECTOR_LIST_PAGE_STATE,
} from './authority-connector-list-page-state';

@State<AuthorityConnectorListPageState>({
  name: 'AuthorityConnectorListPageState',
  defaults: DEFAULT_AUTHORITY_CONNECTOR_LIST_PAGE_STATE,
})
@Injectable()
export class AuthorityConnectorListPageStateImpl {
  constructor(
    private apiService: ApiService,
    private globalStateUtils: GlobalStateUtils,
    private actions$: Actions,
    private errorService: ErrorService,
    private toast: ToastService,
  ) {}

  @Action(GetConnectors)
  onGetConnectors(
    ctx: StateContext<AuthorityConnectorListPageState>,
  ): Observable<never> {
    ctx.patchState({showDetail: false});
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((deploymentEnvironmentId) =>
        this.apiService.getAllConnectors(deploymentEnvironmentId),
      ),
      map((result) => result.connectors),
      Fetched.wrap({failureMessage: 'Failed loading connectors'}),
      tap((connectors) => this.connectorsRefreshed(ctx, connectors)),
      ignoreElements(),
    );
  }

  @Action(GetConnectorsSilent)
  onGetConnectorsSilent(
    ctx: StateContext<AuthorityConnectorListPageState>,
  ): Observable<never> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((deploymentEnvironmentId) =>
        this.apiService.getAllConnectors(deploymentEnvironmentId),
      ),
      map((result) => result.connectors),
      catchError((error) => EMPTY),
      tap((connectors) =>
        this.connectorsRefreshed(ctx, Fetched.ready(connectors)),
      ),
      ignoreElements(),
    );
  }

  private connectorsRefreshed(
    ctx: StateContext<AuthorityConnectorListPageState>,
    newConnectors: Fetched<ConnectorOverviewEntryDto[]>,
  ) {
    ctx.patchState({connectors: newConnectors});
  }

  @Action(DeleteConnector)
  onDeleteConnector(
    ctx: StateContext<AuthorityConnectorListPageState>,
    action: DeleteConnector,
  ): Observable<never> {
    if (ctx.getState().busy) {
      return EMPTY;
    }
    ctx.patchState({busy: true});

    return this.apiService.deleteProvidedConnector(action.connectorId).pipe(
      switchMap(() => this.globalStateUtils.getDeploymentEnvironmentId()),
      switchMap((deploymentEnvironmentId) =>
        this.apiService.getAllConnectors(deploymentEnvironmentId),
      ),
      takeUntil(
        this.actions$.pipe(filter((action) => action instanceof GetConnectors)),
      ),
      this.errorService.toastFailureRxjs('Deleting connector failed'),
      map((result) => result.connectors),
      tap((data) => {
        this.connectorsRefreshed(ctx, Fetched.ready(data));
        this.toast.showSuccess(
          `Connector ${action.connectorId} was successfully deleted`,
        );
        ctx.dispatch(CloseConnectorDetail);
      }),
      finalize(() => ctx.patchState({busy: false})),
      ignoreElements(),
    );
  }

  @Action(ShowConnectorDetail)
  onShowConnectorDetail(ctx: StateContext<AuthorityConnectorListPageState>) {
    ctx.patchState({showDetail: true});
  }

  @Action(CloseConnectorDetail)
  onCloseConnectorDetail(ctx: StateContext<AuthorityConnectorListPageState>) {
    ctx.patchState({showDetail: false});
  }
}
