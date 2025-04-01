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
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Subject, takeUntil} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {OrganizationDeletionCheck} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {ErrorService} from 'src/app/core/services/error.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {OrganizationDeleteDialog} from './organization-delete-dialog.model';

@Component({
  selector: 'app-organization-delete-dialog',
  templateUrl: './organization-delete-dialog.component.html',
})
export class OrganizationDeleteDialogComponent implements OnInit, OnDestroy {
  modalData: Fetched<OrganizationDeletionCheck> = Fetched.empty();
  isBusy = false;

  constructor(
    private apiService: ApiService,
    private errorService: ErrorService,
    private dialogRef: MatDialogRef<OrganizationDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OrganizationDeleteDialog,
  ) {}

  ngOnInit() {
    this.apiService
      .checkOrganizationDeletion(this.data.organizationId)
      .pipe(
        takeUntil(this.ngOnDestroy$),
        Fetched.wrap({
          failureMessage: 'Failed fetching organization deletion check.',
        }),
      )
      .subscribe((modalData) => {
        this.modalData = modalData;
      });
  }

  onDismiss() {
    this.dialogRef.close(null);
  }

  onConfirmDeleteOrganizationClick() {
    this.isBusy = true;
    this.apiService
      .deleteOrganization(this.data.organizationId)
      .pipe(
        finalize(() => (this.isBusy = false)),
        this.errorService.toastFailureRxjs('Failed deleting organization'),
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
