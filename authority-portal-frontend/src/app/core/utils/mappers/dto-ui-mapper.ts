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
import {
  ConnectorStatusDto,
  ConnectorTypeDto,
} from '@sovity.de/authority-portal-client';

export const getConnectorsTypeText = (status: ConnectorTypeDto): string => {
  switch (status) {
    case ConnectorTypeDto.Own:
      return 'OWN';
    case ConnectorTypeDto.Provided:
      return 'PROVIDED';
    case ConnectorTypeDto.Caas:
      return 'CAAS';
    case ConnectorTypeDto.Configuring:
      return 'CONFIGURING';
  }
};
export const getConnectorStatusText = (status: ConnectorStatusDto): string => {
  switch (status) {
    case ConnectorStatusDto.Init:
      return 'Init';
    case ConnectorStatusDto.Provisioning:
      return 'Provisioning';
    case ConnectorStatusDto.AwaitingRunning:
      return 'Awaiting Running';
    case ConnectorStatusDto.Running:
      return 'Running';
    case ConnectorStatusDto.Deprovisioning:
      return 'Deprovisioning';
    case ConnectorStatusDto.AwaitingStopped:
      return 'Awaiting stopped';
    case ConnectorStatusDto.Stopped:
      return 'Stopped';
    case ConnectorStatusDto.Error:
      return 'Error';
    case ConnectorStatusDto.NotFound:
      return 'Not Found';
    case ConnectorStatusDto.Online:
      return 'Online';
    case ConnectorStatusDto.Offline:
      return 'Offline';
    case ConnectorStatusDto.Dead:
      return 'Dead';
    case ConnectorStatusDto.Unknown:
      return 'Unknown';
  }
};
