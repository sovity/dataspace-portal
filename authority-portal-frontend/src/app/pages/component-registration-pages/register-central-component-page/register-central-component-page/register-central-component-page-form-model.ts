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
import {FormControl, FormGroup, ɵFormGroupRawValue} from '@angular/forms';
import {
  CertificateFormModel,
  DEFAULT_CERTIFICATE_FORM_VALUE,
} from '../../../../shared/form-elements/certificate-input-form/certificate-input-form-model';

export interface ComponentTabFormModel {
  name: FormControl<string>;
  location: FormControl<string>;
  frontendUrl: FormControl<string>;
  endpointUrl: FormControl<string>;
}

export type ComponentTabFormValue = ɵFormGroupRawValue<ComponentTabFormModel>;
export const DEFAULT_COMPONENT_TAB_FORM_VALUE: ComponentTabFormValue = {
  name: '',
  location: '',
  frontendUrl: '',
  endpointUrl: '',
};

export interface CertificateTabFormModel extends CertificateFormModel {}

export type CertificateTabFormValue =
  ɵFormGroupRawValue<CertificateTabFormModel>;
export const DEFAULT_CERTIFICATE_TAB_FORM_VALUE: CertificateTabFormValue =
  DEFAULT_CERTIFICATE_FORM_VALUE;

export interface RegisterCentralComponentPageFormModel {
  componentTab: FormGroup<ComponentTabFormModel>;
  certificateTab: FormGroup<CertificateTabFormModel>;
}

export const DEFAULT_REGISTER_CENTRAL_COMPONENT_PAGE_FORM_VALUE: RegisterCentralComponentPageFormValue =
  {
    componentTab: DEFAULT_COMPONENT_TAB_FORM_VALUE,
    certificateTab: DEFAULT_CERTIFICATE_TAB_FORM_VALUE,
  };
export type RegisterCentralComponentPageFormValue =
  ɵFormGroupRawValue<RegisterCentralComponentPageFormModel>;
