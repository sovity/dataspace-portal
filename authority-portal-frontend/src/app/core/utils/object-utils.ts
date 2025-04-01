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

export type Patcher<T> = (value: T) => Partial<T>;

export function patchObj<T>(obj: T, patcher: Patcher<T>): T {
  return {...obj, ...patcher(obj)};
}

/**
 * Create Object with entries [keyExtractor(it), valueExtractor(it)]
 * @param array items
 * @param keyExtractor key extractor
 * @param valueExtractor value extractor
 */
export function associateAsObj<T, K extends string | number, R>(
  array: T[],
  keyExtractor: (it: T) => K,
  valueExtractor: (it: T) => R,
): Record<K, R> {
  return Object.fromEntries(
    array.map((it) => [keyExtractor(it), valueExtractor(it)]),
  ) as Record<K, R>;
}
