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
import {InjectionToken} from '@angular/core';
import {getProfileOrFallback} from './profiles/get-profile-or-fallback';
import {UiColorTheme} from './profiles/ui-color-theme';
import {UiFeature} from './profiles/ui-feature';
import {UiLogo} from './profiles/ui-logo';
import {UiProfile} from './profiles/ui-profile';

export const APP_CONFIG = new InjectionToken<AppConfig>('APP_CONFIG');

/**
 * App Config
 *
 * Contains environment-dependant config values.
 *
 * Values can be overridden via environment variables
 */
export interface AppConfig {
  // selected profile
  profile: UiProfile;
  features: Set<UiFeature>;

  // theme by profile
  theme: UiColorTheme;
  brandFaviconSrc: string;
  brandLogo: UiLogo;
  brandLogoSmall: UiLogo;
  brandLogoUnauthenticatedPage: UiLogo;
  brandLogoOnboardingPage: UiLogo;
  copyrightCompanyName: string;

  // connector registration icons
  connectorSelfOwnedIconSrc: string;
  connectorCaasIconSrc: string;
  caasResellerBrandLogoSrc: string;

  privacyPolicyUrl: string;
  legalNoticeUrl: string;
  supportUrl: string;

  backendUrl: string;
  loginUrl: string;
  logoutUrl: string;
  updatePasswordUrl: string;
  invalidateSessionCookiesUrl: string;
  useFakeBackend: boolean;
  useLocalBackend: boolean;
  brandDataspaceName: string;
  portalName: string;
  enableDashboard: boolean;
}

/**
 * Available Environment Variables
 *
 * Must be prefixed AUTHORITY_PORTAL_FRONTEND
 */
export interface AppConfigEnv {
  AUTHORITY_PORTAL_FRONTEND_BACKEND_URL: string;
  AUTHORITY_PORTAL_FRONTEND_LOGIN_URL: string;
  AUTHORITY_PORTAL_FRONTEND_LOGOUT_URL: string;
  AUTHORITY_PORTAL_FRONTEND_UPDATE_PASSWORD_URL: string;
  AUTHORITY_PORTAL_FRONTEND_USE_FAKE_BACKEND: string;
  AUTHORITY_PORTAL_FRONTEND_USE_LOCAL_BACKEND: string;
  AUTHORITY_PORTAL_FRONTEND_INVALIDATE_SESSION_COOKIES_URL: string;
  AUTHORITY_PORTAL_FRONTEND_PRIVACY_POLICY_URL: string;
  AUTHORITY_PORTAL_FRONTEND_LEGAL_NOTICE_URL: string;
  AUTHORITY_PORTAL_FRONTEND_SUPPORT_URL: string;
  AUTHORITY_PORTAL_FRONTEND_ACTIVE_PROFILE: string;
  AUTHORITY_PORTAL_FRONTEND_DATASPACE_SHORT_NAME: string;
  AUTHORITY_PORTAL_FRONTEND_PORTAL_DISPLAY_NAME: string;
  AUTHORITY_PORTAL_FRONTEND_ENABLE_DASHBOARD: string;
}

/**
 * Build {@link AppConfig} from env vars
 * @param envVars env vars as gotten from /assets/config/config.json
 */
export function buildAppConfig(envVars: AppConfigEnv): AppConfig {
  const {profile, profileConfig} = getProfileOrFallback(
    envVars.AUTHORITY_PORTAL_FRONTEND_ACTIVE_PROFILE,
  );

  return {
    profile,
    ...profileConfig,

    backendUrl: envVars.AUTHORITY_PORTAL_FRONTEND_BACKEND_URL,
    loginUrl: envVars.AUTHORITY_PORTAL_FRONTEND_LOGIN_URL,
    logoutUrl: envVars.AUTHORITY_PORTAL_FRONTEND_LOGOUT_URL,
    updatePasswordUrl: envVars.AUTHORITY_PORTAL_FRONTEND_UPDATE_PASSWORD_URL,
    invalidateSessionCookiesUrl:
      envVars.AUTHORITY_PORTAL_FRONTEND_INVALIDATE_SESSION_COOKIES_URL,
    useFakeBackend:
      envVars.AUTHORITY_PORTAL_FRONTEND_USE_FAKE_BACKEND === 'true',
    useLocalBackend:
      envVars.AUTHORITY_PORTAL_FRONTEND_USE_LOCAL_BACKEND === 'true',
    privacyPolicyUrl: envVars.AUTHORITY_PORTAL_FRONTEND_PRIVACY_POLICY_URL,
    legalNoticeUrl: envVars.AUTHORITY_PORTAL_FRONTEND_LEGAL_NOTICE_URL,
    supportUrl: envVars.AUTHORITY_PORTAL_FRONTEND_SUPPORT_URL,
    brandDataspaceName: envVars.AUTHORITY_PORTAL_FRONTEND_DATASPACE_SHORT_NAME,
    portalName: envVars.AUTHORITY_PORTAL_FRONTEND_PORTAL_DISPLAY_NAME,
    enableDashboard:
      envVars.AUTHORITY_PORTAL_FRONTEND_ENABLE_DASHBOARD === 'true',
  };
}
