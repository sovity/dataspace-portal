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
import {Action, Actions, State, StateContext, Store} from '@ngxs/store';
import {ProvidedConnectorOverviewEntryDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {ErrorService} from 'src/app/core/services/error.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {
  CloseConnectorDetail,
  DeleteProvidedConnector,
  GetProvidedConnectors,
  GetProvidedConnectorsSilent,
  ShowConnectorDetail,
} from './sp-connector-list-page-actions';
import {
  DEFAULT_SP_CONNECTOR_LIST_PAGE_STATE,
  SpConnectorListPageState,
} from './sp-connector-list-page-state';

@State<SpConnectorListPageState>({
  name: 'SpConnectorListPageState',
  defaults: DEFAULT_SP_CONNECTOR_LIST_PAGE_STATE,
})
@Injectable()
export class SpConnectorListPageStateImpl {
  constructor(
    private apiService: ApiService,
    private actions$: Actions,
    private toast: ToastService,
    private errorService: ErrorService,
    private globalStateUtils: GlobalStateUtils,
    private store: Store,
  ) {}

  @Action(GetProvidedConnectors)
  onGetProvidedConnectors(
    ctx: StateContext<SpConnectorListPageState>,
  ): Observable<never> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((deploymentEnvironmentId) =>
        this.apiService.getProvidedConnectors(deploymentEnvironmentId),
      ),
      map((result) => result.connectors),
      Fetched.wrap({failureMessage: 'Failed loading connectors'}),
      tap((connectors) => this.connectorsRefreshed(ctx, connectors)),
      ignoreElements(),
    );
  }

  @Action(GetProvidedConnectorsSilent)
  onGetProvidedConnectorsSilent(
    ctx: StateContext<SpConnectorListPageState>,
  ): Observable<never> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((deploymentEnvironmentId) =>
        this.apiService.getProvidedConnectors(deploymentEnvironmentId),
      ),
      map((result) => result.connectors),
      catchError(() => EMPTY),
      tap((connectors) =>
        this.connectorsRefreshed(ctx, Fetched.ready(connectors)),
      ),
      ignoreElements(),
    );
  }

  private connectorsRefreshed(
    ctx: StateContext<SpConnectorListPageState>,
    newConnectors: Fetched<ProvidedConnectorOverviewEntryDto[]>,
  ) {
    ctx.patchState({connectors: newConnectors});
  }

  @Action(DeleteProvidedConnector)
  onDeleteProvidedConnector(
    ctx: StateContext<SpConnectorListPageState>,
    action: DeleteProvidedConnector,
  ): Observable<never> {
    if (ctx.getState().busy) {
      return EMPTY;
    }
    ctx.patchState({busy: true});

    return this.apiService.deleteProvidedConnector(action.connectorId).pipe(
      switchMap(() => this.globalStateUtils.getDeploymentEnvironmentId()),
      switchMap((deploymentEnvironmentId) =>
        this.apiService.getProvidedConnectors(deploymentEnvironmentId),
      ),
      takeUntil(
        this.actions$.pipe(
          filter((action) => action instanceof GetProvidedConnectors),
        ),
      ),
      this.errorService.toastFailureRxjs("Connector wasn't deleted"),
      map((result) => result.connectors),
      tap((data) => {
        this.connectorsRefreshed(ctx, Fetched.ready(data));
        this.toast.showSuccess(
          `Connector ${action.connectorId} was successfully deleted`,
        );
        this.store.dispatch(CloseConnectorDetail);
      }),
      finalize(() => ctx.patchState({busy: false})),
      ignoreElements(),
    );
  }

  @Action(ShowConnectorDetail)
  onShowConnectorDetail(ctx: StateContext<SpConnectorListPageState>) {
    ctx.patchState({showDetail: true});
  }

  @Action(CloseConnectorDetail)
  onCloseConnectorDetail(ctx: StateContext<SpConnectorListPageState>) {
    ctx.patchState({showDetail: false});
  }
}
