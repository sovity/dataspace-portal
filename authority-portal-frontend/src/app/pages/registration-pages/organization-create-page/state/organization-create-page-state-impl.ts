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
import {Inject, Injectable} from '@angular/core';
import {EMPTY, Observable, of} from 'rxjs';
import {catchError, ignoreElements, takeUntil, tap} from 'rxjs/operators';
import {Action, Actions, State, StateContext, ofAction} from '@ngxs/store';
import {ApiService} from 'src/app/core/api/api.service';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {ErrorService} from '../../../../core/services/error.service';
import {CreateOrganization, Reset} from './organization-create-page-action';
import {
  DEFAULT_ORGANIZATION_REGISTRATION_PAGE_STATE,
  OrganizationRegistrationPageState,
} from './organization-create-page-state';

@State<OrganizationRegistrationPageState>({
  name: 'OrganizationCreatePage',
  defaults: DEFAULT_ORGANIZATION_REGISTRATION_PAGE_STATE,
})
@Injectable()
export class OrganizationCreatePageStateImpl {
  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private apiService: ApiService,
    private toast: ToastService,
    private actions$: Actions,
    private errorService: ErrorService,
  ) {}

  @Action(Reset)
  onReset(ctx: StateContext<OrganizationRegistrationPageState>): void {
    ctx.setState(DEFAULT_ORGANIZATION_REGISTRATION_PAGE_STATE);
  }

  @Action(CreateOrganization, {cancelUncompleted: true})
  onCreateOrganization(
    ctx: StateContext<OrganizationRegistrationPageState>,
    action: CreateOrganization,
  ): Observable<never> {
    ctx.patchState({state: 'submitting'});
    action.disableForm();
    ctx.patchState({email: action.request.userEmail});
    return this.apiService.registerOrganization(action.request).pipe(
      tap((res) => {
        this.toast.showSuccess(`Successfully registered.`);
        ctx.patchState({state: 'success'});
        ctx.patchState({id: res.id ?? ''});
        action.success();
      }),
      takeUntil(this.actions$.pipe(ofAction(Reset))),
      this.errorService.toastRegistrationErrorRxjs(
        'Registration failed due to an unknown error',
        () => {
          ctx.patchState({state: 'error'});
          action.enableForm();
        },
      ),
      ignoreElements(),
    );
  }
}
