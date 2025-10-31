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
import {ActivatedRoute} from '@angular/router';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {UserInfo} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {
  EDC_CONFIG,
  generateConnectorConfig,
  generateConnectorConfigShort,
} from 'src/app/core/services/config/connector-config';
import {ClipboardUtils} from '../../../../core/utils/clipboard-utils';
import {
  GetConnector,
  GetOrganizations,
  Reset,
  Submit,
} from '../state/configure-provided-connector-page-actions';
import {
  ConfigureProvidedConnectorPageState,
  DEFAULT_PROVIDE_CONNECTOR_PAGE_STATE,
} from '../state/configure-provided-connector-page-state';
import {ConfigureProvidedConnectorPageStateImpl} from '../state/configure-provided-connector-page-state-impl';
import {ConfigureProvidedConnectorPageForm} from './configure-provided-connector-page-form.service';

@Component({
  selector: 'app-provide-connector-page',
  templateUrl: './configure-provided-connector-page.component.html',
  providers: [ConfigureProvidedConnectorPageForm],
})
export class ConfigureProvidedConnectorPageComponent
  implements OnInit, OnDestroy
{
  @HostBinding('class.overflow-y-auto')
  cls = true;
  state = DEFAULT_PROVIDE_CONNECTOR_PAGE_STATE;
  userInfo!: UserInfo;

  edcConfig = EDC_CONFIG;

  createActionName = 'Provide Connector';
  exitLink = '/service-partner/provided-connectors';

  connectorId: string;

  @ViewChild('stepper') stepper!: MatStepper;

  private ngOnDestroy$ = new Subject();

  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private store: Store,
    public form: ConfigureProvidedConnectorPageForm,
    public globalStateUtils: GlobalStateUtils,
    private route: ActivatedRoute,
    public clipboardUtils: ClipboardUtils,
  ) {
    const routeParams = this.route.snapshot.params;
    this.connectorId = routeParams['connectorId'];
  }

  ngOnInit(): void {
    this.store.dispatch(GetOrganizations);
    this.store.dispatch(new GetConnector(this.connectorId));
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
      .select<ConfigureProvidedConnectorPageState>(
        ConfigureProvidedConnectorPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  registerConnector(registrationType: 'certificate' | 'jwks'): void {
    if (registrationType === 'jwks') {
      this.form.certificateTab.disable();
    }

    const formValue = this.form.value;
    const organizationId = this.state.connectorData?.organizationId!;

    this.store.dispatch(
      new Submit(
        formValue,
        organizationId,
        this.connectorId,
        () => this.form.group.enable(),
        () => this.form.group.disable(),
        () => {
          setTimeout(() => {
            formValue.connectorTab.useJwks
              ? (this.stepper.selectedIndex = 2)
              : this.stepper.next();
          }, 200);
        },
      ),
    );
  }

  copyInitialConnectorConfig() {
    this.clipboardUtils.copyToClipboard(this.getInitialConnectorConfig());
  }

  getInitialConnectorConfig(): string {
    return generateConnectorConfigShort({
      participantId: this.state.connectorData?.connectorId ?? '',
      dapsJwksUrl: this.state.connectorData?.environment?.dapsJwksUrl ?? '',
      dapsTokenUrl: this.state.connectorData?.environment?.dapsTokenUrl ?? '',
    });
  }

  getFinalConnectorConfig(): string {
    const bringOwnCert = this.form.certificateTab.controls.bringOwnCert.value;
    return generateConnectorConfig({
      connectorBaseUrl: this.form.value.connectorTab.baseUrl,
      certificate: bringOwnCert
        ? this.form.certificateTab.controls.ownCertificate.value.trim()
        : this.form.certificateTab.controls.generatedCertificate.value.trim(),
      privateKey: bringOwnCert
        ? '<Your Private Key Here>'
        : this.form.certificateTab.controls.generatedPrivateKey.value.trim(),
      participantId: this.state.connectorData?.connectorId ?? '',
      dapsJwksUrl: this.state.connectorData?.environment?.dapsJwksUrl ?? '',
      dapsTokenUrl: this.state.connectorData?.environment?.dapsTokenUrl ?? '',
    });
  }

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
