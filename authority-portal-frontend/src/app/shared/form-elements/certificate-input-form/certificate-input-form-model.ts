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

export interface CertificateFormModel {
  bringOwnCert: FormControl<boolean>;
  ownCertificate: FormControl<string>;

  organizationalUnit: FormControl<string>;
  email: FormControl<string>;
  state: FormControl<string>;
  city: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
  generatedCertificate: FormControl<string>;
}

export type CertificateFormValue = ɵFormGroupRawValue<CertificateFormModel>;

export const DEFAULT_CERTIFICATE_FORM_VALUE: CertificateFormValue = {
  bringOwnCert: false,
  ownCertificate: '',
  organizationalUnit: '',
  email: '',
  state: '',
  city: '',
  password: '',
  confirmPassword: '',
  generatedCertificate: '',
};
