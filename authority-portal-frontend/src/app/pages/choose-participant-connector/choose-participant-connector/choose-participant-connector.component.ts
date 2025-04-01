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
import {Component, Inject} from '@angular/core';
import {SelectionBoxModel} from 'src/app/shared/common/selection-box/selection-box.model';
import {APP_CONFIG, AppConfig} from '../../../core/services/config/app-config';

@Component({
  selector: 'app-choose-participant-connector',
  templateUrl: './choose-participant-connector.component.html',
})
export class ChooseParticipantConnectorComponent {
  constructor(@Inject(APP_CONFIG) public appConfig: AppConfig) {}

  selectionBoxes: SelectionBoxModel[] = [
    {
      title: 'I have a connector',
      subTitle: 'Follow the process to set-up your self-hosted connector here',
      icon: this.appConfig.connectorSelfOwnedIconSrc,
      action: {
        url: '/my-organization/connectors/new/self-hosted',
      },
    },
    {
      title: 'I need a connector',
      subTitle:
        'Request a managed connector to begin your journey in data spaces',
      icon: this.appConfig.connectorCaasIconSrc,
      action: {
        url: '/my-organization/connectors/new/choose-provider',
      },
    },
  ];
}
