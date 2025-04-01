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
import {
  InviteParticipantUserRequest,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {
  getParticipantRoles,
  mapRolesToReadableFormat,
} from 'src/app/core/utils/user-role-utils';
import {notBlankValidator} from 'src/app/core/utils/validators/not-blank-validator';
import {
  DEFAULT_PARTICIPANT_INVITE_NEW_USER_FORM_VALUE,
  ParticipantInviteNewUserPageFormModel,
  ParticipantInviteNewUserPageFormValue,
} from './participant-invite-new-user.model';
import {InviteNewUser} from './state/participant-invite-new-user-page-actions';
import {
  DEFAULT_PARTICIPANT_INVITE_NEW_USER_PAGE_STATE,
  ParticipantInviteNewUserPageState,
} from './state/participant-invite-new-user-page-state';
import {ParticipantInviteNewUserPageStateImpl} from './state/participant-invite-new-user-page-state-impl';

@Component({
  selector: 'app-participant-invite-new-user',
  templateUrl: './participant-invite-new-user.component.html',
})
export class ParticipantInviteNewUserComponent {
  state = DEFAULT_PARTICIPANT_INVITE_NEW_USER_PAGE_STATE;
  group = this.buildFormGroup();
  assignableRoles: string[] = getParticipantRoles();

  ngOnDestroy$ = new Subject();

  constructor(
    private store: Store,
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<ParticipantInviteNewUserComponent>,
  ) {}

  get value(): ParticipantInviteNewUserPageFormValue {
    return this.group.value as ParticipantInviteNewUserPageFormValue;
  }

  ngOnInit(): void {
    this.startListeningToState();
  }

  buildFormGroup(): FormGroup<ParticipantInviteNewUserPageFormModel> {
    const initial = DEFAULT_PARTICIPANT_INVITE_NEW_USER_FORM_VALUE;
    return this.formBuilder.nonNullable.group({
      firstName: [
        initial.firstName,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      lastName: [
        initial.lastName,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
      email: [
        initial.email,
        [Validators.required, Validators.maxLength(128), Validators.email],
      ],
      role: [
        initial.role,
        [Validators.required, Validators.maxLength(128), notBlankValidator()],
      ],
    });
  }

  startListeningToState() {
    this.store
      .select<ParticipantInviteNewUserPageState>(
        ParticipantInviteNewUserPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  mapToReadable(role: string): string {
    return mapRolesToReadableFormat(role);
  }

  submit(): void {
    let formValue: ParticipantInviteNewUserPageFormValue = this.value;
    let request: InviteParticipantUserRequest = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      email: formValue.email,
      role: formValue.role as UserRoleDto,
    };
    this.store.dispatch(
      new InviteNewUser(
        request,
        () => this.group.enable(),
        () => this.group.disable(),
        () => this.dialogRef.close(true),
      ),
    );
  }

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
