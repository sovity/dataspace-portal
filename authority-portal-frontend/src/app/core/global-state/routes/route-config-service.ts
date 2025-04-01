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
import {Router, Routes} from '@angular/router';
import {UserInfo} from '@sovity.de/authority-portal-client';
import {Fetched} from 'src/app/core/utils/fetched';
import {
  AUTHORITY_PORTAL_ROUTES,
  CATALOG_REDIRECTS,
  FEATURE_DASHBOARD_ROUTE,
  LOADING_ROUTES,
  ONBOARDING_ROUTES,
  PENDING_ROUTES,
  REJECTED_ROUTES,
  UNAUTHENTICATED_ROUTES,
} from '../../../app-routing.module';
import {ActiveFeatureSet} from '../../services/config/active-feature-set';
import {AuthorityPortalPageSet} from './authority-portal-page-set';
import {UrlBeforeLoginService} from './url-before-login.service';

@Injectable({providedIn: 'root'})
export class RouteConfigService {
  mapping: Record<AuthorityPortalPageSet, Routes> = {
    LOADING: LOADING_ROUTES,
    UNAUTHENTICATED: UNAUTHENTICATED_ROUTES,
    REJECTED: REJECTED_ROUTES,
    PENDING: PENDING_ROUTES,
    ONBOARDING_PROCESS: ONBOARDING_ROUTES,
    AUTHORITY_PORTAL: AUTHORITY_PORTAL_ROUTES,
  };

  readonly defaultRoute = '/catalog';

  constructor(
    private router: Router,
    private urlBeforeLoginService: UrlBeforeLoginService,
    private activeFeatureSet: ActiveFeatureSet,
  ) {}

  decidePageSet(userInfoFetched: Fetched<UserInfo>): AuthorityPortalPageSet {
    if (!userInfoFetched.isReady) {
      return 'LOADING';
    }

    const userInfo = userInfoFetched.data;
    if (userInfo.authenticationStatus === 'UNAUTHENTICATED') {
      return 'UNAUTHENTICATED';
    }

    switch (userInfo.registrationStatus) {
      case 'ACTIVE':
        return 'AUTHORITY_PORTAL';
      case 'ONBOARDING':
        return 'ONBOARDING_PROCESS';
      case 'PENDING':
        return 'PENDING';
      case 'REJECTED':
      default:
        return 'REJECTED';
    }
  }

  switchRouteConfig(
    previousPageSet: AuthorityPortalPageSet,
    nextPageSet: AuthorityPortalPageSet,
  ) {
    if (previousPageSet == nextPageSet) {
      return;
    }

    // Change routes
    const routes = [...this.mapping[nextPageSet]];

    if (nextPageSet === 'AUTHORITY_PORTAL') {
      const rootRouteIndex = routes.findIndex((r) => r.path === '');

      if (rootRouteIndex !== -1) {
        const rootRoute = routes[rootRouteIndex];
        const existingChildren = rootRoute.children || [];

        // Add home route depending on feature set
        const newChildren = [...existingChildren, ...CATALOG_REDIRECTS];

        // Add additional routes depending on feature set & configuration
        if (this.activeFeatureSet.isDashboardEnabled()) {
          newChildren.push(...FEATURE_DASHBOARD_ROUTE);
        }

        routes[rootRouteIndex] = {
          ...rootRoute,
          children: newChildren,
        };
      }
    }

    this.router.resetConfig(routes);

    if (
      previousPageSet === 'ONBOARDING_PROCESS' &&
      nextPageSet === 'AUTHORITY_PORTAL'
    ) {
      this.router
        .navigateByUrl('/random-redirect-for-force-refresh', {
          skipLocationChange: true,
        })
        .then(() => {
          if (this.urlBeforeLoginService.originalUrl != '') {
            this.urlBeforeLoginService.goToOriginalUrl();
          } else {
            this.router.navigate([this.defaultRoute]);
          }
        });
    } else {
      // Force refresh
      this.forceRefreshCurrentRoute();
    }
  }

  forceRefreshCurrentRoute() {
    const currentUrl = this.router.url;
    this.router
      .navigateByUrl('/random-redirect-for-force-refresh', {
        skipLocationChange: true,
      })
      .then(() => this.router.navigateByUrl(currentUrl));
  }
}
