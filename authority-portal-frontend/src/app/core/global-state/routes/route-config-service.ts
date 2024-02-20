import {Injectable} from '@angular/core';
import {Router, Routes} from '@angular/router';
import {UserInfo} from '@sovity.de/authority-portal-client';
import {
  AUTHORITY_PORTAL_ROUTES,
  LOADING_ROUTES,
  ONBOARDING_ROUTES,
  PENDING_ROUTES,
  REJECTED_ROUTES,
  UNAUTHENTICATED_ROUTES,
} from '../../../app-routing.module';
import {Fetched} from '../../utils/fetched';
import {AuthorityPortalPageSet} from './authority-portal-page-set';

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

  constructor(private router: Router) {}

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
    this.router.resetConfig(this.mapping[nextPageSet]);

    // Force refresh
    this.forceRefreshCurrentRoute();
  }

  forceRefreshCurrentRoute() {
    const currentUrl = this.router.url;
    this.router
      .navigateByUrl('/random-redirect-for-force-refresh', {
        skipLocationChange: true,
      })
      .then(() => this.router.navigate([currentUrl]));
  }
}
