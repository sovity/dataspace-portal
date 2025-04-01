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
import {Component, Inject, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';

@Component({
  selector: 'app-tos-check',
  templateUrl: './tos-check.component.html',
})
export class TosCheckComponent {
  @Input()
  ctrl: FormControl<boolean> = new FormControl();

  @Input()
  ctrlId = 'dsgvo-tos';

  constructor(@Inject(APP_CONFIG) public appConfig: AppConfig) {}
}
