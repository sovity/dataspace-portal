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
import {FormBuilder, FormGroup} from '@angular/forms';
import {switchDisabledControls} from 'src/app/core/utils/form-utils';
import {buildOrganizationEditForm} from '../../../shared/business/organization-edit-form/organization-edit-form-builder';
import {organizationEditFormEnabledCtrls} from '../../../shared/business/organization-edit-form/organization-edit-form-enabled-ctrls';
import {
  AuthorityOrganizationEditPageFormModel,
  AuthorityOrganizationEditPageFormValue,
} from './authority-organization-edit-page.form-model';

export class AuthorityOrganizationEditPageForm {
  group = this.buildFormGroup();

  get value(): AuthorityOrganizationEditPageFormValue {
    return this.group.value as AuthorityOrganizationEditPageFormValue;
  }

  constructor(
    private formBuilder: FormBuilder,
    private initialFormValue: AuthorityOrganizationEditPageFormValue,
  ) {}

  buildFormGroup(): FormGroup<AuthorityOrganizationEditPageFormModel> {
    const group: FormGroup<AuthorityOrganizationEditPageFormModel> =
      buildOrganizationEditForm(this.formBuilder, this.initialFormValue);

    switchDisabledControls<AuthorityOrganizationEditPageFormValue>(
      group,
      (value: AuthorityOrganizationEditPageFormValue) =>
        organizationEditFormEnabledCtrls(value),
    );

    return group;
  }
}
