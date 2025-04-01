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
import {Observable, combineLatest, forkJoin} from 'rxjs';
import {
  ignoreElements,
  map,
  switchMap,
  take,
  takeUntil,
  tap,
} from 'rxjs/operators';
import {Action, Actions, State, StateContext, ofAction} from '@ngxs/store';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {ErrorService} from 'src/app/core/services/error.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {
  OnboardingProcessFormSubmit,
  Reset,
} from './organization-onboard-page-action';
import {
  DEFAULT_ORGANIZATION_ONBOARD_PAGE_PAGE_STATE,
  OrganizationOnboardPageState,
} from './organization-onboard-page-state';

@State<OrganizationOnboardPageState>({
  name: 'OrganizationOnboardPageState',
  defaults: DEFAULT_ORGANIZATION_ONBOARD_PAGE_PAGE_STATE,
})
@Injectable()
export class OrganizationOnboardPageStateImpl {
  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private apiService: ApiService,
    private errorService: ErrorService,
    private actions$: Actions,
    private toast: ToastService,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  @Action(Reset, {cancelUncompleted: true})
  onReset(ctx: StateContext<OrganizationOnboardPageState>): Observable<never> {
    return this.globalStateUtils.userInfo$.pipe(
      take(1),
      switchMap((userInfo) =>
        combineLatest([
          this.apiService.getOrganizationUser(userInfo.userId),
          this.globalStateUtils
            .getDeploymentEnvironmentId()
            .pipe(
              switchMap((environmentId) =>
                this.apiService.getOwnOrganizationDetails(environmentId),
              ),
            ),
        ]),
      ),
      map(([user, organization]) => ({user, organization})),
      Fetched.wrap({failureMessage: 'Failed loading onboarding details'}),
      tap((details) => {
        ctx.patchState({
          ...ctx.getState(),
          details,
          onboardingType:
            details.dataOrUndefined?.organization?.registrationStatus ===
            'ONBOARDING'
              ? 'USER_ORGANIZATION_ONBOARDING'
              : 'USER_ONBOARDING',
        });
      }),
      ignoreElements(),
    );
  }

  @Action(OnboardingProcessFormSubmit, {cancelUncompleted: true})
  onSubmit(
    ctx: StateContext<OrganizationOnboardPageState>,
    action: OnboardingProcessFormSubmit,
  ): Observable<never> {
    ctx.patchState({state: 'submitting'});
    action.disableForm();

    const requests: Observable<unknown>[] = [
      this.apiService.onboardingUser(action.request.userProfile),
    ];

    if (ctx.getState().onboardingType === 'USER_ORGANIZATION_ONBOARDING') {
      requests.push(
        this.apiService.onboardingOrganization(
          action.request.organizationProfile,
        ),
      );
    }

    return forkJoin(requests).pipe(
      tap(() => {
        this.toast.showSuccess(`Onboarding completed successfully`);
        ctx.patchState({state: 'success'});
        action.success();
      }),
      takeUntil(this.actions$.pipe(ofAction(Reset))),
      this.errorService.toastFailureRxjs('Onboarding Failed', () => {
        ctx.patchState({state: 'error'});
        action.enableForm();
      }),
      ignoreElements(),
    );
  }
}
