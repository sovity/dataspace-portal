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
import {FormBuilder} from '@angular/forms';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {ignoreElements, switchMap, take, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {UserDetailDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {CustomRxjsOperators} from 'src/app/core/services/custom-rxjs-operators';
import {Fetched} from 'src/app/core/utils/fetched';
import {APP_CONFIG, AppConfig} from '../../../core/services/config/app-config';
import {HeaderBarConfig} from '../../../shared/common/header-bar/header-bar.model';
import {ControlCenterUserEditPageForm} from '../control-center-user-edit-page/control-center-user-edit-page.form';
import {
  buildEditRequest,
  buildFormValue,
} from '../control-center-user-edit-page/control-center-user-edit-page.form-mapper';
import {Reset, Submit} from './control-center-user-edit-page-action';
import {
  ControlCenterUserEditPageState,
  DEFAULT_CONTROL_CENTER_USER_EDIT_PAGE_STATE,
} from './control-center-user-edit-page-state';

type Ctx = StateContext<ControlCenterUserEditPageState>;

@State<ControlCenterUserEditPageState>({
  name: 'ControlCenterUserEditPageState',
  defaults: DEFAULT_CONTROL_CENTER_USER_EDIT_PAGE_STATE,
})
@Injectable()
export class ControlCenterUserEditPageStateImpl {
  constructor(
    private apiService: ApiService,
    private formBuilder: FormBuilder,
    private customRxjsOperators: CustomRxjsOperators,
    private globalStateUtils: GlobalStateUtils,
    private router: Router,
    @Inject(APP_CONFIG) private appConfig: AppConfig,
  ) {}

  @Action(Reset)
  onReset(ctx: Ctx, action: Reset): Observable<never> {
    return this.globalStateUtils.userInfo$.pipe(
      take(1),
      switchMap((userInfo) =>
        this.apiService.getUserDetailDto(userInfo.userId),
      ),
      Fetched.wrap({failureMessage: 'Failed to fetch user details'}),
      tap((user) => {
        ctx.patchState({
          user,
          headerBarConfig: user
            .map((userDetails) => this.buildHeaderBarConfig(userDetails))
            .orElse(null),
        });
        action.setFormInComponent(
          user.map((data) => this.rebuildForm(data)).orElse(null),
        );
      }),
      ignoreElements(),
    );
  }

  @Action(Submit)
  onSubmit(ctx: Ctx, action: Submit): Observable<never> {
    const request = buildEditRequest(action.formValue);
    return this.apiService
      .updateUser(ctx.getState().user.data.userId, request)
      .pipe(
        this.customRxjsOperators.withBusyLock(ctx),
        this.customRxjsOperators.withToastResultHandling('Editing profile'),
        this.customRxjsOperators.onSuccessRedirect([
          '/control-center/my-profile',
        ]),
        ignoreElements(),
      );
  }

  private buildHeaderBarConfig(user: UserDetailDto): HeaderBarConfig {
    return {
      title: `${user.firstName} ${user.lastName}`,
      subtitle: 'Edit Your Profile Information',
      headerActions: [
        {
          label: 'Change Password',
          action: () => {
            this.router.navigate(
              [
                '/externalRedirect',
                {externalUrl: this.appConfig.updatePasswordUrl},
              ],
              {
                skipLocationChange: true,
              },
            );
          },
          permissions: ['USER'],
        },
      ],
    };
  }
  private rebuildForm(data: UserDetailDto): ControlCenterUserEditPageForm {
    const formValue = buildFormValue(data);
    return new ControlCenterUserEditPageForm(this.formBuilder, formValue);
  }
}
