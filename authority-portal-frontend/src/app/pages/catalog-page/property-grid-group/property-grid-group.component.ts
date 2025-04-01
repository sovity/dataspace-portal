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
import {Component, HostBinding, Input} from '@angular/core';
import {PropertyGridGroup} from './property-grid-group';

@Component({
  selector: 'property-grid-group',
  templateUrl: './property-grid-group.component.html',
})
export class PropertyGridGroupComponent {
  @Input()
  propGroups: PropertyGridGroup[] = [];

  @Input()
  columns: number = 3;

  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-start')
  @HostBinding('class.gap-4')
  cls = true;
}
