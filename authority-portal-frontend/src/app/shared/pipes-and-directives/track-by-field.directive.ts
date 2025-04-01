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
import {NgForOf} from '@angular/common';
import {Attribute, Directive, Host, TrackByFunction} from '@angular/core';

export const newTrackByFn =
  <T>(key: keyof T): TrackByFunction<T> =>
  (_, item: T) =>
    item == null ? null : item[key] ?? item;

/**
 * Creates Track By Function for ngFor loops
 */
@Directive({
  selector: '[trackByField]',
})
export class TrackByFieldDirective {
  constructor(
    @Host() ngForOf: NgForOf<unknown>,
    @Attribute('trackByField') private readonly trackByField: string,
  ) {
    if (!ngForOf.ngForTrackBy) {
      ngForOf.ngForTrackBy = newTrackByFn(this.trackByField);
    }
  }
}
