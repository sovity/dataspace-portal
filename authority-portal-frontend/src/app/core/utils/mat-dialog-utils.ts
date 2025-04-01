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
import {ComponentType} from '@angular/cdk/portal';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {Observable} from 'rxjs';

/**
 * Method for launching Angular Material Dialogs with the lifetime of the dialog being handled by a until$ observable
 *
 * @param dialogService MatDialog
 * @param dialog ComponentType
 * @param config MatDialogConfig
 * @param until$ Observable that controls the lifetime of the dialog
 * @return afterClosed Observable
 */
export function showDialogUntil<T, R>(
  dialogService: MatDialog,
  dialog: ComponentType<any>,
  config: MatDialogConfig<T>,
  until$: Observable<unknown>,
): Observable<R | undefined> {
  const ref = dialogService.open(dialog, config);
  until$.subscribe({
    next: () => ref.close(),
    complete: () => ref.close(),
  });
  return ref.afterClosed();
}
