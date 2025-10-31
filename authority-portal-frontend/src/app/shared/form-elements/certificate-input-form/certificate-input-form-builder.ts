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
import {
  CertificateFormModel,
  CertificateFormValue,
} from './certificate-input-form-model';

export const buildCertificateInputForm = (
  formBuilder: FormBuilder,
  initial: CertificateFormValue,
): FormGroup<CertificateFormModel> =>
  formBuilder.nonNullable.group({
    bringOwnCert: [initial.bringOwnCert],
    ownCertificate: [
      initial.ownCertificate,
      [Validators.required, certificateValidator],
    ],
    generatedCertificate: [initial.generatedCertificate, [Validators.required]],
    generatedPrivateKey: [initial.generatedPrivateKey, [Validators.required]],
  });
