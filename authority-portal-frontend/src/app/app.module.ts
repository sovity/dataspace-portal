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
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgxsReduxDevtoolsPluginModule} from '@ngxs/devtools-plugin';
import {NgxsModule} from '@ngxs/store';
import {ApiService} from 'src/app/core/api/api.service';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ApiClientFactory} from './core/api/api-client-factory';
import {CatalogApiService} from './core/api/catalog-api.service';
import {GlobalStateImpl} from './core/global-state/global-state-impl';
import {NgxsInZoneExecutionStrategy} from './core/global-state/ngxs-in-zone-execution-strategy';
import {provideAppConfig} from './core/services/config/app-config-initializer';
import {AuthorityConnectorDetailPageModule} from './pages/authority-connector-detail-page/authority-connector-detail-page.module';
import {AuthorityConnectorListPageModule} from './pages/authority-connector-list-page/authority-connector-list-page.module';
import {AuthorityOrganizationDetailPageModule} from './pages/authority-organization-detail-page/authority-organization-detail-page.module';
import {AuthorityOrganizationEditPageModule} from './pages/authority-organization-edit-page/authority-organization-edit-page.module';
import {AuthorityOrganizationListPageModule} from './pages/authority-organization-list-page/authority-organization-list-page.module';
import {CatalogPageModule} from './pages/catalog-page/catalog-page.module';
import {CentralComponentListPageModule} from './pages/central-component-list-page/central-component-list-page.module';
import {ChooseParticipantCaasModule} from './pages/choose-participant-caas/choose-participant-caas.module';
import {ChooseParticipantConnectorModule} from './pages/choose-participant-connector/choose-participant-connector.module';
import {ConfigureProvidedConnectorPageModule} from './pages/component-registration-pages/provide-connector-page/configure-provided-connector-page.module';
import {RegisterCentralComponentPageModule} from './pages/component-registration-pages/register-central-component-page/register-central-component-page.module';
import {RegisterConnectorPageModule} from './pages/component-registration-pages/register-connector-page/register-connector-page.module';
import {RequestConnectorPageModule} from './pages/component-registration-pages/request-connector-page/request-connector-page.module';
import {ReserveProvidedConnectorPageModule} from './pages/component-registration-pages/reserve-provided-connector-page/reserve-provided-connector-page.module';
import {ControlCenterOrganizationEditPageModule} from './pages/control-center-organization-edit-page/control-center-organization-edit-page.module';
import {ControlCenterOrganizationMemberDetailPageModule} from './pages/control-center-organization-member-detail-page/control-center-organization-member-detail-page.module';
import {ControlCenterOrganizationMembersPageModule} from './pages/control-center-organization-members-page/control-center-organization-members-page.module';
import {ControlCenterOrganizationProfilePageModule} from './pages/control-center-organization-profile-page/control-center-organization-profile-page.module';
import {ControlCenterPageModule} from './pages/control-center-page/control-center-page.module';
import {ControlCenterUserEditPageModule} from './pages/control-center-user-edit-page/control-center-user-edit-page.module';
import {ControlCenterUserProfilePageModule} from './pages/control-center-user-profile-page/control-center-user-profile-page.module';
import {DashboardPageModule} from './pages/dashboard-page/dashboard.module';
import {LoadingPageModule} from './pages/empty-pages/loading-page/loading-page.module';
import {PageNotFoundPageModule} from './pages/empty-pages/page-not-found-page/page-not-found-page.module';
import {UnauthenticatedPageModule} from './pages/empty-pages/unauthenticated-page/unauthenticated-page.module';
import {ParticipantOwnConnectorDetailPageModule} from './pages/participant-own-connector-detail-page/participant-own-connector-detail-page.module';
import {ParticipantOwnConnectorListPageModule} from './pages/participant-own-connector-list-page/participant-own-connector-list-page.module';
import {OrganizationCreatePageModule} from './pages/registration-pages/organization-create-page/organization-create-page.module';
import {OrganizationOnboardPageModule} from './pages/registration-pages/organization-onboard-page/organization-onboard-page.module';
import {OrganizationPendingPageModule} from './pages/registration-pages/organization-pending-page/organization-pending-page.module';
import {OrganizationRejectedPageModule} from './pages/registration-pages/organization-rejected-page/organization-rejected-page.module';
import {SpConnectorDetailPageModule} from './pages/sp-connector-detail-page/sp-connector-detail-page.module';
import {SpConnectorListPageModule} from './pages/sp-connector-list-page/sp-connector-list-page.module';
import {SharedModule} from './shared/shared.module';

@NgModule({
  declarations: [AppComponent],
  providers: [
    provideAppConfig(),
    ApiService,
    ApiClientFactory,
    CatalogApiService,
  ],
  bootstrap: [AppComponent],
  imports: [
    // Angular
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,

    // Ngxs
    NgxsModule.forRoot([GlobalStateImpl], {
      developmentMode: true,
      executionStrategy: NgxsInZoneExecutionStrategy,
    }),
    NgxsReduxDevtoolsPluginModule.forRoot(),

    // Authority Portal
    SharedModule,

    // Authority Portal pages
    AuthorityConnectorDetailPageModule,
    AuthorityConnectorListPageModule,
    AuthorityOrganizationDetailPageModule,
    AuthorityOrganizationListPageModule,
    AuthorityOrganizationEditPageModule,
    CatalogPageModule,
    CentralComponentListPageModule,
    ChooseParticipantCaasModule,
    ChooseParticipantConnectorModule,
    ControlCenterOrganizationEditPageModule,
    ControlCenterOrganizationMemberDetailPageModule,
    ControlCenterOrganizationMembersPageModule,
    ControlCenterOrganizationProfilePageModule,
    ControlCenterPageModule,
    ControlCenterUserEditPageModule,
    ControlCenterUserProfilePageModule,
    DashboardPageModule,
    LoadingPageModule,
    OrganizationCreatePageModule,
    OrganizationOnboardPageModule,
    OrganizationPendingPageModule,
    OrganizationRejectedPageModule,
    PageNotFoundPageModule,
    ParticipantOwnConnectorDetailPageModule,
    ParticipantOwnConnectorListPageModule,
    ConfigureProvidedConnectorPageModule,
    RegisterCentralComponentPageModule,
    RegisterConnectorPageModule,
    RequestConnectorPageModule,
    ReserveProvidedConnectorPageModule,
    SpConnectorDetailPageModule,
    SpConnectorListPageModule,
    UnauthenticatedPageModule,

    // Routing Module
    AppRoutingModule,
  ],
})
export class AppModule {}
