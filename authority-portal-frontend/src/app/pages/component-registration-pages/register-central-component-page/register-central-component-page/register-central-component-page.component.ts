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
  Component,
  HostBinding,
  Inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import {MatStepper} from '@angular/material/stepper';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {
  CentralComponentCreateRequest,
  UserInfo,
} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {ClipboardUtils} from 'src/app/core/utils/clipboard-utils';
import {Reset, Submit} from '../state/register-central-component-page-actions';
import {
  DEFAULT_REGISTER_CENTRAL_COMPONENT_PAGE_STATE,
  RegisterCentralComponentPageState,
} from '../state/register-central-component-page-state';
import {RegisterCentralComponentPageStateImpl} from '../state/register-central-component-page-state-impl';
import {RegisterCentralComponentPageForm} from './register-central-component-page-form';

@Component({
  selector: 'app-register-central-component-page',
  templateUrl: './register-central-component-page.component.html',
  providers: [RegisterCentralComponentPageForm],
})
export class RegisterCentralComponentPageComponent
  implements OnInit, OnDestroy
{
  @HostBinding('class.overflow-y-auto')
  cls = true;
  state = DEFAULT_REGISTER_CENTRAL_COMPONENT_PAGE_STATE;
  userInfo!: UserInfo;

  createActionName = 'Register Central Component';
  exitLink = '/operator/central-components';

  @ViewChild('stepper') stepper!: MatStepper;

  private ngOnDestroy$ = new Subject();

  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private store: Store,
    public form: RegisterCentralComponentPageForm,
    private globalStateUtils: GlobalStateUtils,
    private clipboardUtils: ClipboardUtils,
  ) {}

  ngOnInit(): void {
    this.store.dispatch(Reset);
    this.startListeningToState();
    this.getUserInfo();
  }

  getUserInfo() {
    this.globalStateUtils.userInfo$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((userInfo: UserInfo) => {
        this.userInfo = userInfo;
      });
  }

  copyToClipboard() {
    this.clipboardUtils.copyToClipboard(
      this.state.createdCentralComponentId || '',
    );
  }

  private startListeningToState() {
    this.store
      .select<RegisterCentralComponentPageState>(
        RegisterCentralComponentPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  registerComponent(): void {
    const formValue = this.form.value;
    let request: CentralComponentCreateRequest = {
      name: formValue.componentTab.name,
      endpointUrl: formValue.componentTab.endpointUrl,
      homepageUrl: formValue.componentTab.frontendUrl,
      certificate: formValue.certificateTab.bringOwnCert
        ? formValue.certificateTab.ownCertificate
        : formValue.certificateTab.generatedCertificate,
    };
    this.store.dispatch(
      new Submit(
        request,
        () => this.form.group.enable(),
        () => this.form.group.disable(),
        () => {
          setTimeout(() => {
            this.stepper.next();
          }, 300);
        },
      ),
    );
  }

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
