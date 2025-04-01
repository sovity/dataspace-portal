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
import {ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {NgxsModule} from '@ngxs/store';
import {SharedModule} from 'src/app/shared/shared.module';
import {CentralComponentListPageComponent} from './central-component-list-page/central-component-list-page.component';
import {DeleteIconButtonComponent} from './delete-icon-button/delete-icon-button.component';
import {CentralComponentListPageStateImpl} from './state/central-component-list-page-state-impl';

@NgModule({
  declarations: [CentralComponentListPageComponent, DeleteIconButtonComponent],
  exports: [CentralComponentListPageComponent],
  imports: [
    // Angular
    CommonModule,
    ReactiveFormsModule,
    RouterModule,

    // NGXS
    NgxsModule.forFeature([CentralComponentListPageStateImpl]),

    // Authority Portal
    SharedModule,
  ],
})
export class CentralComponentListPageModule {}
