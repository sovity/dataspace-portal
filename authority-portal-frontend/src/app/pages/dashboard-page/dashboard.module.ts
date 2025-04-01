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
import {NgModule} from '@angular/core';
import {NgChartsModule} from 'ng2-charts';
import {SharedModule} from 'src/app/shared/shared.module';
import {DashboardComponentUptimeCardComponent} from './dashboard-component-uptime-card/dashboard-component-uptime-card.component';
import {DashboardConnectorCardComponent} from './dashboard-connector-card/dashboard-connector-card.component';
import {DashboardCsvReportsCardComponent} from './dashboard-csv-reports-card/dashboard-csv-reports-card.component';
import {DashboardPageComponent} from './dashboard-page/dashboard-page.component';
import {DonutChartComponent} from './donut-chart/donut-chart.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,

    // Authority Portal
    SharedModule,

    // Third party
    NgChartsModule,
  ],
  declarations: [
    DashboardPageComponent,
    DonutChartComponent,
    DashboardComponentUptimeCardComponent,
    DashboardConnectorCardComponent,
    DashboardCsvReportsCardComponent,
  ],
  exports: [DashboardPageComponent],
})
export class DashboardPageModule {}
