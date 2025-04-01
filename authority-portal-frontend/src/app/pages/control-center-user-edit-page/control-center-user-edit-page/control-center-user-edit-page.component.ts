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
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {Reset, Submit} from '../state/control-center-user-edit-page-action';
import {
  ControlCenterUserEditPageState,
  DEFAULT_CONTROL_CENTER_USER_EDIT_PAGE_STATE,
} from '../state/control-center-user-edit-page-state';
import {ControlCenterUserEditPageStateImpl} from '../state/control-center-user-edit-page-state-impl';
import {ControlCenterUserEditPageForm} from './control-center-user-edit-page.form';

@Component({
  selector: 'app-control-center-user-edit-page',
  templateUrl: './control-center-user-edit-page.component.html',
})
export class ControlCenterUserEditPageComponent implements OnInit, OnDestroy {
  form: ControlCenterUserEditPageForm | null = null;
  state: ControlCenterUserEditPageState =
    DEFAULT_CONTROL_CENTER_USER_EDIT_PAGE_STATE;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.reset();
    this.startListeningToState();
  }

  reset(): void {
    this.store.dispatch(new Reset((form) => (this.form = form)));
  }

  startListeningToState(): void {
    this.store
      .select<ControlCenterUserEditPageState>(
        ControlCenterUserEditPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  ngOnDestroy$ = new Subject();
  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  onSubmitClick(): void {
    this.store.dispatch(new Submit(this.form!.value));
  }
}
