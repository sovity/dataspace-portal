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
import {Inject, Injectable} from '@angular/core';
import {Store} from '@ngxs/store';
import {
  AuthorityPortalClient,
  buildAuthorityPortalClient,
} from '@sovity.de/authority-portal-client';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {GlobalState} from '../global-state/global-state';
import {GlobalStateImpl} from '../global-state/global-state-impl';
import {AUTHORITY_PORTAL_FAKE_BACKEND} from './fake-backend/fake-backend';

@Injectable()
export class ApiClientFactory {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private store: Store,
  ) {}

  newAuthorityPortalClient(): AuthorityPortalClient {
    return buildAuthorityPortalClient({
      backendUrl: this.config.backendUrl,
      configOverrides: {
        // Required for Local Dev with Fake Backend
        fetchApi: this.config.useFakeBackend
          ? AUTHORITY_PORTAL_FAKE_BACKEND
          : undefined,

        // Required for Local E2E Dev with Quarkus Backend
        headers: this.buildHeaders(),
      },
    });
  }

  private buildHeaders(): Record<string, string> {
    const globalState = this.store.selectSnapshot<GlobalState>(GlobalStateImpl);
    if (!globalState.e2eDevUser) {
      return {};
    }

    // Local Dev Only: Add Basic Auth Header
    const credentials = [
      globalState.e2eDevUser!.user,
      globalState.e2eDevUser!.password,
    ].join(':');

    return {
      Authorization: `Basic ${btoa(credentials)}`,
    };
  }
}
