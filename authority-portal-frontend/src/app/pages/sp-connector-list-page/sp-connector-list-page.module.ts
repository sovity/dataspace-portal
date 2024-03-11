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
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {NgxsModule} from '@ngxs/store';
import {LoadingElementModule} from 'src/app/common/components/loading-element/loading-element.module';
import {PipesAndDirectivesModule} from 'src/app/common/components/pipes-and-directives/pipes-and-directives.module';
import {MaterialModule} from 'src/app/common/material/material.module';
import {SharedModule} from 'src/app/shared/shared.module';
import {DevUtilsModule} from '../../common/components/dev-utils/dev-utils.module';
import {SpConnectorDetailPageModule} from '../sp-connector-detail-page/sp-connector-detail-page.module';
import {SpConnectorListPageComponent} from './sp-connector-list-page/sp-connector-list-page.component';
import {SpConnectorListPageStateImpl} from './state/sp-connector-list-page-state-impl';

@NgModule({
  declarations: [SpConnectorListPageComponent],
  imports: [
    // Angular
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    DevUtilsModule,
    SharedModule,

    // NGXS
    NgxsModule.forFeature([SpConnectorListPageStateImpl]),

    // Authority Portal
    PipesAndDirectivesModule,
    LoadingElementModule,
    SpConnectorDetailPageModule,
  ],
})
export class SpConnectorListPageModule {}
