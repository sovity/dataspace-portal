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
