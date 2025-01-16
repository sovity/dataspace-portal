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
import {Component, Input, OnDestroy} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Subject} from 'rxjs';
import {LEGAL_ID_TYPES} from '../../../shared/business/organization-create-form/organization-create-form.component';
import {AuthorityOrganizationEditFormModel} from './authority-organization-edit-form-model';

@Component({
  selector: 'app-authority-organization-edit-form',
  templateUrl: './authority-organization-edit-form.component.html',
})
export class AuthorityOrganizationEditFormComponent implements OnDestroy {
  @Input()
  orgForm!: FormGroup<AuthorityOrganizationEditFormModel>;

  idTypes = LEGAL_ID_TYPES;

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
