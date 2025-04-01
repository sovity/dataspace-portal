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
import {subdomainValidators} from 'src/app/core/utils/validators/subdomain-validator';
import {
  DEFAULT_REQUEST_CONNECTOR_PAGE_FORM_VALUE,
  RequestConnectorPageFormModel,
  RequestConnectorPageFormValue,
} from './request-connector-page-form-model';

@Injectable()
export class RequestConnectorPageForm {
  formGroup = this.buildFormGroup();

  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<RequestConnectorPageFormModel> {
    const initial = DEFAULT_REQUEST_CONNECTOR_PAGE_FORM_VALUE;

    return this.formBuilder.nonNullable.group({
      connectorTitle: [
        initial.connectorTitle,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      connectorSubdomain: [
        initial.connectorSubdomain,
        [
          Validators.required,
          Validators.maxLength(128),
          ...subdomainValidators,
        ],
      ],
      connectorDescription: [
        initial.connectorDescription,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
    });
  }

  get getValue(): RequestConnectorPageFormValue {
    return this.formGroup.value as RequestConnectorPageFormValue;
  }
}
