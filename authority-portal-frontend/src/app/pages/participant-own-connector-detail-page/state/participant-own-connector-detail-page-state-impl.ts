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
} from './participant-own-connector-detail-page-actions';
import {
  DEFAULT_PARTICIPANT_OWN_CONNECTOR_DETAIL_PAGE_STATE,
  ParticipantOwnConnectorDetailPageState,
} from './participant-own-connector-detail-page-state';

@State<ParticipantOwnConnectorDetailPageState>({
  name: 'ParticipantOwnConnectorDetailPageState',
  defaults: DEFAULT_PARTICIPANT_OWN_CONNECTOR_DETAIL_PAGE_STATE,
})
@Injectable()
export class ParticipantOwnConnectorDetailPageStateImpl {
  constructor(private apiService: ApiService) {}

  @Action(RefreshConnector, {cancelUncompleted: true})
  onRefreshConnector(
    ctx: StateContext<ParticipantOwnConnectorDetailPageState>,
    action: RefreshConnector,
  ): Observable<never> {
    return this.apiService
      .getOwnOrganizationConnectorDetails(ctx.getState().connectorId)
      .pipe(
        Fetched.wrap({failureMessage: 'Failed loading Connector'}),
        tap((connector) => this.connectorRefreshed(ctx, connector)),
        ignoreElements(),
      );
  }

  @Action(RefreshConnectorSilent, {cancelUncompleted: true})
  onRefreshConnectorSilent(
    ctx: StateContext<ParticipantOwnConnectorDetailPageState>,
    action: RefreshConnector,
  ): Observable<never> {
    return this.apiService
      .getOwnOrganizationConnectorDetails(ctx.getState().connectorId)
      .pipe(
        catchError(() => EMPTY),
        tap((connector) => {
          this.connectorRefreshed(ctx, Fetched.ready(connector));
        }),
        ignoreElements(),
      );
  }

  private connectorRefreshed(
    ctx: StateContext<ParticipantOwnConnectorDetailPageState>,
    connector: Fetched<ConnectorDetailsDto>,
  ) {
    ctx.patchState({connector});
  }

  @Action(SetConnectorId, {cancelUncompleted: true})
  onSetConnectorId(
    ctx: StateContext<ParticipantOwnConnectorDetailPageState>,
    action: SetConnectorId,
  ) {
    ctx.patchState({connectorId: action.connectorId});
  }
}
