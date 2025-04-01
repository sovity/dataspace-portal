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
    this.store.dispatch(
      new Submit(
        {
          name: formValue.connectorTab.name,
          endpointUrl: formValue.connectorTab.endpointUrl,
          location: formValue.connectorTab.location,
          frontendUrl: formValue.connectorTab.frontendUrl,
          managementUrl: formValue.connectorTab.managementUrl,
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

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
