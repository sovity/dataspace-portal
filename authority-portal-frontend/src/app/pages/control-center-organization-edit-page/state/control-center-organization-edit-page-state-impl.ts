/*
 * Copyright (c) 2024 sovity GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      sovity GmbH - initial implementation
 */
import {Injectable} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {Observable} from 'rxjs';
import {ignoreElements, switchMap, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {OwnOrganizationDetailsDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {CustomRxjsOperators} from 'src/app/core/services/custom-rxjs-operators';
import {Fetched} from 'src/app/core/utils/fetched';
import {GlobalStateUtils} from '../../../core/global-state/global-state-utils';
import {HeaderBarConfig} from '../../../shared/common/header-bar/header-bar.model';
import {ControlCenterOrganizationEditPageForm} from '../control-center-organization-edit-page/control-center-organization-edit-page.form';
import {
  buildControlCenterOrganizationEditPageFormValue,
  buildUpdateOwnOrganizationRequest,
} from '../control-center-organization-edit-page/control-center-organization-edit-page.form-mapper';
import {Reset, Submit} from './control-center-organization-edit-page-action';
import {
  ControlCenterOrganizationEditPageState,
  DEFAULT_CONTROL_CENTER_ORGANIZATION_EDIT_PAGE_STATE,
} from './control-center-organization-edit-page-state';

type Ctx = StateContext<ControlCenterOrganizationEditPageState>;

@State<ControlCenterOrganizationEditPageState>({
  name: 'ControlCenterOrganizationEditPageState',
  defaults: DEFAULT_CONTROL_CENTER_ORGANIZATION_EDIT_PAGE_STATE,
})
@Injectable()
export class ControlCenterOrganizationEditPageStateImpl {
  constructor(
    private apiService: ApiService,
    private formBuilder: FormBuilder,
    private customRxjsOperators: CustomRxjsOperators,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  @Action(Reset, {cancelUncompleted: true})
  onReset(ctx: Ctx, action: Reset): Observable<never> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((environmentId) =>
        this.apiService.getOwnOrganizationDetails(environmentId),
      ),
      Fetched.wrap({failureMessage: 'Failed to fetch user details'}),
      tap((organization) => {
        ctx.patchState({
          organization,
          headerBarConfig: organization
            .map((data) => this.buildHeaderBarConfig(data))
            .orElse(null),
        });
        action.setFormInComponent(
          organization.map((data) => this.rebuildForm(data)).orElse(null),
        );
      }),
      ignoreElements(),
    );
  }

  @Action(Submit)
  onSubmit(ctx: Ctx, action: Submit): Observable<never> {
    const request = buildUpdateOwnOrganizationRequest(action.formValue);
    return this.apiService
      .updateOwnOrganizationDetails(request)
      .pipe(
        this.customRxjsOperators.withBusyLock(ctx),
        this.customRxjsOperators.withToastResultHandling(
          'Editing own organization',
        ),
        this.customRxjsOperators.onSuccessRedirect([
          '/control-center/my-organization',
        ]),
        ignoreElements(),
      );
  }

  private buildHeaderBarConfig(
    organization: OwnOrganizationDetailsDto,
  ): HeaderBarConfig {
    return {
      title: organization.name,
      subtitle: 'Edit Your Organization Profile',
      headerActions: [],
    };
  }

  private rebuildForm(
    data: OwnOrganizationDetailsDto,
  ): ControlCenterOrganizationEditPageForm {
    const formValue = buildControlCenterOrganizationEditPageFormValue(data);
    return new ControlCenterOrganizationEditPageForm(
      this.formBuilder,
      formValue,
    );
  }
}
