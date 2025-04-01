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
import {FormBuilder} from '@angular/forms';
import {EMPTY, Observable} from 'rxjs';
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
  buildAuthorityOrganizationEditFormValue,
  buildUpdateOrganizationRequest,
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
    const request = buildUpdateOrganizationRequest(action.formValue);
    const organizationId = ctx.getState().organizationId;

    return this.apiService
      .updateOrganizationDetails(organizationId, request)
      .pipe(
        this.customRxjsOperators.withBusyLock(ctx),
        this.customRxjsOperators.withToastResultHandling(
          'Editing organization',
        ),
        this.customRxjsOperators.onSuccessRedirect([
          '/authority/organizations',
          organizationId,
        ]),
        ignoreElements(),
      );
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
    const formValue = buildAuthorityOrganizationEditFormValue(data);
    return new AuthorityOrganizationEditForm(this.formBuilder, formValue);
  }
}
