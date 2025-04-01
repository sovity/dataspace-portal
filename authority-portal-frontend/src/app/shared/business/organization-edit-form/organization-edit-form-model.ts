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

export interface OrganizationEditFormModel {
  website: FormControl<string>;
  businessUnit: FormControl<string>;
  industry: FormControl<string>;
  description: FormControl<string>;

  mainAddress: FormControl<string>;

  billingAddressSameAsMain: FormControl<boolean>;
  billingAddress: FormControl<string>;

  mainContactName: FormControl<string>;
  mainContactPhoneNumber: FormControl<string>;
  mainContactEmail: FormControl<string>;

  technicalContactSameAsMain: FormControl<boolean>;
  technicalContactName: FormControl<string>;
  technicalContactPhoneNumber: FormControl<string>;
  technicalContactEmail: FormControl<string>;
}

export type OrganizationEditFormValue =
  ɵFormGroupRawValue<OrganizationEditFormModel>;
