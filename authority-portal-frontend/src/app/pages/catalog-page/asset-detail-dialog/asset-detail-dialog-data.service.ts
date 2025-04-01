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
import {DataOfferDetailPageResult} from '@sovity.de/authority-portal-client';
import {AssetDetailDialogData} from './asset-detail-dialog-data';
import {AssetPropertyGridGroupBuilder} from './asset-property-grid-group-builder';

@Injectable()
export class AssetDetailDialogDataService {
  constructor(
    private assetPropertyGridGroupBuilder: AssetPropertyGridGroupBuilder,
  ) {}

  dataOfferDetailPage(
    dataOffer: DataOfferDetailPageResult,
  ): AssetDetailDialogData {
    const propertyGridGroups = [
      this.assetPropertyGridGroupBuilder.buildDataOfferGroup(dataOffer),
      this.assetPropertyGridGroupBuilder.buildAssetPropertiesGroup(
        dataOffer,
        'Asset',
      ),
      ...this.assetPropertyGridGroupBuilder.buildAdditionalPropertiesGroups(
        dataOffer,
      ),
      ...(this.isOnRequestAsset(dataOffer)
        ? []
        : dataOffer.contractOffers.map((contractOffer, i) =>
            this.assetPropertyGridGroupBuilder.buildContractOfferGroup(
              dataOffer,
              contractOffer,
              i,
              dataOffer.contractOffers.length,
            ),
          )),
      ...this.assetPropertyGridGroupBuilder.buildOnRequestContactInformation(
        dataOffer,
      ),
    ].filter((it) => it.properties.length);

    return {
      dataOffer,
      propertyGridGroups,
    };
  }

  private isOnRequestAsset(dataOffer: DataOfferDetailPageResult): boolean {
    return dataOffer.asset.dataSourceAvailability === 'ON_REQUEST';
  }
}
