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
import {CatalogPageSortingItem} from '@sovity.de/authority-portal-client';
import {FilterBoxItem} from '../../filter-box/filter-box-item';
import {CatalogActiveFilterPill} from './catalog-active-filter-pill';

export namespace CatalogPage {
  const tag = 'CatalogPage';

  export class Reset {
    static readonly type = `[${tag}] Reset`;

    constructor(public initialOrganizationIds?: string[]) {}
  }

  export class NeedFetch {
    static readonly type = `[${tag}] Data is out of date, need a fetch.`;
  }

  export class Fetch {
    static readonly type = `[${tag}] Actually fetch data.`;
  }

  export class UpdatePage {
    static readonly type = `[${tag}] Update the selected page`;

    constructor(public pageZeroBased: number) {}
  }

  export class UpdateSearchText {
    static readonly type = `[${tag}] Update the search bar's text`;

    constructor(public searchText: string) {}
  }

  export class UpdateSorting {
    static readonly type = `[${tag}] Update the selected sorting`;

    constructor(public sorting: CatalogPageSortingItem | null) {}
  }

  export class UpdateFilterSelectedItems {
    static readonly type = `[${tag}] Update a Filter's selected Items`;

    constructor(
      public filterId: string,
      public selectedItems: FilterBoxItem[],
    ) {}
  }

  export class UpdateFilterSearchText {
    static readonly type = `[${tag}] Update a Filter's search text`;

    constructor(public filterId: string, public searchText: string) {}
  }

  export class RemoveActiveFilterItem {
    static readonly type = `[${tag}] Remove an active filter from the chips below the search bar`;

    constructor(public item: CatalogActiveFilterPill) {}
  }

  export class EnvironmentChange {
    static readonly type = `[${tag}] Environment Change`;
  }
}
