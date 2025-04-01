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
import {Component, Input} from '@angular/core';
import {ConnectorStatusDto} from '@sovity.de/authority-portal-client';
import {
  getConnectorStatusInnerCircleClasses,
  getConnectorStatusOuterRingClasses,
} from 'src/app/core/utils/ui-utils';
import {getConnectorStatusText} from '../../../core/utils/mappers/dto-ui-mapper';

@Component({
  selector: 'app-connector-status-led',
  templateUrl: './connector-status-led.component.html',
})
export class ConnectorStatusLedComponent {
  @Input()
  status: ConnectorStatusDto = 'UNKNOWN';

  getConnectorStatusOuterRingClasses = getConnectorStatusOuterRingClasses;
  getConnectorStatusInnerCircleClasses = getConnectorStatusInnerCircleClasses;
  getConnectorStatusText = getConnectorStatusText;
}
