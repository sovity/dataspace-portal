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
 * Remove item once from list.
 *
 * Use this over .filter(...) to remove items on user interactions
 * to prevent one click from removing many items.
 *
 * Returns copy.
 */
export function removeOnce<T>(list: T[], item: T): T[] {
  const index = list.indexOf(item);
  if (index >= 0) {
    const copy = [...list];
    copy.splice(index, 1);
    return copy;
  }
  return list;
}

export function filterNonNull<T>(array: (T | null | undefined)[]): T[] {
  return array.filter((it) => it != null) as T[];
}
