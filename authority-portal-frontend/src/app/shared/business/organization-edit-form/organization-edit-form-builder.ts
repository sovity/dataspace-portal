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
import {phoneNumberValidator} from 'src/app/core/utils/validators/phone-number-validator';
import {urlValidator} from 'src/app/core/utils/validators/url-validator';
import {
  OrganizationEditFormModel,
  OrganizationEditFormValue,
} from './organization-edit-form-model';

export const buildOrganizationEditForm = (
  formBuilder: FormBuilder,
  initialOrganization: OrganizationEditFormValue,
): FormGroup<OrganizationEditFormModel> => {
  return formBuilder.nonNullable.group({
    website: [
      initialOrganization.website,
      [Validators.required, Validators.maxLength(128), urlValidator],
    ],
    businessUnit: [
      initialOrganization.businessUnit,
      [Validators.required, Validators.maxLength(128), notBlankValidator()],
    ],
    industry: [initialOrganization.industry, [Validators.required]],
    description: [
      initialOrganization.description,
      [Validators.required, Validators.maxLength(4096), notBlankValidator()],
    ],
    mainAddress: [
      initialOrganization.mainAddress,
      [Validators.required, Validators.maxLength(128), notBlankValidator()],
    ],
    billingAddressSameAsMain: [
      initialOrganization.billingAddressSameAsMain,
      [Validators.required],
    ],
    billingAddress: [
      initialOrganization.billingAddress,
      [Validators.required, Validators.maxLength(128), notBlankValidator()],
    ],
    mainContactName: [
      initialOrganization.mainContactName,
      [Validators.required, Validators.maxLength(128), notBlankValidator()],
    ],
    mainContactPhoneNumber: [
      initialOrganization.mainContactPhoneNumber,
      [
        Validators.required,
        phoneNumberValidator,
        Validators.minLength(5),
        Validators.maxLength(28),
        notBlankValidator(),
      ],
    ],
    mainContactEmail: [
      initialOrganization.mainContactEmail,
      [Validators.required, Validators.maxLength(128), Validators.email],
    ],
    technicalContactSameAsMain: [
      initialOrganization.technicalContactSameAsMain,
      [Validators.required],
    ],
    technicalContactName: [
      initialOrganization.technicalContactName,
      [Validators.required, Validators.maxLength(128), notBlankValidator()],
    ],
    technicalContactPhoneNumber: [
      initialOrganization.technicalContactPhoneNumber,
      [
        Validators.required,
        phoneNumberValidator,
        Validators.minLength(5),
        Validators.maxLength(28),
        notBlankValidator(),
      ],
    ],
    technicalContactEmail: [
      initialOrganization.technicalContactEmail,
      [Validators.required, Validators.maxLength(128), Validators.email],
    ],
  });
};
