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
import {buildOrganizationEditForm} from '../../../shared/business/organization-edit-form/organization-edit-form-builder';
import {organizationEditFormEnabledCtrls} from '../../../shared/business/organization-edit-form/organization-edit-form-enabled-ctrls';
import {
  ControlCenterOrganizationEditPageFormModel,
  ControlCenterOrganizationEditPageFormValue,
} from './control-center-organization-edit-page.form-model';

export class ControlCenterOrganizationEditPageForm {
  group = this.buildFormGroup();

  get value(): ControlCenterOrganizationEditPageFormValue {
    return this.group.value as ControlCenterOrganizationEditPageFormValue;
  }

  constructor(
    private formBuilder: FormBuilder,
    private initialFormValue: ControlCenterOrganizationEditPageFormValue,
  ) {}

  buildFormGroup(): FormGroup<ControlCenterOrganizationEditPageFormModel> {
    const group: FormGroup<ControlCenterOrganizationEditPageFormModel> =
      buildOrganizationEditForm(this.formBuilder, this.initialFormValue);

    switchDisabledControls<ControlCenterOrganizationEditPageFormValue>(
      group,
      (value: ControlCenterOrganizationEditPageFormValue) =>
        organizationEditFormEnabledCtrls(value),
    );

    return group;
  }
}
