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
import {EMPTY, NEVER, Observable} from 'rxjs';
import {ignoreElements, switchMap, tap} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {OrganizationDetailsDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {CustomRxjsOperators} from 'src/app/core/services/custom-rxjs-operators';
import {Fetched} from 'src/app/core/utils/fetched';
import {GlobalStateUtils} from '../../../core/global-state/global-state-utils';
import {HeaderBarConfig} from '../../../shared/common/header-bar/header-bar.model';
import {AuthorityOrganizationEditForm} from '../authority-organization-edit-form/authority-organization-edit-form';
import {
  buildEditRequest,
  buildFormValue,
} from '../authority-organization-edit-form/authority-organization-edit-form-mapper';
import {
  Reset,
  SetOrganizationId,
  Submit,
} from './authority-organization-edit-page-action';
import {
  AuthorityOrganizationEditPageState,
  DEFAULT_AUTHORITY_ORGANIZATION_EDIT_PAGE_STATE,
} from './authority-organization-edit-page-state';

type Ctx = StateContext<AuthorityOrganizationEditPageState>;

@State<AuthorityOrganizationEditPageState>({
  name: 'AuthorityOrganizationEditPageState',
  defaults: DEFAULT_AUTHORITY_ORGANIZATION_EDIT_PAGE_STATE,
})
@Injectable()
export class AuthorityOrganizationEditPageStateImpl {
  constructor(
    private apiService: ApiService,
    private formBuilder: FormBuilder,
    private customRxjsOperators: CustomRxjsOperators,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  @Action(SetOrganizationId)
  onSetOrganizationId(ctx: Ctx, action: SetOrganizationId): Observable<never> {
    ctx.patchState({
      organizationId: action.organizationId,
    });
    return EMPTY;
  }

  @Action(Reset, {cancelUncompleted: true})
  onReset(ctx: Ctx, action: Reset): Observable<never> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((environmentId) =>
        this.apiService.getOrganizationDetailsForAuthority(
          ctx.getState().organizationId,
          environmentId,
        ),
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
    const request = buildEditRequest(action.formValue);

    /* TODO: implement update organization
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
     */

    return NEVER;
  }

  private buildHeaderBarConfig(
    organization: OrganizationDetailsDto,
  ): HeaderBarConfig {
    return {
      title: organization.name,
      subtitle: "Edit the Organization's Profile",
      headerActions: [],
    };
  }

  private rebuildForm(
    data: OrganizationDetailsDto,
  ): AuthorityOrganizationEditForm {
    const formValue = buildFormValue(data);
    return new AuthorityOrganizationEditForm(this.formBuilder, formValue);
  }
}
