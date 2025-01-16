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
import {OrganizationEditFormModel} from '../../../shared/business/organization-edit-form/organization-edit-form-model';
import {AuthorityOrganizationEditFormModel} from './authority-organization-edit-form-model';

@Component({
  selector: 'app-authority-organization-edit-form',
  templateUrl: './authority-organization-edit-form.component.html',
})
export class AuthorityOrganizationEditFormComponent implements OnDestroy {
  @Input()
  authorityOrgForm!: FormGroup<AuthorityOrganizationEditFormModel>;

  idTypes = LEGAL_ID_TYPES;

  get orgForm(): FormGroup<OrganizationEditFormModel> {
    // this only requires a cast because A extends B does not imply T<A> extend T<B>
    return this
      .authorityOrgForm as unknown as FormGroup<OrganizationEditFormModel>;
  }

  ngOnDestroy$ = new Subject();
  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
