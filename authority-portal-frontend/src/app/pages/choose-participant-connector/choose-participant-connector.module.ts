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
import {FormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {RouterLink} from '@angular/router';
import {SharedModule} from 'src/app/shared/shared.module';
import {ChooseParticipantConnectorComponent} from './choose-participant-connector/choose-participant-connector.component';

@NgModule({
  declarations: [ChooseParticipantConnectorComponent],
  exports: [ChooseParticipantConnectorComponent],
  imports: [
    // Anbgular
    BrowserModule,
    CommonModule,
    FormsModule,

    // Authority Portal
    SharedModule,
    RouterLink,
  ],
})
export class ChooseParticipantConnectorModule {}
