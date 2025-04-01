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
import {MemberInfo} from '@sovity.de/authority-portal-client';
import {mapRolesToReadableFormat} from 'src/app/core/utils/user-role-utils';

@Component({
  selector: 'app-organization-user-list',
  templateUrl: './organization-user-list.component.html',
})
export class OrganizationUserListComponent {
  @Input() users!: MemberInfo[];
  @Input() organizationId!: string;
  @Output() onUserSelected = new EventEmitter<MemberInfo>();

  mapToReadable(role: string): string {
    return mapRolesToReadableFormat(role);
  }

  selectUser(user: MemberInfo): void {
    this.onUserSelected.emit(user);
  }
}
