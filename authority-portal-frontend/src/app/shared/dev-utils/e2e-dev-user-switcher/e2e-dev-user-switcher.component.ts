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
import {Store} from '@ngxs/store';
import {
  RefreshUserInfo,
  SwitchE2eDevUser,
} from 'src/app/core/global-state/global-state-actions';
import {RouteConfigService} from 'src/app/core/global-state/routes/route-config-service';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {E2eDevUser} from './e2e-dev-user';
import {E2E_DEV_USERS} from './e2e-dev-users';

@Component({
  selector: 'app-e2e-dev-user-switcher',
  templateUrl: './e2e-dev-user-switcher.component.html',
})
export class E2EDevUserSwitcherComponent {
  minimize = true;

  // another option would be state
  currentUser = E2E_DEV_USERS[0].user;

  users = E2E_DEV_USERS;

  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    public store: Store,
    private routeConfig: RouteConfigService,
  ) {}

  setUser(user: E2eDevUser) {
    this.store.dispatch(new SwitchE2eDevUser(user));
    this.currentUser = user.user;
    this.store.dispatch(RefreshUserInfo);
    this.routeConfig.forceRefreshCurrentRoute();
  }

  hide() {
    this.minimize = !this.minimize;
  }
}
