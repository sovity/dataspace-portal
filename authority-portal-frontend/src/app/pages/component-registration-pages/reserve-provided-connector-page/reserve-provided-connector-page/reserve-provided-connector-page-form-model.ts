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

export interface ConnectorInfoFormModel {
  name: FormControl<string>;
  location: FormControl<string>;
  organization: FormControl<OrganizationOverviewEntryDto | null>;
}
export type ConnectorTabFormValue = ɵFormGroupRawValue<ConnectorInfoFormModel>;
export const DEFAULT_CONNECTOR_INFO_FORM_VALUE: ConnectorTabFormValue = {
  name: '',
  location: '',
  organization: null,
};

export interface ReserveProvidedConnectorPageFormModel {
  connectorInfo: FormGroup<ConnectorInfoFormModel>;
}
export const DEFAULT_PROVIDE_CONNECTOR_PAGE_FORM_VALUE: ReserveProvidedConnectorPageFormValue =
  {
    connectorInfo: DEFAULT_CONNECTOR_INFO_FORM_VALUE,
  };
export type ReserveProvidedConnectorPageFormValue =
  ɵFormGroupRawValue<ReserveProvidedConnectorPageFormModel>;
