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

export function kebabCaseToSentenceCase(input: string): string {
  return input
    .split('-')
    .map((w) => {
      return w.toLowerCase() === 'and'
        ? w
        : w.charAt(0).toUpperCase() + w.slice(1);
    })
    .join(' ');
}

export function trimmedOrNull(s?: string | null): string | null {
  const trimmed = s?.trim();
  return trimmed ? trimmed : null;
}

export function everythingBefore(separator: string, s: string): string {
  const index = s.indexOf(separator);
  return index === -1 ? s : s.substring(0, index);
}

export function everythingAfter(separator: string, s: string): string {
  const index = s.indexOf(separator);
  return index === -1 ? '' : s.substring(index + separator.length);
}

export function capitalize(s: string) {
  return s.charAt(0).toUpperCase() + s.slice(1);
}

export function inferArticle(input: string) {
  const first = input.charAt(0);
  return ['a', 'e', 'i', 'o', 'u'].includes(first) ? 'an' : 'a';
}

export function toKebabCase(input: string): string {
  return input
    .replace(/([a-z])([A-Z])/g, '$1-$2')
    .replace(/\s+/g, '-')
    .toLowerCase();
}

/**
 * Removes the common leading whitespace from all lines in a multi-line string.
 * Empty lines are not considered when calculating the common indentation.
 *
 * @param str - The input string to trim indentation from
 * @returns The string with common leading whitespace removed
 */
export function trimIndent(str: string): string {
  if (!str) {
    return str;
  }

  // Split into lines while preserving line endings
  const lines = str.split(/\r?\n/);

  // Find non-empty lines to calculate common indentation
  const nonEmptyLines = lines.filter((line) => line.trim().length > 0);

  if (nonEmptyLines.length === 0) {
    return str; // All lines are empty, return as-is
  }

  // Calculate the minimum indentation (leading whitespace)
  const indentations = nonEmptyLines.map((line) => {
    const match = line.match(/^(\s*)/);
    return match ? match[1].length : 0;
  });

  const minIndent = Math.min(...indentations);

  // Remove the common indentation from all lines
  const trimmedLines = lines.map((line) => {
    if (line.trim().length === 0) {
      return line; // Keep empty lines as-is
    }
    return line.substring(minIndent);
  });

  return trimmedLines.join('\n');
}
