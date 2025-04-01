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
import {LANGUAGE_DATA, LanguageInfo} from './language-data';

/**
 * Access list of available LanguageSelectItems
 */
@Injectable({providedIn: 'root'})
export class LanguageService {
  itemsByKeyword: Map<string, LanguageInfo>;
  keywords = (it: LanguageInfo) => [it.id, it.idShort, it.sameAs];

  constructor() {
    this.itemsByKeyword = this.buildItemLookupMap();
  }

  /**
   * Find LanguageSelectItem by id
   * @param id idShort or sameAs
   */
  findLabel(id: string): string {
    let item = this.itemsByKeyword.get(id);
    if (item) {
      return item.label;
    }

    return id;
  }

  private buildItemLookupMap(): Map<string, LanguageInfo> {
    const map = new Map<string, LanguageInfo>();
    LANGUAGE_DATA.forEach((info) => {
      this.keywords(info)
        .filter((key) => !!key)
        .forEach((key) => map.set(key!!, info));
    });
    return map;
  }
}
