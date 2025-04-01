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
  CnfFilterAttribute,
  CnfFilterAttributeDisplayType,
} from '@sovity.de/authority-portal-client';
import {FilterBoxItem, buildFilterBoxItems} from './filter-box-item';

/**
 * State of a single Filter
 */
export interface FilterBoxModel {
  id: string;
  title: string;
  selectedItems: FilterBoxItem[];
  availableItems: FilterBoxItem[];
  searchText: string;
  displayType: CnfFilterAttributeDisplayType;
}

export function buildFilterBoxModelWithNewData(
  fetched: CnfFilterAttribute,
  old: FilterBoxModel | null,
): FilterBoxModel {
  const availableItems = buildFilterBoxItems(fetched.values);
  return {
    id: fetched.id,
    title: fetched.title,
    availableItems,
    searchText: old?.searchText ?? '',
    selectedItems: withUpdatedTitles(old?.selectedItems ?? [], availableItems),
    displayType: fetched.displayType,
  };
}

function withUpdatedTitles(
  selectedItems: FilterBoxItem[],
  availableItems: FilterBoxItem[],
): FilterBoxItem[] {
  const fetchedById = new Map<string, FilterBoxItem>(
    availableItems.map((item) => [item.id, item]),
  );

  const isSame = (a: FilterBoxItem, b: FilterBoxItem) => a.label === b.label;

  return selectedItems.map((oldItem) => {
    const newItem = fetchedById.get(oldItem.id);
    if (newItem == null || isSame(newItem, oldItem)) {
      return oldItem;
    }

    return newItem;
  });
}
