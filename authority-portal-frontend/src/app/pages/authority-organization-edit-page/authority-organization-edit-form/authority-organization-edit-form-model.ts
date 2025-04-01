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
import {OrganizationLegalIdTypeDto} from '@sovity.de/authority-portal-client';
import {OrganizationEditFormModel} from '../../../shared/business/organization-edit-form/organization-edit-form-model';

export interface AuthorityOrganizationEditFormModel
  extends OrganizationEditFormModel {
  legalName: FormControl<string>;
  legalIdType: FormControl<OrganizationLegalIdTypeDto>;
  legalId: FormControl<string>;
  commerceRegisterLocation: FormControl<string>;
}

export type AuthorityOrganizationEditFormValue =
  ɵFormGroupRawValue<AuthorityOrganizationEditFormModel>;
