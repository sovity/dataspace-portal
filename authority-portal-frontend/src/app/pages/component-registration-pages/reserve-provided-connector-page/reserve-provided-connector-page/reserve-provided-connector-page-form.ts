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
import {notBlankValidator} from 'src/app/core/utils/validators/not-blank-validator';
import {
  ConnectorInfoFormModel,
  DEFAULT_PROVIDE_CONNECTOR_PAGE_FORM_VALUE,
  ReserveProvidedConnectorPageFormModel,
  ReserveProvidedConnectorPageFormValue,
} from './reserve-provided-connector-page-form-model';

@Injectable()
export class ReserveProvidedConnectorPageForm {
  group = this.buildFormGroup();

  get connectorInfo(): FormGroup<ConnectorInfoFormModel> {
    return this.group.controls.connectorInfo;
  }

  get value(): ReserveProvidedConnectorPageFormValue {
    return this.group.value as ReserveProvidedConnectorPageFormValue;
  }

  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<ReserveProvidedConnectorPageFormModel> {
    const initial = DEFAULT_PROVIDE_CONNECTOR_PAGE_FORM_VALUE;

    const connectorInfo = this.formBuilder.nonNullable.group({
      name: [
        initial.connectorInfo.name,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      location: [
        initial.connectorInfo.location,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      organization: [
        initial.connectorInfo.organization,
        [Validators.required, Validators.maxLength(128)],
      ],
    });

    return this.formBuilder.nonNullable.group({
      connectorInfo: connectorInfo,
    });
  }
}
