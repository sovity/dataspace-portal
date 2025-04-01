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

/**
 * Takes the year/month/day information of a local date and creates a new Date object from it.
 * Hour offset context is removed.
 * Can be used to ensure dates are displayed identically across different timezones when stringified in JSON payloads.
 * @param date date to convert
 */
import {format} from 'date-fns-tz';

export function toGmtZeroHourDate(date: Date): Date {
  return new Date(format(date, 'yyyy-MM-dd'));
}
