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
  filter,
  finalize,
  ignoreElements,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs/operators';
import {Action, Actions, State, StateContext} from '@ngxs/store';
import {CentralComponentDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {ErrorService} from 'src/app/core/services/error.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {
  DeleteCentralComponent,
  RefreshCentralComponents,
} from './central-component-list-page-actions';
import {
  CentralComponentListPageState,
  DEFAULT_CENTRAL_COMPONENT_LIST_PAGE_STATE,
} from './central-component-list-page-state';

@State<CentralComponentListPageState>({
  name: 'CentralComponentListPageState',
  defaults: DEFAULT_CENTRAL_COMPONENT_LIST_PAGE_STATE,
})
@Injectable()
export class CentralComponentListPageStateImpl {
  constructor(
    private apiService: ApiService,
    private actions$: Actions,
    private toast: ToastService,
    private errorService: ErrorService,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  @Action(RefreshCentralComponents)
  onRefreshCentralComponents(
    ctx: StateContext<CentralComponentListPageState>,
  ): Observable<never> {
    return this.fetchCentralComponents().pipe(
      Fetched.wrap({failureMessage: 'Failed loading central components'}),
      tap((centralComponents) =>
        ctx.patchState({centralComponents: centralComponents}),
      ),
      ignoreElements(),
    );
  }
  @Action(DeleteCentralComponent)
  onShowDeleteCentralComponentModal(
    ctx: StateContext<CentralComponentListPageState>,
    action: DeleteCentralComponent,
  ): Observable<any> {
    if (ctx.getState().busy) {
      return EMPTY;
    }
    let centralComponent = action.centralComponent;
    ctx.patchState({
      busyDeletingComponentId: centralComponent.centralComponentId,
    });
    ctx.patchState({
      busy: true,
      busyDeletingComponentId: centralComponent.centralComponentId,
    });

    return this.apiService
      .deleteCentralComponent(
        centralComponent.centralComponentId,
        centralComponent.environmentId,
      )
      .pipe(
        switchMap(() => this.fetchCentralComponents()),
        takeUntil(
          this.actions$.pipe(
            filter((action) => action instanceof RefreshCentralComponents),
          ),
        ),
        this.errorService.toastFailureRxjs("Central Component wasn't deleted"),
        tap((data) => {
          ctx.patchState({centralComponents: Fetched.ready(data)});
          this.toast.showSuccess(
            `Central Component ${centralComponent.name} was successfully deleted`,
          );
        }),
        finalize(() =>
          ctx.patchState({busy: false, busyDeletingComponentId: null}),
        ),
        ignoreElements(),
      );
  }

  private fetchCentralComponents(): Observable<CentralComponentDto[]> {
    return this.globalStateUtils
      .getDeploymentEnvironmentId()
      .pipe(
        switchMap((deploymentEnvironmentId) =>
          this.apiService.getCentralComponents(deploymentEnvironmentId),
        ),
      );
  }
}
