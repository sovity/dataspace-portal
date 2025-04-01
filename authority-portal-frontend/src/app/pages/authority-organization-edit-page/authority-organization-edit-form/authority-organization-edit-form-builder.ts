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
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {notBlankValidator} from 'src/app/core/utils/validators/not-blank-validator';
import {mergeFormGroups} from '../../../core/utils/form-utils';
import {buildOrganizationEditForm} from '../../../shared/business/organization-edit-form/organization-edit-form-builder';
import {
  AuthorityOrganizationEditFormModel,
  AuthorityOrganizationEditFormValue,
} from './authority-organization-edit-form-model';

export const buildAuthorityOrganizationEditForm = (
  formBuilder: FormBuilder,
  initialOrganization: AuthorityOrganizationEditFormValue,
): FormGroup<AuthorityOrganizationEditFormModel> => {
  return mergeFormGroups(
    buildOrganizationEditForm(formBuilder, initialOrganization),
    formBuilder.nonNullable.group({
      legalName: [
        initialOrganization.legalName,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      legalIdType: [initialOrganization.legalIdType, [Validators.required]],
      legalId: [
        initialOrganization.legalId,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      commerceRegisterLocation: [
        initialOrganization.commerceRegisterLocation,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
    }),
  );
};
