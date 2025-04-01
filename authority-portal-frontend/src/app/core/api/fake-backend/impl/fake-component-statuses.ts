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
  ComponentStatusOverview,
  UptimeStatusDto,
} from '@sovity.de/authority-portal-client';

const up = (): UptimeStatusDto => ({
  componentStatus: 'UP',
  uptimePercentage: 99.1,
  timeSpanSeconds: 2592000,
  upSinceSeconds: 2592000,
});

const maintenance = (): UptimeStatusDto => ({
  componentStatus: 'MAINTENANCE',
  uptimePercentage: 69.9,
  timeSpanSeconds: 2592000,
  upSinceSeconds: 0,
});

const down = (): UptimeStatusDto => ({
  componentStatus: 'DOWN',
  uptimePercentage: 51.2,
  timeSpanSeconds: 2592000,
  upSinceSeconds: 0,
});

export const getComponentStatus = (
  environmentId: string,
): ComponentStatusOverview => {
  if (environmentId === 'development') {
    return {
      dapsStatus: maintenance(),
      loggingHouseStatus: down(),
      onlineConnectors: 20,
      disturbedConnectors: 2,
      offlineConnectors: 1,
    };
  } else if (environmentId === 'staging') {
    return {
      dapsStatus: undefined,
      loggingHouseStatus: undefined,
      onlineConnectors: 0,
      disturbedConnectors: 0,
      offlineConnectors: 0,
    };
  } else {
    return {
      dapsStatus: up(),
      loggingHouseStatus: up(),
      onlineConnectors: 1,
      disturbedConnectors: 0,
      offlineConnectors: 0,
    };
  }
};
