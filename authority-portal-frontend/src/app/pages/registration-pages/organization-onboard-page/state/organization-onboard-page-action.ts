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
  OnboardingOrganizationUpdateDto,
  OnboardingUserUpdateDto,
} from '@sovity.de/authority-portal-client';

const tag = 'OnboardingPage';

export interface OnboardingProcessRequest {
  userProfile: OnboardingUserUpdateDto;
  organizationProfile: OnboardingOrganizationUpdateDto;
}

export class OnboardingProcessFormSubmit {
  static readonly type = `[${tag}] Onboarding Form Submit`;
  constructor(
    public request: OnboardingProcessRequest,
    public enableForm: () => void,
    public disableForm: () => void,
    public success: () => void,
  ) {}
}

export class Reset {
  static readonly type = `[${tag}] Reset`;
}
