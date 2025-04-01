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
import {MonoTypeOperatorFunction} from 'rxjs';
import {sampleTime, takeUntil} from 'rxjs/operators';
import {Actions, Store, ofActionDispatched} from '@ngxs/store';

@Injectable({providedIn: 'root'})
export class NgxsUtils {
  constructor(private actions$: Actions, private store: Store) {}

  sampleTime(
    debounceActionType: any,
    executeActionType: any,
    sampleTimeMs: number,
  ) {
    this.actions$
      .pipe(ofActionDispatched(debounceActionType), sampleTime(sampleTimeMs))
      .subscribe(() => {
        this.store.dispatch(executeActionType);
      });
  }

  takeUntil<T>(actionType: any): MonoTypeOperatorFunction<T> {
    return (obs) =>
      obs.pipe(takeUntil(this.actions$.pipe(ofActionDispatched(actionType))));
  }
}
