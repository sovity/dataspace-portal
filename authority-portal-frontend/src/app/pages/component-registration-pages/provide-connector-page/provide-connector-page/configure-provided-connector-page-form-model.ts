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
import {OrganizationOverviewEntryDto} from '@sovity.de/authority-portal-client';
import {
  CertificateFormModel,
  DEFAULT_CERTIFICATE_FORM_VALUE,
} from '../../../../shared/form-elements/certificate-input-form/certificate-input-form-model';

export interface ConnectorTabFormModel {
  frontendUrl: FormControl<string>;
  endpointUrl: FormControl<string>;
  managementUrl: FormControl<string>;
  useJwks: FormControl<boolean>;
  jwksUrl: FormControl<string>;
}
export type ConnectorTabFormValue = ɵFormGroupRawValue<ConnectorTabFormModel>;
export const DEFAULT_CONNECTOR_TAB_FORM_VALUE: ConnectorTabFormValue = {
  frontendUrl: '',
  endpointUrl: '',
  managementUrl: '',
  useJwks: false,
  jwksUrl: '',
};

export interface CertificateTabFormModel extends CertificateFormModel {}
export type CertificateTabFormValue =
  ɵFormGroupRawValue<CertificateTabFormModel>;
export const DEFAULT_CERTIFICATE_TAB_FORM_VALUE: CertificateTabFormValue =
  DEFAULT_CERTIFICATE_FORM_VALUE;

export interface ConfigureProvidedConnectorPageFormModel {
  connectorTab: FormGroup<ConnectorTabFormModel>;
  certificateTab: FormGroup<CertificateTabFormModel>;
}
export const DEFAULT_PROVIDE_CONNECTOR_PAGE_FORM_VALUE: ConfigureProvidedConnectorPageFormValue =
  {
    connectorTab: DEFAULT_CONNECTOR_TAB_FORM_VALUE,
    certificateTab: DEFAULT_CERTIFICATE_TAB_FORM_VALUE,
  };
export type ConfigureProvidedConnectorPageFormValue =
  ɵFormGroupRawValue<ConfigureProvidedConnectorPageFormModel>;
