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
