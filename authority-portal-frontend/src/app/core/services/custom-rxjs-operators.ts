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
import {Router} from '@angular/router';
import {MonoTypeOperatorFunction, NEVER} from 'rxjs';
import {finalize, tap} from 'rxjs/operators';
import {StateContext} from '@ngxs/store';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {ErrorService} from './error.service';

@Injectable({providedIn: 'root'})
export class CustomRxjsOperators {
  constructor(
    private router: Router,
    private errorService: ErrorService,
    private toast: ToastService,
  ) {}

  onSuccessRedirect<T>(routerLink: string[]): MonoTypeOperatorFunction<T> {
    return (apiCall) => {
      return apiCall.pipe(tap(() => this.router.navigate(routerLink)));
    };
  }

  withToastResultHandling<T>(actionName: string): MonoTypeOperatorFunction<T> {
    return (apiCall) => {
      return apiCall.pipe(
        this.errorService.toastFailureRxjs(`${actionName} failed!`),
        tap(() => this.toast.showSuccess(`${actionName} was successful.`)),
      );
    };
  }

  withBusyLock<T, S extends {busy: boolean}>(
    ctx: StateContext<S>,
  ): MonoTypeOperatorFunction<T> {
    return (apiCall) => {
      if (ctx.getState().busy) {
        return NEVER;
      } else {
        ctx.patchState({busy: true} as Partial<S>);
        return apiCall.pipe(
          finalize(() => ctx.patchState({busy: false} as Partial<S>)),
        );
      }
    };
  }
}
