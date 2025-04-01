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
import {DOCUMENT} from '@angular/common';
import {Component, Inject, OnDestroy} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {Observable, Subject, isObservable} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {
  DataOfferDetailPageResult,
  UiAsset,
} from '@sovity.de/authority-portal-client';
import {MailtoLinkBuilder} from 'src/app/core/services/mailto-link-builder';
import {ToastService} from '../../../shared/common/toast-notifications/toast.service';
import {PropertyGridGroup} from '../property-grid-group/property-grid-group';
import {AssetDetailDialogData} from './asset-detail-dialog-data';

/**
 * Asset Detail Dialog
 * Contract Agreement Detail Dialog
 * Contract Offer Detail Dialog
 * <p>
 * All in one! If that's a good idea remains to be seen.
 */
@Component({
  selector: 'asset-detail-dialog',
  templateUrl: './asset-detail-dialog.component.html',
  styleUrls: ['./asset-detail-dialog.component.scss'],
})
export class AssetDetailDialogComponent implements OnDestroy {
  data!: AssetDetailDialogData;
  asset!: UiAsset;
  propGroups!: PropertyGridGroup[];
  dataOffer!: DataOfferDetailPageResult;

  loading = false;

  get isOnRequestDataOffer(): boolean {
    return this.data.dataOffer.asset.dataSourceAvailability === 'ON_REQUEST';
  }

  constructor(
    @Inject(MAT_DIALOG_DATA)
    private _data: AssetDetailDialogData | Observable<AssetDetailDialogData>,
    private mailtoLinkBuilder: MailtoLinkBuilder,
    private toastService: ToastService,
    @Inject(DOCUMENT) private document: Document,
  ) {
    if (isObservable(this._data)) {
      this._data
        .pipe(takeUntil(this.ngOnDestroy$))
        .subscribe((data) => this.setData(data));
    } else {
      this.setData(this._data);
    }
  }

  setData(data: AssetDetailDialogData) {
    this.data = data;
    this.asset = this.data.dataOffer.asset;
    this.dataOffer = this.data.dataOffer;
    this.propGroups = this.data.propertyGridGroups;
  }

  copyUrl() {
    navigator.clipboard
      .writeText(window.location.href)
      .then(() => {
        this.toastService.showSuccess('Data Offer URL copied to clipboard');
      })
      .catch(() => {
        this.toastService.showSuccess(
          'Failed to copy Data Offer URL to clipboard. Check if your browser permissions allow this action.',
        );
      });
  }

  onContactClick() {
    if (!this.asset.onRequestContactEmail) {
      throw new Error('On request asset must have contact email');
    }

    const url = this.mailtoLinkBuilder.buildMailtoUrl(
      this.asset.onRequestContactEmail,
      this.asset.onRequestContactEmailSubject ??
        "I'm interested in your data offer",
    );
    this.document.location.href = url;
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
