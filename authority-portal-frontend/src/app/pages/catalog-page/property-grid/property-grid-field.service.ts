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
import {formatDate} from '@angular/common';
import {Inject, Injectable, LOCALE_ID} from '@angular/core';
import {validUrlPattern} from 'src/app/core/utils/validators/url-validator';
import {PropertyGridField} from './property-grid-field';

@Injectable({providedIn: 'root'})
export class PropertyGridFieldService {
  constructor(@Inject(LOCALE_ID) private locale: string) {}

  guessValue(
    value: string | null | undefined,
  ): Pick<PropertyGridField, 'url' | 'text' | 'additionalClasses'> {
    return {
      text: value || '-',
      url: value?.match(validUrlPattern) ? value : undefined,
      additionalClasses: value?.includes(' ') ? undefined : 'break-all',
    };
  }

  formatDate(date: Date | null | undefined): string {
    if (!date) {
      return '';
    }

    return formatDate(date, 'yyyy-MM-dd HH:mm:ss', this.locale);
  }
}
