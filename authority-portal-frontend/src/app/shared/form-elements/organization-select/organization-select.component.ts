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
import {FormControl} from '@angular/forms';
import {OrganizationOverviewEntryDto} from '@sovity.de/authority-portal-client';
import {Fetched} from 'src/app/core/utils/fetched';

@Component({
  selector: 'app-organization-select',
  templateUrl: './organization-select.component.html',
})
export class OrganizationSelectComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-between')
  @HostBinding('class.items-center')
  cls = true;

  @Input()
  label = 'Organization';

  @Input()
  ctrl: FormControl<OrganizationOverviewEntryDto | null> = new FormControl();

  @Input()
  ctrlId = 'organization';

  @Input()
  organizations: Fetched<OrganizationOverviewEntryDto[]> = Fetched.loading();

  compareWith = (
    o1: OrganizationOverviewEntryDto | null,
    o2: OrganizationOverviewEntryDto | null,
  ) => o1?.id === o2?.id;
}
