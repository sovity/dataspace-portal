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
import {OrganizationEditFormModel} from './organization-edit-form-model';

@Component({
  selector: 'app-organization-edit-form',
  templateUrl: './organization-edit-form.component.html',
})
export class OrganizationEditFormComponent implements OnDestroy {
  @Input()
  orgForm!: FormGroup<OrganizationEditFormModel>;

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
