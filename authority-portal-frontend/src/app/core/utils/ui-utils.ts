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
  OrganizationRegistrationStatusDto,
  UserRegistrationStatusDto,
} from '@sovity.de/authority-portal-client';

export const getOrganizationRegistrationStatusClasses = (
  status: OrganizationRegistrationStatusDto,
): string => {
  switch (status) {
    case OrganizationRegistrationStatusDto.Active:
      return 'text-emerald-700 bg-emerald-100/60';
    case OrganizationRegistrationStatusDto.Rejected:
      return 'text-red-700 bg-red-100/60';
    case OrganizationRegistrationStatusDto.Pending:
      return 'bg-gray-100/90';
    case OrganizationRegistrationStatusDto.Invited:
      return 'bg-gray-100/90';
    case OrganizationRegistrationStatusDto.Onboarding:
      return 'text-orange-700 bg-orange-100/60';
    default:
      return '';
  }
};

export const getOrganizationUserRegistrationStatusClasses = (
  status: UserRegistrationStatusDto,
): string => {
  switch (status) {
    case UserRegistrationStatusDto.Active:
      return 'text-emerald-700 bg-emerald-100/60';
    case UserRegistrationStatusDto.Rejected:
      return 'text-red-700 bg-red-100/60';
    case UserRegistrationStatusDto.Pending:
      return 'bg-gray-100/90';
    case UserRegistrationStatusDto.Invited:
      return 'bg-gray-100/90';
    default:
      return '';
  }
};

export const getConnectorsTypeClasses = (status: ConnectorTypeDto): string => {
  switch (status) {
    case ConnectorTypeDto.Own:
      return 'text-emerald-700 bg-emerald-100/60';
    case ConnectorTypeDto.Provided:
      return 'text-blue-700 bg-blue-100/60';
    case ConnectorTypeDto.Caas:
      return 'bg-yellow-300/90';
    case ConnectorTypeDto.Configuring:
      return 'bg-red-300/90';
    default:
      return '';
  }
};

export const getConnectorStatusOuterRingClasses = (
  status: ConnectorStatusDto,
): string => {
  switch (status) {
    case ConnectorStatusDto.Online:
    case ConnectorStatusDto.Running:
      return 'bg-emerald-500/20';
    case ConnectorStatusDto.Init:
    case ConnectorStatusDto.Provisioning:
    case ConnectorStatusDto.AwaitingRunning:
    case ConnectorStatusDto.Deprovisioning:
    case ConnectorStatusDto.AwaitingStopped:
      return 'bg-amber-500/20';
    case ConnectorStatusDto.Offline:
    case ConnectorStatusDto.Stopped:
    case ConnectorStatusDto.Error:
    case ConnectorStatusDto.NotFound:
    case ConnectorStatusDto.Dead:
    case ConnectorStatusDto.Unknown:
      return 'bg-red-500/20';
  }
};

export const getConnectorStatusInnerCircleClasses = (
  status: ConnectorStatusDto,
): string => {
  switch (status) {
    case ConnectorStatusDto.Online:
    case ConnectorStatusDto.Running:
      return 'bg-emerald-500';
    case ConnectorStatusDto.Init:
    case ConnectorStatusDto.Provisioning:
    case ConnectorStatusDto.AwaitingRunning:
    case ConnectorStatusDto.Deprovisioning:
    case ConnectorStatusDto.AwaitingStopped:
      return 'bg-amber-500';
    case ConnectorStatusDto.Offline:
    case ConnectorStatusDto.Stopped:
    case ConnectorStatusDto.Error:
    case ConnectorStatusDto.NotFound:
    case ConnectorStatusDto.Dead:
    case ConnectorStatusDto.Unknown:
      return 'bg-red-500';
  }
};
