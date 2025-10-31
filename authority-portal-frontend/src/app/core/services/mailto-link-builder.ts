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
import {removeUndefinedValues} from '../utils/record-utils';

@Injectable({providedIn: 'root'})
export class MailtoLinkBuilder {
  private readonly MAILTO = 'mailto:';

  constructor() {}

  buildMailtoUrl(
    email: string,
    subject?: string,
    body?: string,
    cc?: string,
    bcc?: string,
  ): string {
    const queryParams = new URLSearchParams(
      removeUndefinedValues({
        subject,
        body,
        cc,
        bcc,
      }),
    );
    // URLSearchParams replaces spaces with '+', so we need to replace them with '%20' for the mail scenario
    const queryParamsStr = queryParams.toString().replaceAll('+', '%20');
    const queryStr = queryParamsStr ? `?${queryParamsStr}` : '';

    return `${this.MAILTO}${email}${queryStr}`;
  }
}
