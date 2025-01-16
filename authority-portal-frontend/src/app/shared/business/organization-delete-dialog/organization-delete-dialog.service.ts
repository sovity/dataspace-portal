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
import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {filter} from 'rxjs/operators';
import {showDialogUntil} from 'src/app/core/utils/mat-dialog-utils';
import {OrganizationDeleteDialogComponent} from './organization-delete-dialog.component';
import {OrganizationDeleteDialog} from './organization-delete-dialog.model';

@Injectable()
export class OrganizationDeleteDialogService {
  constructor(private matDialog: MatDialog) {}

  showDeleteOrganizationModal(
    data: OrganizationDeleteDialog,
    until$: Observable<any>,
  ): Observable<true> {
    return showDialogUntil<OrganizationDeleteDialog, boolean>(
      this.matDialog,
      OrganizationDeleteDialogComponent,
      {data, panelClass: 'w-[30rem]'},
      until$,
    ).pipe(filter((it) => !!it)) as Observable<true>;
  }
}
