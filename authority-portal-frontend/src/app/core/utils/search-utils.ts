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
 * Simple search that tries to find all search query words in target strings of given items
 * @param items item list
 * @param query search query
 * @param targetsFn search targets
 */
export function search<T>(
  items: T[],
  query: string | null,
  targetsFn: (item: T) => (string | null)[],
): T[] {
  const words = (query ?? '')
    .toLowerCase()
    .split(' ')
    .map((it) => it.trim())
    .filter((it) => it);

  return items.filter((item) => {
    const targets = targetsFn(item)
      .map((it) => it?.toLowerCase())
      .filter((it) => it) as string[];
    return words.every((word) => targets.some((value) => value.includes(word)));
  });
}
