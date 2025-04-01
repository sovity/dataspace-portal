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

@Component({
  selector: 'app-error-element',
  templateUrl: './error-element.component.html',
})
export class ErrorElementComponent {
  @HostBinding('class.flex')
  @HostBinding('class.items-center')
  @HostBinding('class.justify-center')
  @HostBinding('class.text-center')
  @HostBinding('class.px-6')
  @HostBinding('class.py-24')
  @HostBinding('class.sm:py-32')
  @HostBinding('class.lg:px-8')
  cls = true;

  @Input() errorMessage: string = '';
}
