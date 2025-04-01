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
import {Component, Input, OnChanges} from '@angular/core';

export interface AvatarConfig {
  firstName: string;
  lastName: string;
}
@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
})
export class AvatarComponent implements OnChanges {
  @Input() name!: AvatarConfig;
  userName: string = '';

  ngOnChanges(): void {
    if (this.name.firstName.length > 0 && this.name.lastName.length > 0) {
      const firstInitial = this.name.firstName.charAt(0).toUpperCase();
      const lastInitial = this.name.lastName.charAt(0).toUpperCase();
      this.userName = firstInitial + lastInitial;
    }
  }
}
