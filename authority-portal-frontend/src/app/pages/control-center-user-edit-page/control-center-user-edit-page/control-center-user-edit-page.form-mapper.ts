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
import {UpdateUserDto, UserDetailDto} from '@sovity.de/authority-portal-client';
import {ControlCenterUserEditPageFormValue} from './control-center-user-edit-page.form-model';

export function buildFormValue(
  user: UserDetailDto,
): ControlCenterUserEditPageFormValue {
  return {
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    jobTitle: user.position,
    phoneNumber: user.phone,
  };
}

export function buildEditRequest(
  formValue: ControlCenterUserEditPageFormValue,
): UpdateUserDto {
  return {
    firstName: formValue.firstName,
    lastName: formValue.lastName,
    email: formValue.email,
    jobTitle: formValue.jobTitle,
    phone: formValue.phoneNumber,
  };
}
