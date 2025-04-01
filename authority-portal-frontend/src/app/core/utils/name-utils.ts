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

export const buildFullName = (...arr: (string | null | undefined)[]) =>
  arr
    .map((it) => it?.trim())
    .filter((it) => !!it)
    .join(' ');

export function getMainAndTechnicalContacts(formValue: {
  mainContactFirstName: string;
  mainContactLastName: string;
  mainContactEmail: string;
  mainContactPhoneNumber: string;
  technicalContactSameAsMain: boolean;
  technicalContactFirstName: string;
  technicalContactLastName: string;
  technicalContactEmail: string;
  technicalContactPhoneNumber: string;
}): {
  mainContactName: string;
  mainContactEmail: string;
  mainContactPhone: string;
  technicalContactName: string;
  technicalContactEmail: string;
  technicalContactPhone: string;
} {
  let mainContactName = buildFullName(
    formValue.mainContactFirstName,
    formValue.mainContactLastName,
  );
  let mainContactEmail = formValue.mainContactEmail || '';
  let mainContactPhone = formValue.mainContactPhoneNumber || '';

  let technicalContactName = formValue.technicalContactSameAsMain
    ? mainContactName
    : buildFullName(
        formValue.technicalContactFirstName,
        formValue.technicalContactLastName,
      );
  let technicalContactEmail = formValue.technicalContactSameAsMain
    ? mainContactEmail
    : formValue.technicalContactEmail || '';
  let technicalContactPhone = formValue.technicalContactSameAsMain
    ? mainContactPhone
    : formValue.technicalContactPhoneNumber || '';

  return {
    mainContactName,
    mainContactEmail,
    mainContactPhone,
    technicalContactName,
    technicalContactEmail,
    technicalContactPhone,
  };
}
