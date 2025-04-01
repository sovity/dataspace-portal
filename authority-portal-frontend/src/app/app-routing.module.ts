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
import {InjectionToken, NgModule, Type} from '@angular/core';
import {ActivatedRouteSnapshot, RouterModule, Routes} from '@angular/router';
import {UserRoleDto} from '@sovity.de/authority-portal-client';
import {requiresRole} from './core/services/auth/requires-role-guard';
import {AuthorityConnectorListPageComponent} from './pages/authority-connector-list-page/authority-connector-list-page/authority-connector-list-page.component';
import {AuthorityOrganizationEditPageComponent} from './pages/authority-organization-edit-page/authority-organization-edit-page/authority-organization-edit-page.component';
import {AuthorityOrganizationListPageComponent} from './pages/authority-organization-list-page/authority-organization-list-page/authority-organization-list-page.component';
import {CatalogPageComponent} from './pages/catalog-page/catalog-page/catalog-page.component';
import {CentralComponentListPageComponent} from './pages/central-component-list-page/central-component-list-page/central-component-list-page.component';
import {ChooseParticipantCaasComponent} from './pages/choose-participant-caas/choose-participant-caas/choose-participant-caas.component';
import {ChooseParticipantConnectorComponent} from './pages/choose-participant-connector/choose-participant-connector/choose-participant-connector.component';
import {ConfigureProvidedConnectorPageComponent} from './pages/component-registration-pages/provide-connector-page/provide-connector-page/configure-provided-connector-page.component';
import {RegisterCentralComponentPageComponent} from './pages/component-registration-pages/register-central-component-page/register-central-component-page/register-central-component-page.component';
import {RegisterConnectorPageComponent} from './pages/component-registration-pages/register-connector-page/register-connector-page/register-connector-page.component';
import {RequestConnectorPageComponent} from './pages/component-registration-pages/request-connector-page/request-connector-page/request-connector-page.component';
import {ReserveProvidedConnectorPageComponent} from './pages/component-registration-pages/reserve-provided-connector-page/reserve-provided-connector-page/reserve-provided-connector-page.component';
import {ControlCenterPageComponent} from './pages/control-center-page/control-center-page/control-center-page.component';
import {CONTROL_CENTER_ROUTES} from './pages/control-center-page/control-center-routes';
import {DashboardPageComponent} from './pages/dashboard-page/dashboard-page/dashboard-page.component';
import {LoadingPageComponent} from './pages/empty-pages/loading-page/loading-page/loading-page.component';
import {PageNotFoundPageComponent} from './pages/empty-pages/page-not-found-page/page-not-found-page/page-not-found-page.component';
import {UnauthenticatedPageComponent} from './pages/empty-pages/unauthenticated-page/unauthenticated-page/unauthenticated-page.component';
import {ParticipantOwnConnectorDetailPageComponent} from './pages/participant-own-connector-detail-page/participant-own-connector-detail-page/participant-own-connector-detail-page.component';
import {ParticipantOwnConnectorListPageComponent} from './pages/participant-own-connector-list-page/participant-own-connector-list-page/participant-own-connector-list-page.component';
import {OrganizationCreatePageComponent} from './pages/registration-pages/organization-create-page/organization-create-page/organization-create-page.component';
import {OrganizationOnboardPageComponent} from './pages/registration-pages/organization-onboard-page/organization-onboard-page/organization-onboard-page.component';
import {OrganizationPendingPageComponent} from './pages/registration-pages/organization-pending-page/organization-pending-page/organization-pending-page.component';
import {OrganizationRejectedPageComponent} from './pages/registration-pages/organization-rejected-page/organization-rejected-page/organization-rejected-page.component';
import {SpConnectorListPageComponent} from './pages/sp-connector-list-page/sp-connector-list-page/sp-connector-list-page.component';
import {PortalLayoutComponent} from './shared/common/portal-layout/portal-layout/portal-layout.component';

const EXTERNAL_URL_PROVIDER = new InjectionToken('externalRedirectProvider');

export const UNAUTHENTICATED_ROUTES: Routes = [
  {
    path: 'create-organization',
    component: OrganizationCreatePageComponent,
  },
  {
    path: '**',
    component: UnauthenticatedPageComponent,
  },
];

export const PENDING_ROUTES: Routes = singleComponent(
  'registration/pending',
  OrganizationPendingPageComponent,
);

export const REJECTED_ROUTES: Routes = singleComponent(
  'registration/rejected',
  OrganizationRejectedPageComponent,
);

export const ONBOARDING_ROUTES: Routes = singleComponent(
  'onboard',
  OrganizationOnboardPageComponent,
);

export const LOADING_ROUTES: Routes = [
  {
    path: '**',
    component: LoadingPageComponent,
  },
];

const REDIRECT_TO_HOME: string[] = [
  '',
  'registration/pending',
  'registration/rejected',
  'onboard',
];

const getProperRedirectUrl = (fallbackUrl: string) => {
  const url = localStorage.getItem('originalUrl') || fallbackUrl;
  return url;
};

export const CATALOG_REDIRECTS: Routes = REDIRECT_TO_HOME.map((path) => ({
  path,
  redirectTo: (() => getProperRedirectUrl('catalog'))(),
  pathMatch: 'full',
}));

export const FEATURE_DASHBOARD_ROUTE: Routes = [
  {
    path: 'dashboard',
    component: DashboardPageComponent,
    data: {
      requiresRole: ['USER'] satisfies UserRoleDto[],
    },
    canActivate: [requiresRole],
  },
];

export const AUTHORITY_PORTAL_ROUTES: Routes = [
  // participant own connector registration
  {
    path: 'my-organization/connectors/new',
    component: ChooseParticipantConnectorComponent,
    data: {
      requiresRole: ['KEY_USER'] satisfies UserRoleDto[],
    },
    canActivate: [requiresRole],
  },
  {
    path: 'my-organization/connectors/new/choose-provider',
    component: ChooseParticipantCaasComponent,
    data: {
      requiresRole: ['KEY_USER'] satisfies UserRoleDto[],
    },
    canActivate: [requiresRole],
  },
  {
    path: 'my-organization/connectors/new/provided',
    component: RequestConnectorPageComponent,
    data: {
      requiresRole: ['KEY_USER'] satisfies UserRoleDto[],
    },
    canActivate: [requiresRole],
  },
  {
    path: 'my-organization/connectors/new/self-hosted',
    component: RegisterConnectorPageComponent,
    data: {
      requiresRole: ['KEY_USER'] satisfies UserRoleDto[],
    },
    canActivate: [requiresRole],
  },

  {
    path: '',
    component: PortalLayoutComponent,
    children: [
      {
        path: 'catalog',
        component: CatalogPageComponent,
        data: {
          requiresRole: ['USER'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
        children: [
          {
            path: ':connectorId/:assetId',
            component: CatalogPageComponent,
            data: {
              requiresRole: ['USER'] satisfies UserRoleDto[],
            },
            canActivate: [requiresRole],
          },
        ],
      },
      {
        path: 'my-organization/data-offers',
        component: CatalogPageComponent,
        data: {
          requiresRole: ['USER'] satisfies UserRoleDto[],
          catalogType: 'my-data-offers',
        },
        canActivate: [requiresRole],
        children: [
          {
            path: ':connectorId/:assetId',
            component: CatalogPageComponent,
            data: {
              requiresRole: ['USER'] satisfies UserRoleDto[],
            },
            canActivate: [requiresRole],
          },
        ],
      },
      {
        path: 'control-center',
        component: ControlCenterPageComponent,
        data: {
          requiresRole: ['USER'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
        children: CONTROL_CENTER_ROUTES,
      },
      {
        path: 'users-and-roles',
        redirectTo: 'control-center/users-and-roles',
        pathMatch: 'full',
      },
      // My Organization Section
      {
        path: 'my-organization/connectors',
        component: ParticipantOwnConnectorListPageComponent,
        data: {
          requiresRole: ['USER'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },
      {
        path: 'my-organization/connectors/:connectorId',
        component: ParticipantOwnConnectorDetailPageComponent,
        data: {
          requiresRole: ['USER'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },

      // Operator Admin Section
      {
        path: 'operator/central-components',
        component: CentralComponentListPageComponent,
        data: {
          requiresRole: ['OPERATOR_ADMIN'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },
      {
        path: 'operator/central-components/provide',
        component: RegisterCentralComponentPageComponent,
        data: {
          requiresRole: ['OPERATOR_ADMIN'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },

      // Service Partner Section
      {
        path: 'service-partner/provided-connectors',
        component: SpConnectorListPageComponent,
        data: {
          requiresRole: ['SERVICE_PARTNER_ADMIN'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },
      {
        path: 'service-partner/provided-connectors/:connectorId/configure-connector',
        component: ConfigureProvidedConnectorPageComponent,
        data: {
          requiresRole: ['SERVICE_PARTNER_ADMIN'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },
      {
        path: 'service-partner/provided-connectors/provide-connector',
        component: ReserveProvidedConnectorPageComponent,
        data: {
          requiresRole: ['SERVICE_PARTNER_ADMIN'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },

      // Participant Section

      // Authority Section
      {
        path: 'authority/organizations',
        component: AuthorityOrganizationListPageComponent,
        data: {
          requiresRole: ['AUTHORITY_USER'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },
      {
        path: 'authority/organizations/:organizationId',
        component: AuthorityOrganizationListPageComponent,
        data: {
          requiresRole: ['AUTHORITY_USER'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },
      {
        path: 'authority/organizations/:organizationId/edit',
        component: AuthorityOrganizationEditPageComponent,
        data: {
          requiresRole: ['AUTHORITY_ADMIN'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },
      {
        path: 'authority/connectors',
        component: AuthorityConnectorListPageComponent,
        data: {
          requiresRole: ['AUTHORITY_USER'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },
      {
        path: 'operator/connectors',
        component: AuthorityConnectorListPageComponent,
        data: {
          requiresRole: ['OPERATOR_ADMIN'] satisfies UserRoleDto[],
        },
        canActivate: [requiresRole],
      },
    ],
  },
  {
    path: 'externalRedirect',
    canActivate: [EXTERNAL_URL_PROVIDER],
    component: PageNotFoundPageComponent,
  },
  {
    path: '**',
    component: PageNotFoundPageComponent,
  },
];

function singleComponent(path: string, component: Type<any>): Routes {
  return [
    {
      path,
      component,
    },
    {
      path: '**',
      redirectTo: path,
      pathMatch: 'full',
    },
  ];
}

@NgModule({
  imports: [RouterModule.forRoot(LOADING_ROUTES)],
  exports: [RouterModule],
  providers: [
    {
      provide: EXTERNAL_URL_PROVIDER,
      useValue: (route: ActivatedRouteSnapshot) => {
        const externalUrl = route.paramMap.get('externalUrl');
        window.open(externalUrl!!, '_self');
      },
    },
  ],
})
export class AppRoutingModule {}
