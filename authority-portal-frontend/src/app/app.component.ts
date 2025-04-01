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
import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject, interval} from 'rxjs';
import {Store} from '@ngxs/store';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {GlobalState} from './core/global-state/global-state';
import {RefreshUserInfo} from './core/global-state/global-state-actions';
import {GlobalStateImpl} from './core/global-state/global-state-impl';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit, OnDestroy {
  state!: GlobalState;
  favicon: HTMLLinkElement | null = document.querySelector("link[rel*='icon']");

  private ngOnDestroy$ = new Subject();

  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private store: Store,
  ) {}

  ngOnInit() {
    this.startListeningToGlobalState();
    this.startPollingUserInfo();
    this.setTheme();
  }

  private setTheme() {
    if (!this.favicon) {
      this.favicon = document.createElement('link');
      this.favicon.rel = 'icon';
      document.head.appendChild(this.favicon);
    }
    this.favicon.href = this.appConfig.brandFaviconSrc;
    window.document.body.classList.add(this.appConfig.theme);
  }

  private startListeningToGlobalState() {
    this.store.select<GlobalState>(GlobalStateImpl).subscribe((state) => {
      this.state = state;
    });
  }

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  private startPollingUserInfo() {
    this.store.dispatch(RefreshUserInfo);
    interval(3000).subscribe(() => this.store.dispatch(RefreshUserInfo));
  }
}
