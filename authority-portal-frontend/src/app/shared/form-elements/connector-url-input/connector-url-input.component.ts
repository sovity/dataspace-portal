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
  selector: 'app-connector-url-input',
  templateUrl: './connector-url-input.component.html',
})
export class ConnectorUrlInputComponent {
  @HostBinding('class.select-none')
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-between')
  @HostBinding('class.items-center')
  cls = true;

  @Input()
  label: string = 'Unnamed';

  @Input()
  ctrl: FormControl<string> = new FormControl();

  @Input()
  ctrlId = 'connector-url-' + Math.random().toString(36).substring(7);

  @Input()
  urlSuffix = '';

  @Input({transform: booleanAttribute})
  required = true;

  get placeholder() {
    return 'https://my-connector.my-org.com' + this.urlSuffix;
  }

  exampleBaseUrl = 'https://{{ Connector URL }}';
}
