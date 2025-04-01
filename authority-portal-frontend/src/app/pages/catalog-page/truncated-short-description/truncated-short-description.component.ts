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
  selector: 'truncated-short-description',
  templateUrl: './truncated-short-description.component.html',
})
export class TruncatedShortDescription {
  @Input() text!: string | undefined;
  @HostBinding('class.whitespace-pre-line')
  @HostBinding('class.line-clamp-5')
  cls = true;
  @HostBinding('class.italic')
  get italic(): boolean {
    return !this.text;
  }
}
