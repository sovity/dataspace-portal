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
