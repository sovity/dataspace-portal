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
import {Component, HostBinding, Input, TrackByFunction} from '@angular/core';
import {PropertyGridField} from './property-grid-field';

@Component({
  selector: 'property-grid',
  templateUrl: './property-grid.component.html',
})
export class PropertyGridComponent {
  @Input()
  props: PropertyGridField[] = [];

  @Input()
  columns: number = 3;
  @HostBinding('class.grid')
  @HostBinding('class.grid-cols-3')
  @HostBinding('class.gap-4')
  cls = true;

  trackByIndex: TrackByFunction<any> = (index: number) => index;
}
