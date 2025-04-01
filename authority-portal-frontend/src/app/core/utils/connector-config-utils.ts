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
  CreateConnectorResponse,
  DeploymentEnvironmentDto,
} from '@sovity.de/authority-portal-client';

export function buildConnectorConfigFromResponse(
  deploymentEnvironment: DeploymentEnvironmentDto,
  response: CreateConnectorResponse,
): string {
  return [
    `MY_EDC_PARTICIPANT_ID: "${response.id}"`,
    `EDC_OAUTH_CLIENT_ID: "${response.clientId}"`,
    `EDC_OAUTH_TOKEN_URL: "${deploymentEnvironment.dapsTokenUrl}"`,
    `EDC_OAUTH_PROVIDER_JWKS_URL: "${deploymentEnvironment.dapsJwksUrl}"`,
    `EDC_LOGGINGHOUSE_EXTENSION_URL: "${deploymentEnvironment.loggingHouseUrl}"`,
  ].join('\n');
}

export function buildConnectorConfigFromLocalData(
  deploymentEnvironment: DeploymentEnvironmentDto,
  connectorId: string,
  clientId: string,
): string {
  return [
    `MY_EDC_PARTICIPANT_ID: "${connectorId}"`,
    `EDC_OAUTH_CLIENT_ID: "${clientId}"`,
    `EDC_OAUTH_TOKEN_URL: "${deploymentEnvironment.dapsTokenUrl}"`,
    `EDC_OAUTH_PROVIDER_JWKS_URL: "${deploymentEnvironment.dapsJwksUrl}"`,
    `EDC_LOGGINGHOUSE_EXTENSION_URL: "${deploymentEnvironment.loggingHouseUrl}"`,
  ].join('\n');
}
