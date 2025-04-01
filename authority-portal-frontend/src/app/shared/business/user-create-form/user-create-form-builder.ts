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
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {notBlankValidator} from 'src/app/core/utils/validators/not-blank-validator';
import {passwordEntropyValidator} from 'src/app/core/utils/validators/password-entropy-validator';
import {passwordMatchValidator} from 'src/app/core/utils/validators/password-match-validator';
import {phoneNumberValidator} from 'src/app/core/utils/validators/phone-number-validator';
import {
  UserCreateFormModel,
  UserCreateFormValue,
} from './user-create-form-model';

export const buildUserCreateForm = (
  formBuilder: FormBuilder,
  initialUser: UserCreateFormValue,
): FormGroup<UserCreateFormModel> => {
  return formBuilder.nonNullable.group(
    {
      firstName: [
        initialUser.firstName,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      lastName: [
        initialUser.lastName,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      jobTitle: [
        initialUser.jobTitle,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      phoneNumber: [
        initialUser.phoneNumber,
        [
          Validators.required,
          phoneNumberValidator,
          Validators.minLength(5),
          Validators.maxLength(28),
          notBlankValidator(),
        ],
      ],
      email: [
        initialUser.email,
        [Validators.required, Validators.maxLength(128), Validators.email],
      ],
      password: [
        initialUser.password,
        [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(128),
          notBlankValidator(),
          passwordEntropyValidator,
        ],
      ],
      confirmPassword: [initialUser.password, [Validators.required]],
    },
    {validators: passwordMatchValidator('password', 'confirmPassword')},
  );
};
