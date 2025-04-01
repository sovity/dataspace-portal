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
import {UiProfile} from './ui-profile';
import {UiProfileConfig} from './ui-profile-config';
import {UI_PROFILE_DATA} from './ui-profile-data';

/**
 * Find profile (or default to first)
 * @param profile profile
 */
export function getProfileOrFallback(profile?: string | null): {
  profile: UiProfile;
  profileConfig: UiProfileConfig;
} {
  if (UI_PROFILE_DATA[profile as UiProfile]) {
    return {
      profile: profile as UiProfile,
      profileConfig: UI_PROFILE_DATA[profile as UiProfile],
    };
  }

  const fallback: UiProfile = 'sovity-open-source';

  const availableProfiles = Object.keys(UI_PROFILE_DATA)
    .map((s) => `"${s}"`)
    .join(', ');

  console.error(
    `Invalid profile: ${JSON.stringify(profile)}.`,
    `Expected one of ${availableProfiles}.`,
    `Falling back to ${JSON.stringify(fallback)}'.`,
  );

  return {
    profile: fallback,
    profileConfig: UI_PROFILE_DATA[fallback],
  };
}
