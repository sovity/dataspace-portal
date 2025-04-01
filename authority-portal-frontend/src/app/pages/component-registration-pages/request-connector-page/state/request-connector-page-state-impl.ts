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
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {ignoreElements, switchMap, takeUntil, tap} from 'rxjs/operators';
import {Action, Actions, State, StateContext, ofAction} from '@ngxs/store';
import {CreateConnectorResponse} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {ErrorService} from 'src/app/core/services/error.service';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {Reset, Submit} from './request-connector-page-actions';
import {
  DEFAULT_REQUEST_CONNECTOR_STATE,
  RequestConnectorPageState,
} from './request-connector-page-state';

@State<RequestConnectorPageState>({
  name: 'RequestConnectorPage',
  defaults: DEFAULT_REQUEST_CONNECTOR_STATE,
})
@Injectable()
export class RequestConnectorPageStateImpl {
  constructor(
    private apiService: ApiService,
    private actions$: Actions,
    private toast: ToastService,
    private errorService: ErrorService,
    private globalStateUtils: GlobalStateUtils,
    private router: Router,
  ) {}

  @Action(Reset)
  onReset(ctx: StateContext<RequestConnectorPageState>): void {
    ctx.setState(DEFAULT_REQUEST_CONNECTOR_STATE);
  }

  @Action(Submit, {cancelUncompleted: true})
  onSubmit(
    ctx: StateContext<RequestConnectorPageState>,
    action: Submit,
  ): Observable<never> {
    ctx.patchState({state: 'submitting'});
    action.disableForm();

    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap(
        (deploymentEnvironmentId): Observable<CreateConnectorResponse> =>
          this.apiService.createCaas(action.request, deploymentEnvironmentId),
      ),
      tap((res) => {
        switch (res.status) {
          case 'OK':
            this.toast.showSuccess(
              `Connector ${action.request.connectorTitle} requested successfully. You will receive an E-Mail confirming the deployment in the next few minutes.`,
            );
            ctx.patchState({state: 'success'});
            this.router.navigate(['/', 'my-organization', 'connectors']);
            break;
          case 'WARNING':
            this.toast.showWarning(
              res?.message ||
                'A problem occurred while requesting the connector.',
            );
            ctx.patchState({state: 'success'});
            break;
          case 'ERROR':
            this.toast.showDanger(
              res?.message || 'Failed requesting connector',
            );
            ctx.patchState({state: 'error'});
            action.enableForm();
            break;
        }
      }),
      takeUntil(this.actions$.pipe(ofAction(Reset))),
      this.errorService.toastFailureRxjs('Failed requesting connector', () => {
        ctx.patchState({state: 'error'});
        action.enableForm();
      }),
      ignoreElements(),
    );
  }
}
