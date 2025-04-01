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
import {EMPTY, MonoTypeOperatorFunction} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';

@Injectable({providedIn: 'root'})
export class ErrorService {
  constructor(private toastService: ToastService) {}

  toastFailure(failureMessage: string, error?: any) {
    this.toastService.showDanger(failureMessage);
    console.error(...[failureMessage, error].filter((it) => it !== undefined));
  }

  toastFailureRxjs<T>(
    failureMessage: string,
    onError?: () => void,
  ): MonoTypeOperatorFunction<T> {
    return catchError((error) => {
      this.toastFailure(failureMessage, error);
      if (onError) {
        onError();
      }
      return EMPTY;
    });
  }

  toastRegistrationErrorRxjs<T>(
    genericFailureMessage: string,
    onError?: () => void,
  ): MonoTypeOperatorFunction<T> {
    return catchError((err) => {
      let errorMessage = genericFailureMessage;
      if (err?.response?.status === 409) {
        errorMessage = 'A user with this email address already exists.';
      }
      this.toastFailure(errorMessage);
      if (onError) {
        onError();
      }
      return EMPTY;
    });
  }
}
