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
import {toKebabCase} from '../../../core/utils/string-utils';
import {CONTROL_CENTER_ROUTES} from '../control-center-routes';

export interface ControlCenterTab {
  title: string;
  routerLink: string[];
}

@Component({
  selector: 'app-control-center-page',
  templateUrl: './control-center-page.component.html',
})
export class ControlCenterPageComponent {
  tabs: ControlCenterTab[] = this.buildControlCenterTabs();

  private buildControlCenterTabs(): ControlCenterTab[] {
    let tabs = CONTROL_CENTER_ROUTES.filter((it) => !it.data.excludeFromTabs);
    return tabs.map((it) => ({
      title: it.data.title,
      routerLink: ['/control-center', it.path!],
    }));
  }

  protected readonly toKebabCase = toKebabCase;
}
