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
import {FormControl, ɵFormGroupRawValue} from '@angular/forms';
import {OrganizationLegalIdTypeDto} from '@sovity.de/authority-portal-client';

export interface OrganizationCreateFormModel {
  legalName: FormControl<string>;
  website: FormControl<string>;
  businessUnit: FormControl<string>;
  industry: FormControl<string>;
  description: FormControl<string>;

  mainAddressStreet: FormControl<string>;
  mainAddressCity: FormControl<string>;
  mainAddressHouseNo: FormControl<string>;
  mainAddressZipCode: FormControl<string>;
  mainAddressCountry: FormControl<string>;

  billingAddressSameAsMain: FormControl<boolean>;
  billingAddressStreet: FormControl<string>;
  billingAddressCity: FormControl<string>;
  billingAddressHouseNo: FormControl<string>;
  billingAddressZipCode: FormControl<string>;
  billingAddressCountry: FormControl<string>;

  mainContactFirstName: FormControl<string>;
  mainContactLastName: FormControl<string>;
  mainContactPhoneNumber: FormControl<string>;
  mainContactEmail: FormControl<string>;

  technicalContactSameAsMain: FormControl<boolean>;
  technicalContactFirstName: FormControl<string>;
  technicalContactLastName: FormControl<string>;
  technicalContactPhoneNumber: FormControl<string>;
  technicalContactEmail: FormControl<string>;

  legalIdType: FormControl<OrganizationLegalIdTypeDto>;
  legalId: FormControl<string>;
  commerceRegisterLocation: FormControl<string>;
}

export type OrganizationCreateFormValue =
  ɵFormGroupRawValue<OrganizationCreateFormModel>;

export const DEFAULT_ORGANIZATION_CREATE_FORM_MODEL: OrganizationCreateFormValue =
  {
    legalName: '',
    website: '',
    businessUnit: '',
    industry: '',
    description: '',

    mainAddressStreet: '',
    mainAddressCity: '',
    mainAddressHouseNo: '',
    mainAddressZipCode: '',
    mainAddressCountry: '',

    billingAddressSameAsMain: true,
    billingAddressStreet: '',
    billingAddressCity: '',
    billingAddressHouseNo: '',
    billingAddressZipCode: '',
    billingAddressCountry: '',

    legalIdType: OrganizationLegalIdTypeDto.TaxId,
    legalId: '',
    commerceRegisterLocation: '',

    mainContactFirstName: '',
    mainContactLastName: '',
    mainContactPhoneNumber: '',
    mainContactEmail: '',
    technicalContactSameAsMain: true,
    technicalContactFirstName: '',
    technicalContactLastName: '',
    technicalContactPhoneNumber: '',
    technicalContactEmail: '',
  };
