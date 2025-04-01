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
import {catchError, ignoreElements, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {ConnectorDetailsDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {
  RefreshConnector,
  RefreshConnectorSilent,
  SetConnectorId,
} from './authority-connector-detail-page-actions';
import {
  AuthorityConnectorDetailPageState,
  DEFAULT_AUTHORITY_CONNECTOR_DETAIL_PAGE_STATE,
} from './authority-connector-detail-page-state';

@State<AuthorityConnectorDetailPageState>({
  name: 'AuthorityConnectorDetailPageState',
  defaults: DEFAULT_AUTHORITY_CONNECTOR_DETAIL_PAGE_STATE,
})
@Injectable()
export class AuthorityConnectorDetailPageStateImpl {
  constructor(private apiService: ApiService) {}

  @Action(RefreshConnector, {cancelUncompleted: true})
  onRefreshConnector(
    ctx: StateContext<AuthorityConnectorDetailPageState>,
  ): Observable<never> {
    return this.apiService.getConnector(ctx.getState().connectorId).pipe(
      Fetched.wrap({failureMessage: 'Failed loading Connector'}),
      tap((connector) => this.connectorRefreshed(ctx, connector)),
      ignoreElements(),
    );
  }

  @Action(RefreshConnectorSilent)
  onRefreshConnectorSilent(
    ctx: StateContext<AuthorityConnectorDetailPageState>,
  ): Observable<never> {
    return this.apiService.getConnector(ctx.getState().connectorId).pipe(
      catchError(() => EMPTY),
      tap((connector) =>
        this.connectorRefreshed(ctx, Fetched.ready(connector)),
      ),
      ignoreElements(),
    );
  }

  private connectorRefreshed(
    ctx: StateContext<AuthorityConnectorDetailPageState>,
    connector: Fetched<ConnectorDetailsDto>,
  ) {
    ctx.patchState({connector});
  }

  @Action(SetConnectorId, {cancelUncompleted: true})
  onSetConnectorId(
    ctx: StateContext<AuthorityConnectorDetailPageState>,
    action: SetConnectorId,
  ) {
    ctx.patchState({connectorId: action.connectorId});
  }
}
