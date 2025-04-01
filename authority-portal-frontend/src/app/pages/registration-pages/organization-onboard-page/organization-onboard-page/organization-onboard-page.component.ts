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
import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatStepper} from '@angular/material/stepper';
import {Title} from '@angular/platform-browser';
import {Subject, take, takeUntil} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {
  OwnOrganizationDetailsDto,
  UserDetailDto,
} from '@sovity.de/authority-portal-client';
import {
  mergeFormGroups,
  switchDisabledControls,
} from 'src/app/core/utils/form-utils';
import {
  APP_CONFIG,
  AppConfig,
} from '../../../../core/services/config/app-config';
import {buildOrganizationCreateForm} from '../../../../shared/business/organization-create-form/organization-create-form-builder';
import {organizationCreateFormEnabledCtrls} from '../../../../shared/business/organization-create-form/organization-create-form-enabled-ctrls';
import {OrganizationCreateFormModel} from '../../../../shared/business/organization-create-form/organization-create-form-model';
import {buildUserOnboardForm} from '../../../../shared/business/user-onboard-form/user-onboard-form-builder';
import {UserOnboardFormModel} from '../../../../shared/business/user-onboard-form/user-onboard-form-model';
import {
  OnboardingProcessFormSubmit,
  Reset,
} from '../state/organization-onboard-page-action';
import {
  DEFAULT_ORGANIZATION_ONBOARD_PAGE_PAGE_STATE,
  OrganizationOnboardPageState,
} from '../state/organization-onboard-page-state';
import {OrganizationOnboardPageStateImpl} from '../state/organization-onboard-page-state-impl';
import {
  buildInitialOnboardingFormValue,
  buildOnboardingRequest,
} from './organization-onboard-page.form-mapper';
import {
  OnboardingOrganizationTabFormModel,
  OnboardingOrganizationTabFormValue,
  OnboardingUserTabFormModel,
  OnboardingWizardFormModel,
  OnboardingWizardFormValue,
} from './organization-onboard-page.form-model';

@Component({
  selector: 'app-organization-onboard-page',
  templateUrl: './organization-onboard-page.component.html',
})
export class OrganizationOnboardPageComponent implements OnInit {
  state = DEFAULT_ORGANIZATION_ONBOARD_PAGE_PAGE_STATE;
  parentFormGroup!: FormGroup<OnboardingWizardFormModel>;
  showForm: boolean = false;
  ngOnDestroy$ = new Subject();

  @ViewChild('stepper') stepper!: MatStepper;

  get userForm(): FormGroup<OnboardingUserTabFormModel> {
    return this.parentFormGroup.controls.userTab;
  }
  get userOnboardForm(): FormGroup<UserOnboardFormModel> {
    return this.parentFormGroup.controls
      .userTab as unknown as FormGroup<UserOnboardFormModel>;
  }

  get orgForm(): FormGroup<OnboardingOrganizationTabFormModel> {
    return this.parentFormGroup.controls.organizationTab;
  }

  get orgProfileForm(): FormGroup<OrganizationCreateFormModel> {
    // this only requires a cast because A extends B does not imply T<A> extend T<B>
    return this.orgForm as unknown as FormGroup<OrganizationCreateFormModel>;
  }

  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private store: Store,
    private formBuilder: FormBuilder,
    private titleService: Title,
  ) {
    this.titleService.setTitle('Onboarding');
  }

  ngOnInit(): void {
    this.store.dispatch(Reset);
    this.startListeningToState();
    this.setupFormOnce();
  }

  private setupFormOnce() {
    this.store
      .select<OrganizationOnboardPageState>(OrganizationOnboardPageStateImpl)
      .pipe(
        map((it) => it.details),
        filter((it) => it.isReady),
        map((it) => it.data),
        takeUntil(this.ngOnDestroy$),
        take(1),
      )
      .subscribe((details) => {
        this.parentFormGroup = this.buildFormGroup(
          details.user,
          details.organization,
        );
      });
  }

  private startListeningToState() {
    this.store
      .select<OrganizationOnboardPageState>(OrganizationOnboardPageStateImpl)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  buildFormGroup(
    user: UserDetailDto,
    organizationDetail: OwnOrganizationDetailsDto,
  ): FormGroup<OnboardingWizardFormModel> {
    const initial = buildInitialOnboardingFormValue(user, organizationDetail);
    let userTab: FormGroup<OnboardingUserTabFormModel> = mergeFormGroups(
      buildUserOnboardForm(this.formBuilder, initial.userTab),
      this.formBuilder.nonNullable.group({
        acceptedTos: [initial.userTab.acceptedTos, Validators.requiredTrue],
      }),
    );

    let organizationTab: FormGroup<OnboardingOrganizationTabFormModel> =
      mergeFormGroups(
        buildOrganizationCreateForm(this.formBuilder, initial.organizationTab),
        this.formBuilder.nonNullable.group({
          acceptedTos: [
            initial.organizationTab.acceptedTos,
            Validators.requiredTrue,
          ],
        }),
      );

    switchDisabledControls<OnboardingOrganizationTabFormValue>(
      organizationTab,
      (value: OnboardingOrganizationTabFormValue) => {
        return {
          ...organizationCreateFormEnabledCtrls(value),
          acceptedTos: true,
        };
      },
    );

    return this.formBuilder.nonNullable.group({
      userTab,
      organizationTab,
    });
  }

  submit(): void {
    const request = buildOnboardingRequest(
      this.parentFormGroup.value as OnboardingWizardFormValue,
    );
    this.store.dispatch(
      new OnboardingProcessFormSubmit(
        request,
        () => this.parentFormGroup.enable(),
        () => this.parentFormGroup.disable(),
        () => {
          setTimeout(() => {
            this.stepper.next();
          }, 0);
        },
      ),
    );
  }
}
