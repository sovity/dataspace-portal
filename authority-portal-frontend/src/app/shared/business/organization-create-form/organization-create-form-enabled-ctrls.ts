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
import {OrganizationLegalIdTypeDto} from '@sovity.de/authority-portal-client';
import {OrganizationCreateFormValue} from './organization-create-form-model';

export const organizationCreateFormEnabledCtrls = (
  value: OrganizationCreateFormValue,
): Record<keyof OrganizationCreateFormValue, boolean> => {
  const isCommercialRegister =
    value.legalIdType === OrganizationLegalIdTypeDto.CommerceRegisterInfo;
  const billingAddressEnabled = !value.billingAddressSameAsMain;
  const technicalContactEnabled = !value.technicalContactSameAsMain;

  return {
    legalName: true,
    website: true,
    businessUnit: true,
    industry: true,
    description: true,

    mainAddressStreet: true,
    mainAddressCity: true,
    mainAddressHouseNo: true,
    mainAddressZipCode: true,
    mainAddressCountry: true,

    billingAddressSameAsMain: true,
    billingAddressStreet: billingAddressEnabled,
    billingAddressCity: billingAddressEnabled,
    billingAddressHouseNo: billingAddressEnabled,
    billingAddressZipCode: billingAddressEnabled,
    billingAddressCountry: billingAddressEnabled,

    legalIdType: true,
    legalId: true,
    commerceRegisterLocation: isCommercialRegister,

    mainContactFirstName: true,
    mainContactLastName: true,
    mainContactPhoneNumber: true,
    mainContactEmail: true,
    technicalContactSameAsMain: true,
    technicalContactFirstName: technicalContactEnabled,
    technicalContactLastName: technicalContactEnabled,
    technicalContactPhoneNumber: technicalContactEnabled,
    technicalContactEmail: technicalContactEnabled,
  };
};
