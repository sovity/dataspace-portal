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
import {organizationEditFormEnabledCtrls} from '../../../shared/business/organization-edit-form/organization-edit-form-enabled-ctrls';
import {AuthorityOrganizationEditFormValue} from './authority-organization-edit-form-model';

export const authorityOrganizationEditFormEnabledCtrls = (
  value: AuthorityOrganizationEditFormValue,
): Record<keyof AuthorityOrganizationEditFormValue, boolean> => {
  return {
    legalName: true,
    legalIdType: true,
    legalId: true,
    commerceRegisterLocation: true,
    ...organizationEditFormEnabledCtrls(value),
  };
};
