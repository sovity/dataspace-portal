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

export type AuthorityInviteNewOrganizationPageFormValue =
  ɵFormGroupRawValue<AuthorityInviteNewOrganizationPageFormModel>;

export interface AuthorityInviteNewOrganizationPageFormModel {
  userEmail: FormControl<string>;
  userFirstName: FormControl<string>;
  userLastName: FormControl<string>;
  orgName: FormControl<string>;
  userJobTitle: FormControl<string>;
  userPhoneNumber: FormControl<string>;
}

export const DEFAULT_AUTHORITY_INVITE_NEW_ORGANIZATION_FORM_VALUE: AuthorityInviteNewOrganizationPageFormValue =
  {
    userEmail: '',
    userFirstName: '',
    userLastName: '',
    orgName: '',
    userJobTitle: '',
    userPhoneNumber: '',
  };
