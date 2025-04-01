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
import {ActivatedRoute} from '@angular/router';
import {Subject, takeUntil} from 'rxjs';
import {take} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {AuthorityOrganizationEditForm} from '../authority-organization-edit-form/authority-organization-edit-form';
import {
  Reset,
  SetOrganizationId,
  Submit,
} from '../state/authority-organization-edit-page-action';
import {
  AuthorityOrganizationEditPageState,
  DEFAULT_AUTHORITY_ORGANIZATION_EDIT_PAGE_STATE,
} from '../state/authority-organization-edit-page-state';
import {AuthorityOrganizationEditPageStateImpl} from '../state/authority-organization-edit-page-state-impl';

@Component({
  selector: 'app-control-center-organization-edit-page',
  templateUrl: './authority-organization-edit-page.component.html',
})
export class AuthorityOrganizationEditPageComponent
  implements OnInit, OnDestroy
{
  form: AuthorityOrganizationEditForm | null = null;
  state: AuthorityOrganizationEditPageState =
    DEFAULT_AUTHORITY_ORGANIZATION_EDIT_PAGE_STATE;

  constructor(private store: Store, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.params.pipe(take(1)).subscribe((params) => {
      this.setOrganizationId(params.organizationId);
      this.reset();
      this.startListeningToState();
    });
  }

  setOrganizationId(organizationId: string) {
    this.store.dispatch(new SetOrganizationId(organizationId));
  }

  reset(): void {
    this.store.dispatch(new Reset((form) => (this.form = form)));
  }

  startListeningToState(): void {
    this.store
      .select<AuthorityOrganizationEditPageState>(
        AuthorityOrganizationEditPageStateImpl,
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
