/*
 * Copyright (c) 2024 sovity GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      sovity GmbH - initial implementation
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
