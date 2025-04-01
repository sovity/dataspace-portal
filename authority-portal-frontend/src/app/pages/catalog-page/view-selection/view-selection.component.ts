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
import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ViewModeEnum} from './view-mode-enum';
import {ViewModeItem} from './view-mode-item';

@Component({
  selector: 'view-selection',
  templateUrl: './view-selection.component.html',
})
export class ViewSelectionComponent {
  @Input() selected!: ViewModeEnum;
  @Output() selectedChange = new EventEmitter<ViewModeEnum>();

  items: ViewModeItem[] = [
    {
      value: ViewModeEnum.GRID,
      tooltip: 'Grid View',
      icon: 'apps',
    },
    {
      value: ViewModeEnum.LIST,
      tooltip: 'List View',
      icon: 'view_headline',
    },
  ];

  onSelection(view: ViewModeEnum) {
    this.selectedChange.emit(view);
  }
}
