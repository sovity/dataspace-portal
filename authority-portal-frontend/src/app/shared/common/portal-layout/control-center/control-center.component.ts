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
import {AvatarConfig} from '../avatar/avatar.component';
import {ControlCenterModel} from './control-center.model';

@Component({
  selector: 'app-control-center',
  templateUrl: './control-center.component.html',
})
export class ControlCenterComponent implements OnChanges {
  @Input() userData!: ControlCenterModel;
  @Input() logoutUrl!: string;

  userAvatar!: AvatarConfig;
  userMenuOpen = false;

  ngOnChanges(): void {
    this.userAvatar = {
      firstName: this.userData.firstName,
      lastName: this.userData.lastName,
    };
  }
}
