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
import {Pipe, PipeTransform} from '@angular/core';
import {
  OrganizationOverviewEntryDto,
  OrganizationRegistrationStatusDto,
} from '@sovity.de/authority-portal-client';

@Pipe({
  name: 'sortingFilter',
})
export class FilterByRegistrationStatusPipe implements PipeTransform {
  transform(
    items: OrganizationOverviewEntryDto[],
    filter:
      | OrganizationRegistrationStatusDto
      | OrganizationRegistrationStatusDto[]
      | null,
  ): OrganizationOverviewEntryDto[] {
    if (!items || !filter || filter.length === 0) {
      return items;
    }

    if (Array.isArray(filter)) {
      // If filter is an array, filter by multiple statuses
      return items.filter((item) => filter.includes(item.registrationStatus));
    } else {
      // If filter is a single object, filter by that status
      return items.filter((item) => item.registrationStatus === filter);
    }
  }
}
