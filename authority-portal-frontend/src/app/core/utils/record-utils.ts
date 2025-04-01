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
 * Remove fields with null values from Property Records due to EDC Backend expecting non-null values
 * @param obj object / record
 */
export function removeNullValues(
  obj: Record<string, string | null>,
): Record<string, string> {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => v != null) as [string, string][],
  );
}

/**
 * Remove fields with undefined values from property records
 * @param obj object / record
 */
export function removeUndefinedValues(
  obj: Record<string, string | undefined>,
): Record<string, string> {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => v != null) as [string, string][],
  );
}

/**
 * Maps keys of a given object
 * @param obj object
 * @param mapFn key mapper
 * @return new object with keys mapped
 */
export function mapKeys<
  K extends string | number | symbol,
  L extends string | number | symbol,
  V,
>(obj: Record<K, V>, mapFn: (key: K) => L): Record<L, V> {
  return Object.fromEntries(
    Object.entries(obj).map(([k, v]) => [mapFn(k as K), v]),
  ) as Record<L, V>;
}
