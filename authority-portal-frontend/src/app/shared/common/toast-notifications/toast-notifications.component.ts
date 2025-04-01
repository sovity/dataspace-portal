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
import {Component, OnDestroy, OnInit, TrackByFunction} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {ToastNotification, ToastService} from './toast.service';

@Component({
  selector: 'app-toast-notifications',
  templateUrl: './toast-notifications.component.html',
})
export class ToastNotificationsComponent implements OnInit, OnDestroy {
  toasts: ToastNotification[] = [];

  constructor(public toastService: ToastService) {}

  dismiss(id: number): void {
    this.toastService.dismissToast(id);
  }

  trackByFn: TrackByFunction<ToastNotification> = (_, item) => item.id;

  ngOnInit(): void {
    this.toastService.toasts$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((toasts) => {
        this.toasts = toasts;
      });
  }

  ngOnDestroy$ = new Subject<void>();
  ngOnDestroy(): void {
    this.ngOnDestroy$.next();
    this.ngOnDestroy$.complete();
  }
}
