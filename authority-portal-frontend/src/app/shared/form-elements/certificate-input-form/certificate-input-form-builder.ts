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
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {certificateValidator} from 'src/app/core/utils/validators/certificate-validator';
import {notBlankValidator} from 'src/app/core/utils/validators/not-blank-validator';
import {passwordEntropyValidator} from 'src/app/core/utils/validators/password-entropy-validator';
import {passwordMatchValidator} from 'src/app/core/utils/validators/password-match-validator';
import {
  CertificateFormModel,
  CertificateFormValue,
} from './certificate-input-form-model';

export const buildCertificateInputForm = (
  formBuilder: FormBuilder,
  initial: CertificateFormValue,
): FormGroup<CertificateFormModel> =>
  formBuilder.nonNullable.group(
    {
      bringOwnCert: [initial.bringOwnCert],
      ownCertificate: [
        initial.ownCertificate,
        [Validators.required, certificateValidator],
      ],
      organizationalUnit: [
        initial.organizationalUnit,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      email: [
        initial.email,
        [Validators.required, Validators.email, Validators.maxLength(128)],
      ],
      state: [
        initial.state,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      city: [
        initial.city,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      password: [
        initial.password,
        [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(128),
          passwordEntropyValidator,
          notBlankValidator(),
        ],
      ],
      confirmPassword: [initial.confirmPassword, [Validators.required]],
      generatedCertificate: [
        initial.generatedCertificate,
        [Validators.required],
      ],
    },
    {validators: passwordMatchValidator('password', 'confirmPassword')},
  );
