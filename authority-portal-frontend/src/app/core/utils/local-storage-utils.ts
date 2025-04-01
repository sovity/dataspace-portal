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

export class LocalStorageUtils {
  saveData<T>(key: string, value: T) {
    localStorage.setItem(key, JSON.stringify(value));
  }

  getData<T>(
    key: string,
    defaultValue: T,
    isValidValue: (value?: unknown) => value is T,
  ): T {
    const data = this.getDataUnsafe(key, defaultValue);
    if (isValidValue(data)) {
      return data;
    }
    return defaultValue;
  }

  private getDataUnsafe(key: string, defaultValue: any): unknown {
    const storedItem = localStorage.getItem(key);

    try {
      return storedItem == null ? defaultValue : JSON.parse(storedItem);
    } catch (e) {
      console.warn('Error parsing local storage value', key, storedItem);
      return defaultValue;
    }
  }

  removeData(key: string) {
    localStorage.removeItem(key);
  }

  clearData() {
    localStorage.clear();
  }
}
