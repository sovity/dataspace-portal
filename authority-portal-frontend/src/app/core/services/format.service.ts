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
import {formatNumber} from '@angular/common';
import {Inject, Injectable, LOCALE_ID} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class FormatService {
  constructor(@Inject(LOCALE_ID) private locale: string) {}

  formatNumber(value: number | null | undefined, digitsInfo?: string): string {
    return value == null ? '' : formatNumber(value, this.locale, digitsInfo);
  }

  formatInteger(value: number | null | undefined): string {
    return this.formatNumber(value, '1.0-0');
  }
}
