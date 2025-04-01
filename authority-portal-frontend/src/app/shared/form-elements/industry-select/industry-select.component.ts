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
import {Component, HostBinding, Input, booleanAttribute} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-industry-select',
  templateUrl: './industry-select.component.html',
})
export class IndustrySelectComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.flex-1')
  cls = true;

  @Input()
  label: string = 'Industry';

  @Input()
  ctrl: FormControl<string | null> = new FormControl();

  @Input()
  ctrlId = 'industry';

  @Input({transform: booleanAttribute})
  required = true;

  industries = [
    'Automotive industry',
    'Consulting',
    'Electrical engineering',
    'E-Mobility',
    'Energy industry',
    'Finance',
    'Geodata',
    'Government',
    'Information and communication technology',
    'Infrastructure operator',
    'Insurance industry',
    'Logistics',
    'Meteorological services',
    'Micromobility provider',
    'Municipality',
    'Passenger transportation',
    'Research & Development',
    'Sensor supplier',
    'Software development',
    'Telecommunication',
    'Tourism',
    'Traffic engineering',
    'Other',
  ];
}
