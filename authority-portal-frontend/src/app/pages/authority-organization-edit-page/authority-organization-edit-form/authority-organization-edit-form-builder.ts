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
