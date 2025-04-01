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
