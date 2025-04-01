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
import {FormControl, FormGroup, ɵFormGroupRawValue} from '@angular/forms';
import {OrganizationCreateFormModel} from '../../../../shared/business/organization-create-form/organization-create-form-model';
import {UserOnboardFormModel} from '../../../../shared/business/user-onboard-form/user-onboard-form-model';

export interface OnboardingUserTabFormModel extends UserOnboardFormModel {
  acceptedTos: FormControl<boolean>;
}

export type OnboardingUserTabFormValue =
  ɵFormGroupRawValue<OnboardingUserTabFormModel>;

export interface OnboardingOrganizationTabFormModel
  extends OrganizationCreateFormModel {
  acceptedTos: FormControl<boolean>;
}

export type OnboardingOrganizationTabFormValue =
  ɵFormGroupRawValue<OnboardingOrganizationTabFormModel>;

export interface OnboardingWizardFormModel {
  userTab: FormGroup<OnboardingUserTabFormModel>;
  organizationTab: FormGroup<OnboardingOrganizationTabFormModel>;
}

export type OnboardingWizardFormValue =
  ɵFormGroupRawValue<OnboardingWizardFormModel>;
