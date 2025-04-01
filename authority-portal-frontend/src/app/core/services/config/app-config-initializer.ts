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
import {Provider} from '@angular/core';
import {APP_CONFIG, AppConfig, buildAppConfig} from './app-config';

/**
 * The config is fetched before the angular project starts.
 */
let appConfig: AppConfig | null;

export const provideAppConfig = (): Provider => ({
  provide: APP_CONFIG,
  useFactory: () => appConfig,
});

export async function loadConfig() {
  return fetch('/assets/config/app-configuration.json')
    .then((response) => response.json())
    .then(buildAppConfig)
    .then((config) => {
      appConfig = config;
    });
}
