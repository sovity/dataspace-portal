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
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {NgxsModule} from '@ngxs/store';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {SharedModule} from '../../shared/shared.module';
import {AssetCardTagListComponent} from './asset-card-tag-list/asset-card-tag-list.component';
import {AssetDetailDialogDataService} from './asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogComponent} from './asset-detail-dialog/asset-detail-dialog.component';
import {AssetDetailDialogService} from './asset-detail-dialog/asset-detail-dialog.service';
import {AssetPropertyGridGroupBuilder} from './asset-detail-dialog/asset-property-grid-group-builder';
import {BrokerDataOfferCardsComponent} from './broker-data-offer-cards/broker-data-offer-cards.component';
import {BrokerDataOfferList} from './broker-data-offer-list/broker-data-offer-list.component';
import {CatalogPageComponent} from './catalog-page/catalog-page.component';
import {CatalogPageState} from './catalog-page/state/catalog-page-state';
import {FilterBoxComponent} from './filter-box/filter-box.component';
import {JsonDialogComponent} from './json-dialog/json-dialog.component';
import {JsonDialogService} from './json-dialog/json-dialog.service';
import {MarkdownDescriptionComponent} from './markdown-description/markdown-description.component';
import {PolicyExpressionComponent} from './policy-editor/renderer/policy-expression/policy-expression.component';
import {PolicyRendererComponent} from './policy-editor/renderer/policy-renderer/policy-renderer.component';
import {PropertyGridGroupComponent} from './property-grid-group/property-grid-group.component';
import {PropertyGridComponent} from './property-grid/property-grid.component';
import {TruncatedShortDescription} from './truncated-short-description/truncated-short-description.component';
import {UrlListDialogComponent} from './url-list-dialog/url-list-dialog.component';
import {UrlListDialogService} from './url-list-dialog/url-list-dialog.service';
import {ViewSelectionComponent} from './view-selection/view-selection.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // NGXS
    NgxsModule.forFeature([CatalogPageState]),

    // Feature Modules
    SharedModule,

    // Thirdparty
    NgxJsonViewerModule,
  ],
  declarations: [
    AssetCardTagListComponent,
    AssetDetailDialogComponent,
    BrokerDataOfferCardsComponent,
    BrokerDataOfferList,
    CatalogPageComponent,
    FilterBoxComponent,
    JsonDialogComponent,
    MarkdownDescriptionComponent,
    PropertyGridComponent,
    PropertyGridGroupComponent,
    TruncatedShortDescription,
    UrlListDialogComponent,
    ViewSelectionComponent,
    PolicyRendererComponent,
    PolicyExpressionComponent,
  ],
  providers: [
    AssetDetailDialogDataService,
    AssetDetailDialogService,
    AssetPropertyGridGroupBuilder,
    JsonDialogService,
    UrlListDialogService,
  ],
  exports: [CatalogPageComponent],
})
export class CatalogPageModule {}
