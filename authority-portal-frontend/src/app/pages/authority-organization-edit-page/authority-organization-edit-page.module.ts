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
import {SharedModule} from 'src/app/shared/shared.module';
import {AuthorityOrganizationEditFormComponent} from './authority-organization-edit-form/authority-organization-edit-form.component';
import {AuthorityOrganizationEditPageComponent} from './authority-organization-edit-page/authority-organization-edit-page.component';
import {AuthorityOrganizationEditPageStateImpl} from './state/authority-organization-edit-page-state-impl';

@NgModule({
  declarations: [
    AuthorityOrganizationEditFormComponent,
    AuthorityOrganizationEditPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,

    // NGXS
    NgxsModule.forFeature([AuthorityOrganizationEditPageStateImpl]),

    SharedModule,
  ],
})
export class AuthorityOrganizationEditPageModule {}
