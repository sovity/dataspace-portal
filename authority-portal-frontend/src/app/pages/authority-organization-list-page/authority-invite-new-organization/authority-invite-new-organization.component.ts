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
import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {InviteOrganizationRequest} from '@sovity.de/authority-portal-client';
import {notBlankValidator} from 'src/app/core/utils/validators/not-blank-validator';
import {phoneNumberValidator} from 'src/app/core/utils/validators/phone-number-validator';
import {
  AuthorityInviteNewOrganizationPageFormModel,
  AuthorityInviteNewOrganizationPageFormValue,
  DEFAULT_AUTHORITY_INVITE_NEW_ORGANIZATION_FORM_VALUE,
} from './authority-invite-new-organization.model';
import {
  InviteNewOrganization,
  Reset,
} from './state/authority-invite-new-organization-page-actions';
import {
  AuthorityInviteNewOrganizationPageState,
  DEFAULT_AUTHORITY_INVITE_NEW_ORGANIZATION_PAGE_STATE,
} from './state/authority-invite-new-organization-page-state';
import {AuthorityInviteNewOrganizationPageStateImpl} from './state/authority-invite-new-organization-page-state-impl';

@Component({
  selector: 'app-authority-invite-new-organization',
  templateUrl: './authority-invite-new-organization.component.html',
})
export class AuthorityInviteNewOrganizationComponent {
  state = DEFAULT_AUTHORITY_INVITE_NEW_ORGANIZATION_PAGE_STATE;
  group = this.buildFormGroup();

  ngOnDestroy$ = new Subject();

  constructor(
    private store: Store,
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<AuthorityInviteNewOrganizationComponent>,
  ) {}

  get loading(): boolean {
    return this.state.state === 'submitting';
  }

  get value(): AuthorityInviteNewOrganizationPageFormValue {
    return this.group.value as AuthorityInviteNewOrganizationPageFormValue;
  }

  ngOnInit(): void {
    this.startListeningToState();
    this.store.dispatch(Reset);
  }

  buildFormGroup(): FormGroup<AuthorityInviteNewOrganizationPageFormModel> {
    const initial = DEFAULT_AUTHORITY_INVITE_NEW_ORGANIZATION_FORM_VALUE;
    return this.formBuilder.nonNullable.group({
      userFirstName: [
        initial.userFirstName,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      userLastName: [
        initial.userLastName,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      userEmail: [
        initial.userEmail,
        [Validators.required, Validators.maxLength(128), Validators.email],
      ],
      orgName: [
        initial.orgName,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      userJobTitle: [initial.userJobTitle],
      userPhoneNumber: [
        initial.userPhoneNumber,
        [phoneNumberValidator, Validators.maxLength(28)],
      ],
    });
  }

  startListeningToState() {
    this.store
      .select<AuthorityInviteNewOrganizationPageState>(
        AuthorityInviteNewOrganizationPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  submit(): void {
    let formValue: AuthorityInviteNewOrganizationPageFormValue = this.value;
    let request: InviteOrganizationRequest = {
      userFirstName: formValue.userFirstName,
      userLastName: formValue.userLastName,
      userEmail: formValue.userEmail,
      orgName: formValue.orgName,
      userJobTitle: formValue.userJobTitle,
      userPhoneNumber: formValue.userPhoneNumber,
    };
    this.store.dispatch(
      new InviteNewOrganization(
        request,
        () => this.group.enable(),
        () => this.group.disable(),
      ),
    );
    this.dialogRef.close();
  }

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
