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

export function buildAddressString(formValue: {
  street?: string;
  houseNo?: string;
  zipCode?: string;
  city?: string;
  country?: string;
}): string {
  return `${formValue.street} ${formValue.houseNo}, ${formValue.zipCode} ${formValue.city}, ${formValue.country}`;
}

export function getMainAndBillingAddresses(formValue?: {
  mainAddressStreet: string;
  mainAddressCity: string;
  mainAddressHouseNo: string;
  mainAddressZipCode: string;
  mainAddressCountry: string;

  billingAddressSameAsMain: boolean;
  billingAddressStreet: string;
  billingAddressCity: string;
  billingAddressHouseNo: string;
  billingAddressZipCode: string;
  billingAddressCountry: string;
}): {mainAddress: string; billingAddress: string} {
  let mainAddress = buildAddressString({
    street: formValue?.mainAddressStreet,
    houseNo: formValue?.mainAddressHouseNo,
    zipCode: formValue?.mainAddressZipCode,
    city: formValue?.mainAddressCity,
    country: formValue?.mainAddressCountry,
  });

  let billingAddress = formValue?.billingAddressSameAsMain
    ? mainAddress
    : buildAddressString({
        street: formValue?.billingAddressStreet,
        houseNo: formValue?.billingAddressHouseNo,
        zipCode: formValue?.billingAddressZipCode,
        city: formValue?.billingAddressCity,
        country: formValue?.billingAddressCountry,
      });

  return {mainAddress, billingAddress};
}
