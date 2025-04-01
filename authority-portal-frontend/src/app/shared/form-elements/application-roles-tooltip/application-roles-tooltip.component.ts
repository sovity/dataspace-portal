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
import {Component} from '@angular/core';

@Component({
  selector: 'app-application-roles-tooltip',
  templateUrl: './application-roles-tooltip.component.html',
})
export class ApplicationRolesTooltipComponent {
  // may need refinements
  applicationRolesTooltip = `
AUTHORITY USER
Can view all participant details, invite new participants and manage pending registration requests

AUTHORITY ADMIN
Can additionally manage application roles for all users

SERVICE PARTNER ADMIN
Can provide connectors for other participants and manage them

OPERATOR ADMIN
Can provide and manage central components in DAPS and manage all connectors
  `;
}
