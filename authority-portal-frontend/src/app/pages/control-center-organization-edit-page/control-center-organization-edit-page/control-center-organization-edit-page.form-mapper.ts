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
  OwnOrganizationDetailsDto,
  UpdateOwnOrganizationDto,
} from '@sovity.de/authority-portal-client';
import {ControlCenterOrganizationEditPageFormValue} from './control-center-organization-edit-page.form-model';

export function buildControlCenterOrganizationEditPageFormValue(
  organization: OwnOrganizationDetailsDto,
): ControlCenterOrganizationEditPageFormValue {
  let billingAddressSameAsMain =
    organization.mainAddress === organization.billingAddress;
  let technicalContactSameAsMain =
    organization.mainContactName === organization.techContactName &&
    organization.mainContactPhone === organization.techContactPhone &&
    organization.mainContactEmail === organization.techContactEmail;

  return {
    website: organization.url ?? '',
    businessUnit: organization.businessUnit ?? '',
    industry: organization.industry ?? '',
    description: organization.description ?? '',

    mainAddress: organization.mainAddress ?? '',
    billingAddressSameAsMain,
    billingAddress: billingAddressSameAsMain
      ? ''
      : organization.billingAddress ?? '',

    mainContactName: organization.mainContactName ?? '',
    mainContactPhoneNumber: organization.mainContactPhone ?? '',
    mainContactEmail: organization.mainContactEmail ?? '',
    technicalContactSameAsMain,
    technicalContactName: technicalContactSameAsMain
      ? ''
      : organization.techContactName ?? '',
    technicalContactPhoneNumber: technicalContactSameAsMain
      ? ''
      : organization.techContactPhone ?? '',
    technicalContactEmail: technicalContactSameAsMain
      ? ''
      : organization.techContactEmail ?? '',
  };
}

export function buildUpdateOwnOrganizationRequest(
  formValue: ControlCenterOrganizationEditPageFormValue,
): UpdateOwnOrganizationDto {
  return {
    url: formValue.website,
    businessUnit: formValue.businessUnit,
    industry: formValue.industry,
    description: formValue.description,

    address: formValue.mainAddress,
    billingAddress: formValue.billingAddressSameAsMain
      ? formValue.mainAddress
      : formValue.billingAddress,

    mainContactName: formValue.mainContactName,
    mainContactPhone: formValue.mainContactPhoneNumber,
    mainContactEmail: formValue.mainContactEmail,
    techContactName: formValue.technicalContactSameAsMain
      ? formValue.mainContactName
      : formValue.technicalContactName,
    techContactPhone: formValue.technicalContactSameAsMain
      ? formValue.mainContactPhoneNumber
      : formValue.technicalContactPhoneNumber,
    techContactEmail: formValue.technicalContactSameAsMain
      ? formValue.mainContactEmail
      : formValue.technicalContactEmail,
  };
}
