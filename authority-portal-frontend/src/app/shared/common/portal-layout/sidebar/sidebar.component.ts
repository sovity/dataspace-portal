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
  HostListener,
  Inject,
  OnDestroy,
  OnInit,
} from '@angular/core';
import {Subject, distinctUntilChanged, takeUntil} from 'rxjs';
import {map} from 'rxjs/operators';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {ActiveFeatureSet} from '../../../../core/services/config/active-feature-set';
import {SidebarSection} from './sidebar.model';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent implements OnInit, OnDestroy {
  isExpandedMenu: boolean = true;
  sidebarSections: SidebarSection[] = [];
  private ngOnDestroy$ = new Subject();

  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private globalStateUtils: GlobalStateUtils,
    private activeFeatureSet: ActiveFeatureSet,
  ) {}

  ngOnInit() {
    this.startListeningToEnvironmentChanges();
  }

  startListeningToEnvironmentChanges(): void {
    this.globalStateUtils.userInfo$
      .pipe(
        map((it) => it.organizationName),
        distinctUntilChanged(),
        takeUntil(this.ngOnDestroy$),
      )
      .subscribe((organizationName) => {
        this.setSideBarSections(organizationName);
      });
  }

  // Listen for window resize events
  @HostListener('window:resize', ['$event'])
  onResize(event: Event): void {
    this.checkWindowWidth();
  }

  // Function to check window width and update isExpandedMenu accordingly
  checkWindowWidth(): void {
    this.isExpandedMenu = window.innerWidth > 768; // Set the breakpoint as per your design
  }

  navigateHome() {
    window.open('catalog', '_self');
  }

  setSideBarSections(organizationName: string): void {
    this.sidebarSections = [
      {
        title: 'Home',
        userRoles: ['USER'],
        menus: [
          {
            title: 'Data Catalog',
            icon: 'tag',
            rLink: '/catalog',
          },
          {
            title: 'Dashboard',
            icon: 'dashboard',
            rLink: '/dashboard',
            isDisabled: !this.activeFeatureSet.isDashboardEnabled(),
          },
        ],
      },
      {
        title: organizationName ?? 'My Organization',
        userRoles: ['USER'],
        menus: [
          {
            title: 'My Organization',
            icon: 'home',
            rLink: '/control-center/my-organization',
          },
          {
            title: 'My Data Offers',
            icon: 'tag',
            rLink: `/my-organization/data-offers`,
          },
          {
            title: 'My Connectors',
            icon: 'connector',
            rLink: '/my-organization/connectors',
          },
        ],
      },
      {
        title: 'Operator Section',
        userRoles: ['OPERATOR_ADMIN'],
        menus: [
          {
            title: 'All Connectors',
            icon: 'connector',
            rLink: '/operator/connectors',
          },
          {
            title: 'Central Components',
            icon: 'extension',
            rLink: '/operator/central-components',
          },
        ],
      },
      {
        title: 'Service Partner Section',
        userRoles: ['SERVICE_PARTNER_ADMIN'],
        menus: [
          {
            title: 'Provided Connectors',
            icon: 'connector',
            rLink: '/service-partner/provided-connectors',
          },
        ],
      },
      {
        title: 'Authority Section',
        userRoles: ['AUTHORITY_USER'],
        menus: [
          {
            title: 'Organizations',
            icon: 'building-office-2',
            rLink: '/authority/organizations',
          },
          {
            title: 'All Connectors',
            icon: 'connector',
            rLink: '/authority/connectors',
          },
        ],
      },
      {
        title: 'Support',
        userRoles: ['USER'],
        menus: [
          {
            title: 'Support',
            icon: 'question-mark-circle',
            rLink: this.appConfig.supportUrl,
            isExternalLink: true,
          },
        ],
      },
    ];
  }

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
