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

export function toMinutes(seconds: number): number {
  return seconds / 60;
}

export function toHours(seconds: number): number {
  return toMinutes(seconds) / 60;
}

export function toDays(seconds: number): number {
  return toHours(seconds) / 24;
}

export function humanizeDuration(seconds: number): string {
  const days = toDays(seconds);
  if (days >= 1) {
    if (days > 30) {
      return '30+ days';
    }
    return `${days.toFixed(0)} ${days === 1 ? 'day' : 'days'}`;
  }

  const hours = toHours(seconds);
  if (hours >= 1) {
    return `${hours.toFixed(0)} ${hours === 1 ? 'hour' : 'hours'}`;
  }

  const minutes = toMinutes(seconds);
  if (minutes >= 1) {
    return `${minutes.toFixed(0)} ${minutes === 1 ? 'minute' : 'minutes'}`;
  }

  return `${seconds.toFixed(0)} ${seconds === 1 ? 'second' : 'seconds'}`;
}
