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
import {Component, Inject} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';

@Component({
  selector: 'app-unauthenticated-page',
  templateUrl: './unauthenticated-page.component.html',
})
export class UnauthenticatedPageComponent {
  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private titleService: Title,
  ) {
    this.titleService.setTitle('Unauthenticated');
  }

  get loginUrl(): string {
    const url = new URL(this.appConfig.loginUrl);
    url.searchParams.set('redirect_uri', location.href);
    return url.toString();
  }
}
