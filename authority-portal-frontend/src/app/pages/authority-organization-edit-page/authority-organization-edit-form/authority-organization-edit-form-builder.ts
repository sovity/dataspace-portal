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
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {notBlankValidator} from 'src/app/core/utils/validators/not-blank-validator';
import {phoneNumberValidator} from 'src/app/core/utils/validators/phone-number-validator';
import {urlValidator} from 'src/app/core/utils/validators/url-validator';
import {
  AuthorityOrganizationEditFormModel,
  AuthorityOrganizationEditFormValue,
} from './authority-organization-edit-form-model';

export const buildAuthorityOrganizationEditForm = (
  formBuilder: FormBuilder,
  initialOrganization: AuthorityOrganizationEditFormValue,
): FormGroup<AuthorityOrganizationEditFormModel> => {
  return formBuilder.nonNullable.group({
    legalName: [
      initialOrganization.legalName,
      [Validators.required, Validators.maxLength(128), notBlankValidator()],
    ],
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
    legalIdType: [initialOrganization.legalIdType, [Validators.required]],
    legalId: [
      initialOrganization.legalId,
      [Validators.required, Validators.maxLength(128), notBlankValidator()],
    ],
    commerceRegisterLocation: [
      initialOrganization.commerceRegisterLocation,
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
