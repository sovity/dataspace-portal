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
import {Component, OnDestroy, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {Subject} from 'rxjs';
import {Store} from '@ngxs/store';
import {GlobalState} from 'src/app/core/global-state/global-state';
import {GlobalStateImpl} from 'src/app/core/global-state/global-state-impl';

@Component({
  selector: 'app-loading-page',
  templateUrl: './loading-page.component.html',
})
export class LoadingPageComponent implements OnInit, OnDestroy {
  state!: GlobalState;

  private ngOnDestroy$ = new Subject();

  constructor(private store: Store, private titleService: Title) {
    this.titleService.setTitle('Error');
  }

  ngOnInit() {
    this.startListeningToGlobalState();
  }

  private startListeningToGlobalState() {
    this.store
      .select<GlobalState>(GlobalStateImpl)
      .subscribe((state) => (this.state = state));
  }

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
