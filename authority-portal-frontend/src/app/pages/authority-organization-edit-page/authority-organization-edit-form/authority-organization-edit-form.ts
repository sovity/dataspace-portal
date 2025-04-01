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
import {FormBuilder, FormGroup} from '@angular/forms';
import {switchDisabledControls} from 'src/app/core/utils/form-utils';
import {buildAuthorityOrganizationEditForm} from './authority-organization-edit-form-builder';
import {authorityOrganizationEditFormEnabledCtrls} from './authority-organization-edit-form-enabled-ctrls';
import {
  AuthorityOrganizationEditFormModel,
  AuthorityOrganizationEditFormValue,
} from './authority-organization-edit-form-model';

export class AuthorityOrganizationEditForm {
  group = this.buildFormGroup();

  get value(): AuthorityOrganizationEditFormValue {
    return this.group.value as AuthorityOrganizationEditFormValue;
  }

  constructor(
    private formBuilder: FormBuilder,
    private initialFormValue: AuthorityOrganizationEditFormValue,
  ) {}

  buildFormGroup(): FormGroup<AuthorityOrganizationEditFormModel> {
    const group: FormGroup<AuthorityOrganizationEditFormModel> =
      buildAuthorityOrganizationEditForm(
        this.formBuilder,
        this.initialFormValue,
      );

    switchDisabledControls<AuthorityOrganizationEditFormValue>(
      group,
      (value: AuthorityOrganizationEditFormValue) =>
        authorityOrganizationEditFormEnabledCtrls(value),
    );

    return group;
  }
}
