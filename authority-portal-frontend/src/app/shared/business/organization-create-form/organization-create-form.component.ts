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
import {OrganizationCreateFormModel} from './organization-create-form-model';

export const LEGAL_ID_TYPES: {value: string; viewValue: string}[] = [
  {value: 'TAX_ID', viewValue: 'Tax ID'},
  {
    value: 'COMMERCE_REGISTER_INFO',
    viewValue: 'Commerce Register Number',
  },
];

@Component({
  selector: 'app-organization-create-form',
  templateUrl: './organization-create-form.component.html',
})
export class OrganizationCreateFormComponent implements OnDestroy {
  @Input()
  orgForm!: FormGroup<OrganizationCreateFormModel>;

  idTypes = LEGAL_ID_TYPES;

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
