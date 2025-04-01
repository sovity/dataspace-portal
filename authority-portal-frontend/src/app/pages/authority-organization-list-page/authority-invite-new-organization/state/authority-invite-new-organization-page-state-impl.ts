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
import {ignoreElements, takeUntil, tap} from 'rxjs/operators';
import {
  Action,
  Actions,
  State,
  StateContext,
  Store,
  ofAction,
} from '@ngxs/store';
import {ApiService} from 'src/app/core/api/api.service';
import {RefreshOrganizations} from 'src/app/pages/authority-organization-list-page/authority-organization-list-page/state/authority-organization-list-page-actions';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {ErrorService} from '../../../../core/services/error.service';
import {
  InviteNewOrganization,
  Reset,
} from './authority-invite-new-organization-page-actions';
import {
  AuthorityInviteNewOrganizationPageState,
  DEFAULT_AUTHORITY_INVITE_NEW_ORGANIZATION_PAGE_STATE,
} from './authority-invite-new-organization-page-state';

@State<AuthorityInviteNewOrganizationPageState>({
  name: 'AuthorityInviteNewOrganizationPageState',
  defaults: DEFAULT_AUTHORITY_INVITE_NEW_ORGANIZATION_PAGE_STATE,
})
@Injectable()
export class AuthorityInviteNewOrganizationPageStateImpl {
  constructor(
    private apiService: ApiService,
    private toast: ToastService,
    private actions$: Actions,
    private store: Store,
    private errorService: ErrorService,
  ) {}

  @Action(Reset)
  onReset(ctx: StateContext<AuthorityInviteNewOrganizationPageState>): void {
    ctx.setState(DEFAULT_AUTHORITY_INVITE_NEW_ORGANIZATION_PAGE_STATE);
  }

  @Action(InviteNewOrganization)
  onInviteNewOrganization(
    ctx: StateContext<AuthorityInviteNewOrganizationPageState>,
    action: InviteNewOrganization,
  ): Observable<never> {
    ctx.patchState({state: 'submitting'});
    action.disableForm();
    return this.apiService.inviteOrganization(action.request).pipe(
      tap(() => {
        this.toast.showSuccess(
          `The invitation for ${action.request.orgName} was sent.`,
        );
        ctx.patchState({state: 'success'});
        this.store.dispatch(RefreshOrganizations);
      }),
      takeUntil(this.actions$.pipe(ofAction(Reset))),
      this.errorService.toastRegistrationErrorRxjs(
        'Failed inviting organization due to an unknown error.',
        () => {
          ctx.patchState({state: 'error'});
          action.enableForm();
        },
      ),
      ignoreElements(),
    );
  }
}
