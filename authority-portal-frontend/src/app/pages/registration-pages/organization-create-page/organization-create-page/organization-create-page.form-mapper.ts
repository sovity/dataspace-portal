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
import {RegistrationRequestDto} from '@sovity.de/authority-portal-client';
import {getMainAndBillingAddresses} from 'src/app/core/utils/address-utils';
import {getMainAndTechnicalContacts} from 'src/app/core/utils/name-utils';
import {RegistrationWizardFormValue} from './organization-create-page.form-model';

export function buildRegistrationRequest(
  formValue: RegistrationWizardFormValue,
): RegistrationRequestDto {
  let user = formValue.userTab;
  let org = formValue.organizationTab;

  const {mainAddress, billingAddress} = getMainAndBillingAddresses(org);

  const {
    mainContactName,
    mainContactEmail,
    mainContactPhone,
    technicalContactName,
    technicalContactEmail,
    technicalContactPhone,
  } = getMainAndTechnicalContacts(org);

  return {
    // User Profile
    userEmail: user?.email || '',
    userFirstName: user?.firstName || '',
    userLastName: user?.lastName || '',
    userJobTitle: user?.jobTitle || '',
    userPhone: user?.phoneNumber || '',
    userPassword: user?.password || '',

    // Organization
    organizationName: org?.legalName || '',

    // Organization Metadata
    organizationUrl: org?.website || '',
    organizationDescription: org?.description || '',
    organizationBusinessUnit: org?.businessUnit || '',
    organizationIndustry: org?.industry || '',
    organizationAddress: mainAddress,
    organizationBillingAddress: billingAddress,
    organizationLegalIdType: org?.legalIdType!,
    organizationLegalIdNumber: org?.legalId || '',
    organizationCommerceRegisterLocation: org?.commerceRegisterLocation || '',

    // Organization Contacts
    organizationMainContactName: mainContactName,
    organizationMainContactEmail: mainContactEmail,
    organizationMainContactPhone: mainContactPhone,
    organizationTechContactName: technicalContactName,
    organizationTechContactEmail: technicalContactEmail,
    organizationTechContactPhone: technicalContactPhone,
  };
}
