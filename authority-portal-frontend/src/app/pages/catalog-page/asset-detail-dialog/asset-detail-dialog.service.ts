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
import {NEVER, Observable, takeUntil} from 'rxjs';
import {catchError, map, switchMap} from 'rxjs/operators';
import {DataOfferDetailPageQuery} from '@sovity.de/authority-portal-client';
import {CatalogApiService} from 'src/app/core/api/catalog-api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {showDialogUntil} from 'src/app/core/utils/mat-dialog-utils';
import {ErrorService} from '../../../core/services/error.service';
import {AssetDetailDialogData} from './asset-detail-dialog-data';
import {AssetDetailDialogDataService} from './asset-detail-dialog-data.service';
import {AssetDetailDialogComponent} from './asset-detail-dialog.component';
import {AssetNotAvailableDialogComponent} from './asset-not-available-dialog/asset-not-available-dialog.component';

@Injectable()
export class AssetDetailDialogService {
  constructor(
    private dialog: MatDialog,
    private globalStateUtils: GlobalStateUtils,
    private catalogApiService: CatalogApiService,
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private errorService: ErrorService,
  ) {}

  /**
   * Shows an Asset Detail Dialog until until$ emits / completes
   * @param connectorId Connector ID
   * @param assetId Asset ID
   * @param until$ observable that controls the lifetime of the dialog
   */
  open(
    assetId: string,
    connectorId: string,
    until$: Observable<any> = NEVER,
  ): Observable<undefined> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((envId) => {
        const query: DataOfferDetailPageQuery = {connectorId, assetId};
        return this.catalogApiService.dataOfferDetailPage(envId, query);
      }),
      takeUntil(until$),
      map((result) =>
        this.assetDetailDialogDataService.dataOfferDetailPage(result),
      ),
      switchMap((data) => this.openDialogWithData(data, until$)),
      catchError(() => {
        return this.openErrorDialog(until$);
      }),
    );
  }

  /**
   * Shows an Asset Detail Dialog until until$ emits / completes
   * @param data Asset Detail Dialog data, or a stream if there's a need to refresh the data
   * @param until$ observable that controls the lifetime of the dialog
   */
  private openDialogWithData(
    data: AssetDetailDialogData | Observable<AssetDetailDialogData>,
    until$: Observable<any> = NEVER,
  ): Observable<undefined> {
    return showDialogUntil(
      this.dialog,
      AssetDetailDialogComponent,
      {
        data,
        maxWidth: '1000px',
        maxHeight: '90vh',
        autoFocus: false,
      },
      until$,
    );
  }

  private openErrorDialog(
    until$: Observable<any> = NEVER,
  ): Observable<undefined> {
    return showDialogUntil(
      this.dialog,
      AssetNotAvailableDialogComponent,
      {maxWidth: '600px'},
      until$,
    );
  }
}
