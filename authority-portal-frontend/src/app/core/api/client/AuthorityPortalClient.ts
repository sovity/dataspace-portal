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
import {
  CatalogApi,
  Configuration,
  ConfigurationParameters,
  UiApi,
} from './generated';

/**
 * API Client for our sovity Authority Portal Client
 */
export interface AuthorityPortalClient {
  uiApi: UiApi;
  catalogApi: CatalogApi;
}

/**
 * Configure & Build new Authority Portal Client
 * @param opts opts
 */
export function buildAuthorityPortalClient(
  opts: AuthorityPortalClientOptions,
): AuthorityPortalClient {
  const config = new Configuration({
    basePath: opts.backendUrl,
    credentials: 'same-origin',
    ...opts.configOverrides,
  });

  return {
    uiApi: new UiApi(config),
    catalogApi: new CatalogApi(config),
  };
}

/**
 * Options for instantiating an Authority Portal API Client
 */
export interface AuthorityPortalClientOptions {
  backendUrl: string;
  configOverrides?: Partial<ConfigurationParameters>;
}
