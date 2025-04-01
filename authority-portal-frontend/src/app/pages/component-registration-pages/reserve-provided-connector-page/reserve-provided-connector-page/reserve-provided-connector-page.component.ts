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
import {Router} from '@angular/router';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {UserInfo, UserRoleDto} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {HeaderBarConfig} from '../../../../shared/common/header-bar/header-bar.model';
import {DEFAULT_CONTROL_CENTER_ORGANIZATION_EDIT_PAGE_STATE} from '../../../control-center-organization-edit-page/state/control-center-organization-edit-page-state';
import {
  GetOrganizations,
  Reset,
  Submit,
} from '../state/reserve-provided-connector-page-actions';
import {
  DEFAULT_RESERVE_PROVIDED_CONNECTOR_PAGE_STATE,
  ReserveProvidedConnectorPageState,
} from '../state/reserve-provided-connector-page-state';
import {ReserveProvidedConnectorPageStateImpl} from '../state/reserve-provided-connector-page-state-impl';
import {ReserveProvidedConnectorPageForm} from './reserve-provided-connector-page-form';

@Component({
  selector: 'app-reserve-provided-connector-page',
  templateUrl: './reserve-provided-connector-page.component.html',
  providers: [ReserveProvidedConnectorPageForm],
})
export class ReserveProvidedConnectorPageComponent
  implements OnInit, OnDestroy
{
  @HostBinding('class.overflow-y-auto')
  cls = true;
  state = DEFAULT_RESERVE_PROVIDED_CONNECTOR_PAGE_STATE;
  userInfo!: UserInfo;

  exitLink = '/service-partner/provided-connectors';

  @ViewChild('stepper') stepper!: MatStepper;

  private ngOnDestroy$ = new Subject();

  headerConfig!: HeaderBarConfig;

  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private store: Store,
    public form: ReserveProvidedConnectorPageForm,
    private globalStateUtils: GlobalStateUtils,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.store.dispatch(GetOrganizations);
    this.store.dispatch(Reset);
    this.startListeningToState();
    this.getUserInfo();
    this.initializeHeaderBar();
  }

  getUserInfo() {
    this.globalStateUtils.userInfo$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((userInfo: UserInfo) => {
        this.userInfo = userInfo;
      });
  }

  initializeHeaderBar() {
    this.headerConfig = {
      title: 'Provide Connector',
      subtitle: 'Register a connector for a participant organization.',
      headerActions: [],
    };
  }

  private startListeningToState() {
    this.store
      .select<ReserveProvidedConnectorPageState>(
        ReserveProvidedConnectorPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  reserveConnector(): void {
    const formValue = this.form.value;
    const organizationId = formValue.connectorInfo.organization!.id;

    this.store.dispatch(
      new Submit(
        formValue,
        organizationId,
        () => this.form.group.enable(),
        () => this.form.group.disable(),
        () => {
          setTimeout(() => {
            this.router.navigate(['/service-partner/provided-connectors']);
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
