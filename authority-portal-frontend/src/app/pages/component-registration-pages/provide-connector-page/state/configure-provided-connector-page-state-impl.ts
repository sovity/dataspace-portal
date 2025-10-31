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
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ignoreElements, map, switchMap, takeUntil, tap} from 'rxjs/operators';
import {Action, Actions, State, StateContext, ofAction} from '@ngxs/store';
import {
  ConfigureProvidedConnectorWithCertificateRequest,
  ConfigureProvidedConnectorWithJwksRequest,
  ConnectorDetailsDto,
  CreateConnectorResponse,
  OrganizationOverviewEntryDto,
} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {ErrorService} from 'src/app/core/services/error.service';
import {Fetched} from 'src/app/core/utils/fetched';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {EDC_CONFIG} from '../../../../core/services/config/connector-config';
import {ConfigureProvidedConnectorPageFormValue} from '../provide-connector-page/configure-provided-connector-page-form-model';
import {
  GetConnector,
  GetOrganizations,
  Reset,
  Submit,
} from './configure-provided-connector-page-actions';
import {
  ConfigureProvidedConnectorPageState,
  DEFAULT_PROVIDE_CONNECTOR_PAGE_STATE,
} from './configure-provided-connector-page-state';

@State<ConfigureProvidedConnectorPageState>({
  name: 'ProvideConnectorPage',
  defaults: DEFAULT_PROVIDE_CONNECTOR_PAGE_STATE,
})
@Injectable()
export class ConfigureProvidedConnectorPageStateImpl {
  constructor(
    private apiService: ApiService,
    private actions$: Actions,
    private toast: ToastService,
    private errorService: ErrorService,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  @Action(Reset)
  onReset(ctx: StateContext<ConfigureProvidedConnectorPageState>): void {
    ctx.setState(DEFAULT_PROVIDE_CONNECTOR_PAGE_STATE);
  }

  @Action(Submit, {cancelUncompleted: true})
  onSubmit(
    ctx: StateContext<ConfigureProvidedConnectorPageState>,
    action: Submit,
  ): Observable<never> {
    ctx.patchState({state: 'submitting'});
    action.disableForm();

    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap(
        (deploymentEnvironmentId): Observable<CreateConnectorResponse> => {
          if (action.request.connectorTab.useJwks) {
            const request = this.buildConfigureConnectorWithJwksRequest(
              action.request,
            );
            return this.apiService.configureProvidedConnectorWithJwks(
              request,
              action.organizationId,
              action.connectorId,
              deploymentEnvironmentId,
            );
          } else {
            const request = this.buildConfigureConnectorWithCertificateRequest(
              action.request,
            );
            return this.apiService.configureProvidedConnectorWithCertificate(
              request,
              action.organizationId,
              action.connectorId,
              deploymentEnvironmentId,
            );
          }
        },
      ),
      tap((res) => {
        ctx.patchState({
          deploymentEnvironment:
            this.globalStateUtils.snapshot.selectedEnvironment,
          connectorId: res.id,
        });
        switch (res.status) {
          case 'OK':
            this.toast.showSuccess(`Connector was successfully configured`);
            ctx.patchState({state: 'success'});
            action.success();
            break;
          case 'WARNING':
            this.toast.showWarning(
              res?.message ||
                'A problem occurred while providing the connector.',
            );
            ctx.patchState({state: 'success'});
            action.success();
            break;
          case 'ERROR':
            this.toast.showDanger(res?.message || 'Failed providing connector');
            ctx.patchState({state: 'error'});
            action.enableForm();
            break;
        }
      }),
      takeUntil(this.actions$.pipe(ofAction(Reset))),
      this.errorService.toastFailureRxjs('Failed providing connector', () => {
        ctx.patchState({state: 'error'});
        action.enableForm();
      }),
      ignoreElements(),
    );
  }

  @Action(GetOrganizations, {cancelUncompleted: true})
  onRefreshOrganizations(
    ctx: StateContext<ConfigureProvidedConnectorPageState>,
  ): Observable<never> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((deploymentEnvironmentId) =>
        this.apiService.getOrganizationsForProvidingConnectors(
          deploymentEnvironmentId,
        ),
      ),
      map((result) => result.organizations),
      Fetched.wrap({failureMessage: 'Failed loading organizations'}),
      tap((organizations) => this.organizationsRefreshed(ctx, organizations)),
      ignoreElements(),
    );
  }

  @Action(GetConnector, {cancelUncompleted: true})
  onGetConnector(
    ctx: StateContext<ConfigureProvidedConnectorPageState>,
    action: GetConnector,
  ): Observable<never> {
    return this.globalStateUtils.getDeploymentEnvironmentId().pipe(
      switchMap((deploymentEnvironmentId) =>
        this.apiService.getProvidedConnectorDetails(action.connectorId),
      ),
      tap((connector) => this.connectorFetched(ctx, connector)),
      ignoreElements(),
    );
  }

  private organizationsRefreshed(
    ctx: StateContext<ConfigureProvidedConnectorPageState>,
    newOrganizations: Fetched<OrganizationOverviewEntryDto[]>,
  ) {
    ctx.patchState({organizationList: newOrganizations});
  }

  private connectorFetched(
    ctx: StateContext<ConfigureProvidedConnectorPageState>,
    connector: ConnectorDetailsDto,
  ) {
    ctx.patchState({
      connectorData: connector,
    });
  }

  private buildConfigureConnectorWithJwksRequest(
    formValue: ConfigureProvidedConnectorPageFormValue,
  ): ConfigureProvidedConnectorWithJwksRequest {
    return {
      ...this.buildCommonConfigureConnectorWithCertificateRequest(formValue),
      jwksUrl: formValue.connectorTab.useCustomUrls
        ? formValue.connectorTab.jwksUrl
        : new URL(
            EDC_CONFIG.defaultPaths.jwksUrl,
            formValue.connectorTab.baseUrl,
          ).toString(),
    };
  }

  private buildConfigureConnectorWithCertificateRequest(
    formValue: ConfigureProvidedConnectorPageFormValue,
  ): ConfigureProvidedConnectorWithCertificateRequest {
    return {
      ...this.buildCommonConfigureConnectorWithCertificateRequest(formValue),
      certificate: formValue.certificateTab.bringOwnCert
        ? formValue.certificateTab.ownCertificate
        : formValue.certificateTab.generatedCertificate,
    };
  }

  private buildCommonConfigureConnectorWithCertificateRequest(
    formValue: ConfigureProvidedConnectorPageFormValue,
  ) {
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

    return {
      frontendUrl,
      endpointUrl,
      managementUrl,
    };
  }
}
