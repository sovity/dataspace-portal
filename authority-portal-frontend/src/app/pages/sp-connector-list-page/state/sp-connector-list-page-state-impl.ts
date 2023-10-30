import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ignoreElements, map, switchMap, take, tap} from 'rxjs/operators';
import {Action, Selector, State, StateContext, Store} from '@ngxs/store';
import {ConnectorOverviewEntryDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalState} from 'src/app/core/global-state/global-state';
import {GlobalStateImpl} from 'src/app/core/global-state/global-state-impl';
import {Fetched} from 'src/app/core/utils/fetched';
import {GetOwnOrganizationConnectors} from './sp-connector-list-page-actions';
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
  constructor(private apiService: ApiService, private store: Store) {}

  @Action(GetOwnOrganizationConnectors)
  onGetOwnOrganizationConnectors(
    ctx: StateContext<SpConnectorListPageState>,
  ): Observable<never> {
    return this.store.select<GlobalState>(GlobalStateImpl).pipe(
      take(1),
      map((globalState) => globalState.selectedEnvironment),
      switchMap((selectedEnvironment) =>
        this.apiService.getOwnOrganizationConnectors(
          selectedEnvironment?.environmentId,
        ),
      ),
      map((result) => result.connectors),
      Fetched.wrap({failureMessage: 'Failed loading connectors'}),
      tap((connectors) => this.connectorsRefreshed(ctx, connectors)),
      ignoreElements(),
    );
  }

  private connectorsRefreshed(
    ctx: StateContext<SpConnectorListPageState>,
    newConnectors: Fetched<ConnectorOverviewEntryDto[]>,
  ) {
    ctx.patchState({connectors: newConnectors});
  }
}
