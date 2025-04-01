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
import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {switchDisabledControls} from 'src/app/core/utils/form-utils';
import {connectorUrlValidator} from 'src/app/core/utils/validators/connector-url-validator';
import {notBlankValidator} from 'src/app/core/utils/validators/not-blank-validator';
import {buildCertificateInputForm} from '../../../../shared/form-elements/certificate-input-form/certificate-input-form-builder';
import {certificateInputFormEnabledCtrls} from '../../../../shared/form-elements/certificate-input-form/certificate-input-form-enabled-ctrls';
import {
  CertificateTabFormModel,
  CertificateTabFormValue,
  ConnectorTabFormModel,
  DEFAULT_REGISTER_CONNECTOR_PAGE_FORM_VALUE,
  RegisterConnectorPageFormModel,
  RegisterConnectorPageFormValue,
} from './register-connector-page-form-model';

@Injectable()
export class RegisterConnectorPageForm {
  group = this.buildFormGroup();

  get connectorTab(): FormGroup<ConnectorTabFormModel> {
    return this.group.controls.connectorTab;
  }

  get certificateTab(): FormGroup<CertificateTabFormModel> {
    return this.group.controls.certificateTab;
  }

  get value(): RegisterConnectorPageFormValue {
    return this.group.value as RegisterConnectorPageFormValue;
  }

  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<RegisterConnectorPageFormModel> {
    const initial = DEFAULT_REGISTER_CONNECTOR_PAGE_FORM_VALUE;

    const connectorTab = this.formBuilder.nonNullable.group({
      name: [
        initial.connectorTab.name,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      location: [
        initial.connectorTab.location,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      frontendUrl: [
        initial.connectorTab.frontendUrl,
        [
          Validators.required,
          Validators.maxLength(128),
          notBlankValidator(),
          connectorUrlValidator,
        ],
      ],
      endpointUrl: [
        initial.connectorTab.endpointUrl,
        [
          Validators.required,
          Validators.maxLength(128),
          notBlankValidator(),
          connectorUrlValidator,
        ],
      ],
      managementUrl: [
        initial.connectorTab.managementUrl,
        [
          Validators.required,
          Validators.maxLength(128),
          notBlankValidator(),
          connectorUrlValidator,
        ],
      ],
    });

    const certificateTab = buildCertificateInputForm(
      this.formBuilder,
      initial.certificateTab,
    );

    switchDisabledControls<CertificateTabFormValue>(certificateTab, (value) =>
      certificateInputFormEnabledCtrls(value),
    );

    return this.formBuilder.nonNullable.group({
      connectorTab,
      certificateTab,
    });
  }
}
