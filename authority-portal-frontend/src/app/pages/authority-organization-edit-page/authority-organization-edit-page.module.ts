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
import {RouterModule} from '@angular/router';
import {NgxsModule} from '@ngxs/store';
import {SharedModule} from 'src/app/shared/shared.module';
import {AuthorityOrganizationEditPageComponent} from './authority-organization-edit-page/authority-organization-edit-page.component';
import {AuthorityOrganizationEditPageStateImpl} from './state/authority-organization-edit-page-state-impl';

@NgModule({
  declarations: [AuthorityOrganizationEditPageComponent],
  imports: [
    CommonModule,
    RouterModule,

    // NGXS
    NgxsModule.forFeature([AuthorityOrganizationEditPageStateImpl]),

    SharedModule,
  ],
})
export class AuthorityOrganizationEditPageModule {}
