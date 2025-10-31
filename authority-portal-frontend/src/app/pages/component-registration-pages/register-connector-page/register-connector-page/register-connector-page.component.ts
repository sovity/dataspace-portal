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
import {UserInfo} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {
  EDC_CONFIG,
  generateConnectorConfig,
} from 'src/app/core/services/config/connector-config';
import {Reset, Submit} from '../state/register-connector-page-actions';
import {
  DEFAULT_REGISTER_CONNECTOR_PAGE_STATE,
  RegisterConnectorPageState,
} from '../state/register-connector-page-state';
import {RegisterConnectorPageStateImpl} from '../state/register-connector-page-state-impl';
import {RegisterConnectorPageForm} from './register-connector-page-form';

@Component({
  selector: 'app-register-connector-page',
  templateUrl: './register-connector-page.component.html',
  providers: [RegisterConnectorPageForm],
})
export class RegisterConnectorPageComponent implements OnInit, OnDestroy {
  @HostBinding('class.overflow-y-auto')
  cls = true;
  state = DEFAULT_REGISTER_CONNECTOR_PAGE_STATE;
  userInfo!: UserInfo;

  createActionName = 'Register Connector';
  backLink = '/my-organization/connectors/new';
  exitLink = '/my-organization/connectors';

  edcConfig = EDC_CONFIG;

  @ViewChild('stepper') stepper!: MatStepper;

  private ngOnDestroy$ = new Subject();

  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private store: Store,
    public form: RegisterConnectorPageForm,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  ngOnInit(): void {
    this.store.dispatch(Reset);
    this.startListeningToState();
    this.getUserInfo();
  }

  get useCustomUrls(): boolean {
    return this.form.connectorTab.controls.useCustomUrls.value;
  }

  getUserInfo() {
    this.globalStateUtils.userInfo$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((userInfo: UserInfo) => {
        this.userInfo = userInfo;
      });
  }

  private startListeningToState() {
    this.store
      .select<RegisterConnectorPageState>(RegisterConnectorPageStateImpl)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  registerConnector(): void {
    const formValue = this.form.value;

    const frontendUrl = formValue.connectorTab.useCustomUrls
      ? formValue.connectorTab.frontendUrl
      : formValue.connectorTab.baseUrl;
    const endpointUrl = formValue.connectorTab.useCustomUrls
      ? formValue.connectorTab.endpointUrl
      : new URL(
          EDC_CONFIG.defaultPaths.dspApi,
          formValue.connectorTab.baseUrl,
        ).toString();
    const managementUrl = formValue.connectorTab.useCustomUrls
      ? formValue.connectorTab.managementUrl
      : new URL(
          EDC_CONFIG.defaultPaths.managementApi,
          formValue.connectorTab.baseUrl,
        ).toString();

    this.store.dispatch(
      new Submit(
        {
          name: formValue.connectorTab.name,
          location: formValue.connectorTab.location,
          frontendUrl,
          endpointUrl,
          managementUrl,
          certificate: formValue.certificateTab.bringOwnCert
            ? formValue.certificateTab.ownCertificate
            : formValue.certificateTab.generatedCertificate,
        },
        () => this.form.group.enable(),
        () => this.form.group.disable(),
        () => {
          setTimeout(() => {
            this.stepper.next();
          }, 200);
        },
      ),
    );
  }

  getConnectorConfig(): string {
    const bringOwnCert = this.form.certificateTab.controls.bringOwnCert.value;
    return generateConnectorConfig({
      connectorBaseUrl: this.form.value.connectorTab.baseUrl,
      certificate: bringOwnCert
        ? this.form.certificateTab.controls.ownCertificate.value.trim()
        : this.form.certificateTab.controls.generatedCertificate.value.trim(),
      privateKey: bringOwnCert
        ? '<Your Private Key Here>'
        : this.form.certificateTab.controls.generatedPrivateKey.value.trim(),
      participantId: this.state.connectorId ?? '',
      dapsJwksUrl: this.state.deploymentEnvironment?.dapsJwksUrl ?? '',
      dapsTokenUrl: this.state.deploymentEnvironment?.dapsTokenUrl ?? '',
    });
  }

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
