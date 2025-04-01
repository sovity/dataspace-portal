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
 * Group items by key extractor
 * @param array items
 * @param keyExtractor key extractor
 */
export function groupedBy<T, K>(
  array: T[],
  keyExtractor: (it: T) => K,
): Map<K, T[]> {
  const map = new Map<K, T[]>();
  array.forEach((it) => {
    const key = keyExtractor(it);
    if (!map.has(key)) {
      map.set(key, []);
    }
    map.get(key)!.push(it);
  });
  return map;
}

/**
 * Create Map with entries [keyExtractor(it), it]
 * @param array items
 * @param keyExtractor key extractor
 */
export function associateBy<T, K>(
  array: T[],
  keyExtractor: (it: T) => K,
): Map<K, T> {
  return new Map(array.map((it) => [keyExtractor(it), it]));
}
