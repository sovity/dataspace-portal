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
import {
  OrganizationDetailsDto,
  UpdateOrganizationDto,
} from '@sovity.de/authority-portal-client';
import {
  buildControlCenterOrganizationEditPageFormValue,
  buildUpdateOwnOrganizationRequest,
} from '../../control-center-organization-edit-page/control-center-organization-edit-page/control-center-organization-edit-page.form-mapper';
import {AuthorityOrganizationEditFormValue} from './authority-organization-edit-form-model';

export function buildAuthorityOrganizationEditFormValue(
  organization: OrganizationDetailsDto,
): AuthorityOrganizationEditFormValue {
  return {
    legalName: organization.name,
    legalIdType: organization.legalIdType ?? 'TAX_ID',
    legalId: organization.legalId ?? '',
    commerceRegisterLocation: organization.commerceRegisterLocation ?? '',
    ...buildControlCenterOrganizationEditPageFormValue(organization),
  };
}

export function buildUpdateOrganizationRequest(
  formValue: AuthorityOrganizationEditFormValue,
): UpdateOrganizationDto {
  return {
    name: formValue.legalName,
    legalIdType: formValue.legalIdType,
    legalIdNumber: formValue.legalId,
    commerceRegisterLocation: formValue.commerceRegisterLocation,
    ...buildUpdateOwnOrganizationRequest(formValue),
  };
}
