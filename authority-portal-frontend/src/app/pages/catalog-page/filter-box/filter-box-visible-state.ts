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
import {search} from 'src/app/core/utils/search-utils';
import {difference} from 'src/app/core/utils/set-utils';
import {FilterBoxItem} from './filter-box-item';
import {FilterBoxModel} from './filter-box-model';

/**
 * Utility Class for interpreting a {@link FilterBoxModel}.
 */
export class FilterBoxVisibleState {
  constructor(
    /**
     * Filter ID, required for trackBy
     */
    public id: string,
    /**
     * Available Items + Texts
     */
    public model: FilterBoxModel,
    /**
     * Items after applying search
     */
    public visibleItems: FilterBoxItem[],
    /**
     * Selected Items
     */
    public selectedIds: Set<string>,
  ) {}

  /**
   * Calculates the visible state from search text, selected items, available items.
   * @param model search text, selected items, available items
   */
  static buildVisibleState(model: FilterBoxModel): FilterBoxVisibleState {
    const {selectedItems, availableItems, searchText} = model;

    const {selectedIds, allItems} = this.mergeSelectedAndAvailableItems(
      selectedItems,
      availableItems,
    );

    const visibleItems = search(allItems, searchText, (item) => [
      item.id,
      item.label,
    ]);

    return new FilterBoxVisibleState(
      model.id,
      model,
      visibleItems,
      selectedIds,
    );
  }

  private static mergeSelectedAndAvailableItems(
    selectedItems: FilterBoxItem[],
    availableItems: FilterBoxItem[],
  ): {selectedIds: Set<string>; allItems: FilterBoxItem[]} {
    const items = new Map<string, FilterBoxItem>();
    selectedItems.forEach((item) => items.set(item.id, item));
    availableItems.forEach((item) => items.set(item.id, item));

    const selectedIds = new Set<string>(selectedItems.map((item) => item.id));
    const availableIds = new Set<string>(availableItems.map((item) => item.id));
    const selectedUnavailableItems = [
      ...difference(selectedIds, availableIds),
    ].map((it) => items.get(it)!!);

    // Items that are selected, but not part of the available items should show up first in the list
    const allItems = [...selectedUnavailableItems, ...availableItems];
    return {selectedIds, allItems};
  }

  get numSelectedItems(): number {
    return this.selectedIds.size;
  }

  isSelected(item: FilterBoxItem): boolean {
    return this.selectedIds.has(item.id);
  }

  isEqualSelectedItems(items: FilterBoxItem[]): boolean {
    return (
      this.selectedIds.size === items.length &&
      items.every((item) => this.selectedIds.has(item.id))
    );
  }
}
