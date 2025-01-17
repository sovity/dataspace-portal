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
import {FormControl, ɵFormGroupRawValue} from '@angular/forms';
import {OrganizationLegalIdTypeDto} from '@sovity.de/authority-portal-client';
import {OrganizationEditFormModel} from '../../../shared/business/organization-edit-form/organization-edit-form-model';

export interface AuthorityOrganizationEditFormModel
  extends OrganizationEditFormModel {
  legalName: FormControl<string>;
  legalIdType: FormControl<OrganizationLegalIdTypeDto>;
  legalId: FormControl<string>;
  commerceRegisterLocation: FormControl<string>;
}

export type AuthorityOrganizationEditFormValue =
  ɵFormGroupRawValue<AuthorityOrganizationEditFormModel>;
