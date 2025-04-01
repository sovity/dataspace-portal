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
import {BehaviorSubject} from 'rxjs';

export type ToastNotification = {
  id: number;
  style: ToastStyle;
  message: string;
};

export enum ToastStyle {
  Success = 'SUCCESS',
  Warning = 'WARNING',
  Danger = 'DANGER',
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  toasts$: BehaviorSubject<ToastNotification[]> = new BehaviorSubject<
    ToastNotification[]
  >([]);

  private id = 1;

  showSuccess(message: string): void {
    this.showToast(ToastStyle.Success, message);
  }

  showDanger(message: string): void {
    this.showToast(ToastStyle.Danger, message);
  }

  showWarning(message: string): void {
    this.showToast(ToastStyle.Warning, message);
  }

  showToast(style: ToastStyle, message: string): void {
    const newToast: ToastNotification = {
      id: this.nextId(),
      style,
      message,
    };

    this.patchToasts((toasts) => [...toasts, newToast]);

    setTimeout(() => this.dismissToast(newToast.id), 5000);
  }

  dismissToast(id: number): void {
    this.patchToasts((toasts) => toasts.filter((toast) => toast.id !== id));
  }

  private nextId(): number {
    return this.id++;
  }

  private patchToasts(
    mapper: (previous: ToastNotification[]) => ToastNotification[],
  ) {
    this.toasts$.next(mapper(this.toasts$.value));
  }
}
