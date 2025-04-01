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
import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Subject, takeUntil} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {UserDeletionCheck} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {ErrorService} from 'src/app/core/services/error.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {UserDeleteDialog} from './user-delete-dialog.model';

@Component({
  selector: 'app-user-delete-dialog',
  templateUrl: './user-delete-dialog.component.html',
})
export class UserDeleteDialogComponent implements OnInit, OnDestroy {
  modalData: Fetched<UserDeletionCheck> = Fetched.empty();
  deleteOrganizationCreatorForm = this.formBuilder.nonNullable.group({
    successor: ['', Validators.required],
  });
  isBusy = false;

  constructor(
    private apiService: ApiService,
    private errorService: ErrorService,
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<UserDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserDeleteDialog,
  ) {}

  ngOnInit() {
    this.apiService
      .checkUserDeletion(this.data.userId)
      .pipe(
        takeUntil(this.ngOnDestroy$),
        Fetched.wrap({failureMessage: 'Failed fetching user deletion check.'}),
      )
      .subscribe((modalData) => {
        this.modalData = modalData;
      });
  }

  onDismiss() {
    this.dialogRef.close(null);
  }

  get isLastParticipantAdmin() {
    return this.modalData?.dataOrUndefined?.isLastParticipantAdmin ?? false;
  }

  get isOrganizationCreator() {
    return this.modalData?.dataOrUndefined?.isOrganizationCreator ?? false;
  }

  onConfirmDeleteUserClick() {
    const successorId = this.deleteOrganizationCreatorForm.value.successor;
    this.isBusy = true;
    this.apiService
      .deleteUser(this.data.userId, successorId)
      .pipe(
        finalize(() => (this.isBusy = false)),
        this.errorService.toastFailureRxjs('Failed deleting user'),
      )
      .subscribe(() => {
        this.dialogRef.close(true);
        this.data.onDeleteSuccess(this.modalData!.data);
      });
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
