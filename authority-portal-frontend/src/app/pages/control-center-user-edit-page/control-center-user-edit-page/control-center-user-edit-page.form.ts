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
import {buildUserEditForm} from '../../../shared/business/user-edit-form/user-edit-form-builder';
import {
  ControlCenterUserEditPageFormModel,
  ControlCenterUserEditPageFormValue,
} from './control-center-user-edit-page.form-model';

export class ControlCenterUserEditPageForm {
  group = this.buildFormGroup();

  get value(): ControlCenterUserEditPageFormValue {
    return this.group.value as ControlCenterUserEditPageFormValue;
  }

  constructor(
    private formBuilder: FormBuilder,
    private initialFormValue: ControlCenterUserEditPageFormValue,
  ) {}

  buildFormGroup(): FormGroup<ControlCenterUserEditPageFormModel> {
    return buildUserEditForm(this.formBuilder, this.initialFormValue);
  }
}
