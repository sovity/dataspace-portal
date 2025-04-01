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
import {
  CatalogPageResult,
  CatalogPageSortingItem,
  PaginationMetadata,
} from '@sovity.de/authority-portal-client';
import {Fetched} from 'src/app/core/utils/fetched';
import {FilterBoxVisibleState} from '../../filter-box/filter-box-visible-state';
import {CatalogActiveFilterPill} from './catalog-active-filter-pill';

export interface CatalogPageStateModel {
  /**
   * We need an initial fetch to succeed so we can get the filters and sortings.
   */
  isPageReady: boolean;

  /**
   * User Input: Search Text
   */
  searchText: string;

  /**
   * User Input: Selected Filter
   */
  activeSorting: CatalogPageSortingItem | null;

  /**
   * User Input: Selected Filters
   * (and also includes filter definitions)
   */
  filters: Record<string, FilterBoxVisibleState>;

  /**
   * sorting definitions
   */
  sortings: CatalogPageSortingItem[];

  /**
   * Active Filter Pills as derived from user input.
   */
  activeFilterPills: CatalogActiveFilterPill[];

  /**
   * Data
   */
  fetchedData: Fetched<CatalogPageResult>;

  /**
   * Pagination Information kept also between calls so we can render the pagination component, always.
   */
  paginationMetadata: PaginationMetadata;

  /**
   * We disable the pagination component for calls after the filters have changed
   * because we expect different numbers of results.
   */
  paginationDisabled: boolean;

  /**
   * Current Page
   */
  pageZeroBased: number;
}

export const EMPTY_PAGINATION_METADATA: PaginationMetadata = {
  pageSize: 0,
  pageOneBased: 1,
  numTotal: 0,
  numVisible: 0,
};

export const DEFAULT_CATALOG_PAGE_STATE_MODEL: CatalogPageStateModel = {
  isPageReady: false,

  searchText: '',
  activeFilterPills: [],
  filters: {},
  sortings: [],
  activeSorting: null,

  fetchedData: Fetched.empty(),
  paginationMetadata: EMPTY_PAGINATION_METADATA,
  paginationDisabled: true,
  pageZeroBased: 0,
};
