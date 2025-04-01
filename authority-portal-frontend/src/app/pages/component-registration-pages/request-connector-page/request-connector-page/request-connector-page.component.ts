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
import {Component, Inject, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable, Subject, switchMap, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {CaasAvailabilityResponse} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {inferArticle} from '../../../../core/utils/string-utils';
import {Reset, Submit} from '../state/request-connector-page-actions';
import {
  DEFAULT_REQUEST_CONNECTOR_STATE,
  RequestConnectorPageState,
} from '../state/request-connector-page-state';
import {RequestConnectorPageStateImpl} from '../state/request-connector-page-state-impl';
import {RequestConnectorPageForm} from './request-connector-page-form';

@Component({
  selector: 'app-request-connector-page',
  templateUrl: './request-connector-page.component.html',
  providers: [RequestConnectorPageForm],
})
export class RequestConnectorPageComponent implements OnInit {
  state = DEFAULT_REQUEST_CONNECTOR_STATE;
  requestConnectorPageForm = this.form.formGroup.controls;

  createActionName = 'Request CaaS';
  backLink = '/my-organization/connectors/new/choose-provider';

  sponsoredCaasAmount = 1;

  private ngOnDestroy$ = new Subject();

  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    public form: RequestConnectorPageForm,
    private store: Store,
    private apiService: ApiService,
    private globalStateUtils: GlobalStateUtils,
    private router: Router,
  ) {}

  ngOnInit() {
    this.redirectIfOverCaasLimits();
    this.startListeningToState();
    this.store.dispatch(Reset);
  }

  requestCaas(): void {
    this.form.formGroup.disable();
    this.store.dispatch(
      new Submit(
        {
          ...this.form.getValue,
        },
        () => this.form.formGroup.enable(),
        () => this.form.formGroup.disable(),
      ),
    );
  }

  redirectIfOverCaasLimits() {
    this.globalStateUtils
      .getDeploymentEnvironmentId()
      .pipe(
        switchMap(
          (deploymentEnvironmentId): Observable<CaasAvailabilityResponse> =>
            this.apiService.checkFreeCaasUsage(deploymentEnvironmentId),
        ),
        takeUntil(this.ngOnDestroy$),
      )
      .subscribe((x) => {
        this.sponsoredCaasAmount = x.limit ?? 1;
        if ((x.current ?? 0) >= (x.limit ?? -1)) {
          this.router.navigate([
            '/my-organization',
            'connectors',
            'registration',
          ]);
        }
      });
  }

  startListeningToState() {
    this.store
      .select<RequestConnectorPageState>(RequestConnectorPageStateImpl)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  protected readonly inferArticle = inferArticle;
}
