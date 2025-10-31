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
import {trimIndent} from '../../utils/string-utils';

export interface EdcConfig {
  defaultPaths: {
    dspApi: string;
    jwksUrl: string;
    managementApi: string;
  };

  dockerImage: string;
  deploymentGuideUrl: string;
  configurationUrl: string;
}

const version = '16.0.0';

export const EDC_CONFIG: EdcConfig = {
  defaultPaths: {
    dspApi: '/api/v1/dsp',
    jwksUrl: '/api/v1/dsp/jwks',
    managementApi: '/api/management',
  },

  dockerImage: `ghcr.io/sovity/edc-ce:${version}`,
  deploymentGuideUrl: `https://github.com/sovity/edc-ce/blob/v${version}/docs/deployment-guide/goals/production-ce/cp-with-integrated-dp.md`,
  configurationUrl: `https://github.com/sovity/edc-ce/blob/v${version}/docs/deployment-guide/config/connector-ce/README.md`,
};

export const generateConnectorConfigShort = (opts: {
  participantId: string;
  dapsTokenUrl: string;
  dapsJwksUrl: string;
}) =>
  trimIndent(`
      # Full configuration reference:
      # ${EDC_CONFIG.configurationUrl}

      # --- Dataspace Roll-In ---
      "edc.participant.id": '${opts.participantId}'
      "edc.oauth.token.url": '${opts.dapsTokenUrl}'
      "edc.oauth.provider.jwks.url": '${opts.dapsJwksUrl}'
  `);

export const generateConnectorConfig = (opts: {
  connectorBaseUrl: string;
  participantId: string;
  certificate: string;
  privateKey: string;
  dapsTokenUrl: string;
  dapsJwksUrl: string;
}): string => {
  return trimIndent(
    `
        # Full configuration reference:
        # ${EDC_CONFIG.configurationUrl}

        # --- Dataspace Roll-In ---
        "edc.participant.id": '${opts.participantId}'
        "edc.oauth.token.url": '${opts.dapsTokenUrl}'
        "edc.oauth.provider.jwks.url": '${opts.dapsJwksUrl}'
        "sovity.vault.kind": 'vault-in-memory'
        "sovity.vault.in-memory.init.daps-cert": |
          ${opts.certificate.split('\n').join('\n          ')}
        "sovity.vault.in-memory.init.daps-priv": |
          ${opts.privateKey.split('\n').join('\n          ')}

        # --- Connector Setup --
        "sovity.deployment.kind": 'control-plane-with-integrated-data-plane'
        "sovity.edc.fqdn.public": '${getFqdnFromUrl(opts.connectorBaseUrl)}'
        "sovity.edc.fqdn.internal": 'localhost'
        "sovity.jdbc.url": 'jdbc://...'
        "sovity.jdbc.password": '...'
        "sovity.jdbc.user": '...'

        # --- Connector Metadata ---
        "sovity.edc.title": 'Your Connector'
        "sovity.edc.description": 'Your Self-Hosted EDC Connector'
        "sovity.edc.curator.name": 'Your Organization Name'
        "sovity.edc.curator.url": 'https://your-organization-url.com'
        "sovity.edc.maintainer.name": 'Your Maintainer Organization Name'
        "sovity.edc.maintainer.url": 'https://your-maintainer-organization-url.com'
      `,
  );
};

const getFqdnFromUrl = (url: string): string => {
  try {
    return new URL(url).hostname;
  } catch (error) {
    return '<YOUR FULLY QUALIFIED DOMAIN NAME>';
  }
};
