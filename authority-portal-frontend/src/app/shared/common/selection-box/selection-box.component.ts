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
import {Router} from '@angular/router';
import {APP_CONFIG, AppConfig} from '../../../core/services/config/app-config';
import {SelectionBoxModel} from './selection-box.model';

@Component({
  selector: 'app-selection-box',
  templateUrl: './selection-box.component.html',
})
export class SelectionBoxComponent {
  @Input() selectionBoxConfig!: SelectionBoxModel;
  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private router: Router,
  ) {}

  onAction() {
    if (this.selectionBoxConfig?.action?.url) {
      if (!this.selectionBoxConfig.action.isDisabled) {
        this.router.navigate([this.selectionBoxConfig.action.url]);
      }
    }
    if (this.selectionBoxConfig?.action?.externalUrl) {
      window.open(this.selectionBoxConfig.action.externalUrl, '_blank');
    }
  }
}
