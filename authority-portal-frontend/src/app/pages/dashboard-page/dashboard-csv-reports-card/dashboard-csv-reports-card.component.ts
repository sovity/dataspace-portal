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
import {Component, HostBinding, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {
  DeploymentEnvironmentDto,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';

export interface ReportLink {
  title: string;
  subTitle: string;
  url: string;
  roles: UserRoleDto[];
}

@Component({
  selector: 'app-dashboard-csv-reports-card',
  templateUrl: './dashboard-csv-reports-card.component.html',
})
export class DashboardCsvReportsCardComponent implements OnInit, OnDestroy {
  @HostBinding('class.border')
  @HostBinding('class.border-gray-100')
  @HostBinding('class.shadow')
  @HostBinding('class.rounded-xl')
  @HostBinding('class.p-6')
  @HostBinding('class.bg-white')
  cls = true;

  reportLinks: ReportLink[] = [];

  constructor(private globalStateUtils: GlobalStateUtils) {}

  ngOnInit(): void {
    this.startListeningToState();
  }

  startListeningToState(): void {
    this.globalStateUtils.deploymentEnvironment$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((env) => {
        this.reportLinks = this.buildReportLinks(env);
      });
  }

  buildReportLinks(env: DeploymentEnvironmentDto): ReportLink[] {
    return [
      {
        title: `Connectors`,
        subTitle: '.csv',
        url: `/api/reporting/connectors?environmentId=${env.environmentId}`,
        roles: ['USER'],
      },
      {
        title: `Data Offers Report`,
        subTitle: '.csv',
        url: `/api/reporting/data-offers?environmentId=${env.environmentId}`,
        roles: ['USER'],
      },
      {
        title: `System Stability Report`,
        subTitle: '.csv',
        url: `/api/reporting/system-stability?environmentId=${env.environmentId}`,
        roles: ['USER'],
      },
      {
        title: 'Users and Roles Report',
        subTitle: '.csv',
        url: '/api/reporting/users',
        roles: ['AUTHORITY_ADMIN', 'AUTHORITY_USER'],
      },
    ];
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
