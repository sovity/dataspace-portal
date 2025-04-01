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
import {Component, OnDestroy, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {Router} from '@angular/router';
import {Subject, interval} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {
  ConnectorTypeDto,
  ProvidedConnectorOverviewEntryDto,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {SlideOverService} from 'src/app/core/services/slide-over.service';
import {sliderOverNavigation} from 'src/app/core/utils/helper';
import {getConnectorsTypeClasses} from 'src/app/core/utils/ui-utils';
import {FilterBarConfig} from 'src/app/shared/common/filter-bar/filter-bar.model';
import {HeaderBarConfig} from 'src/app/shared/common/header-bar/header-bar.model';
import {
  NavigationType,
  SlideOverAction,
  SlideOverConfig,
} from 'src/app/shared/common/slide-over/slide-over.model';
import {getConnectorsTypeText} from '../../../core/utils/mappers/dto-ui-mapper';
import {SpConnectorDetailPageComponent} from '../../sp-connector-detail-page/sp-connector-detail-page/sp-connector-detail-page.component';
import {RefreshConnectorSilent} from '../../sp-connector-detail-page/state/sp-connector-detail-page-actions';
import {
  CloseConnectorDetail,
  GetProvidedConnectors,
  GetProvidedConnectorsSilent,
  ShowConnectorDetail,
} from '../state/sp-connector-list-page-actions';
import {
  DEFAULT_SP_CONNECTOR_LIST_PAGE_STATE,
  SpConnectorListPageState,
} from '../state/sp-connector-list-page-state';
import {SpConnectorListPageStateImpl} from '../state/sp-connector-list-page-state-impl';

@Component({
  selector: 'app-sp-connector-list-page',
  templateUrl: './sp-connector-list-page.component.html',
})
export class SpConnectorListPageComponent implements OnInit, OnDestroy {
  state = DEFAULT_SP_CONNECTOR_LIST_PAGE_STATE;
  showDetail: boolean = false;
  slideOverConfig!: SlideOverConfig;
  componentToRender = SpConnectorDetailPageComponent;
  headerConfig!: HeaderBarConfig;
  filterBarConfig!: FilterBarConfig;

  getConnectorsTypeClasses = getConnectorsTypeClasses;

  private ngOnDestroy$ = new Subject();

  constructor(
    private store: Store,
    private globalStateUtils: GlobalStateUtils,
    private router: Router,
    private slideOverService: SlideOverService,
    private titleService: Title,
  ) {
    this.titleService.setTitle('Provided Connectors');
  }

  ngOnInit() {
    this.initializeHeaderBar();
    this.initializeFilterBar();
    this.refresh();
    this.startListeningToState();
    this.startRefreshingOnEnvChange();
  }

  initializeFilterBar() {
    this.filterBarConfig = {
      filters: [
        {
          id: 'type',
          label: 'Type',
          icon: 'tag',
          type: 'MULTISELECT',
          options: [],
        },
        {
          id: 'status',
          label: 'Status',
          type: 'SELECT',
          icon: 'status',
          options: [],
        },
      ],
    };
  }

  initializeHeaderBar() {
    this.headerConfig = {
      title: 'Provided Connectors',
      subtitle:
        'List of connectors provided by you in your capacity as a Service Partner.',
      headerActions: [
        {
          label: 'Provide Connector',
          action: () =>
            this.router.navigate([
              'service-partner/provided-connectors/provide-connector',
            ]),
          permissions: [UserRoleDto.ServicePartnerAdmin],
        },
      ],
    };
  }

  refresh() {
    this.store.dispatch(GetProvidedConnectors);
    interval(3000)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe(() => {
        this.store.dispatch(GetProvidedConnectorsSilent);
        if (this.showDetail) {
          this.store.dispatch(RefreshConnectorSilent);
        }
      });
  }

  startListeningToState() {
    this.store
      .select<SpConnectorListPageState>(SpConnectorListPageStateImpl)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
        this.showDetail = state.showDetail;
      });
  }

  startRefreshingOnEnvChange() {
    this.globalStateUtils.onDeploymentEnvironmentChangeSkipFirst({
      ngOnDestroy$: this.ngOnDestroy$,
      onChanged: () => {
        this.refresh();
      },
    });
  }

  configureConnector(connectorId: string) {
    this.router.navigate([
      `service-partner/provided-connectors/${connectorId}/configure-connector`,
    ]);
  }

  handleNavigation(direction: SlideOverAction, currentConnectorId: string) {
    let totalConnectors = this.state.connectors.data.length;
    let currentIndex = this.state.connectors.data.findIndex(
      (connector) => connector.id === currentConnectorId,
    );
    let nextIndex = sliderOverNavigation(
      direction,
      currentIndex,
      totalConnectors,
    );

    this.slideOverConfig = {
      ...this.slideOverConfig,
      childComponentInput: {
        id: this.state.connectors.data[nextIndex].id,
      },
      label: this.state.connectors.data[nextIndex].name,
    };
    this.slideOverService.setSlideOverConfig(this.slideOverConfig);
  }

  closeDetailPage() {
    this.store.dispatch(CloseConnectorDetail);
    this.slideOverService.slideOverReset();
  }

  openDetailPage(connector: ProvidedConnectorOverviewEntryDto) {
    this.slideOverConfig = {
      childComponentInput: {
        id: connector.id,
      },
      label: connector.name,
      icon: 'connector',
      showNavigation: this.state.connectors.data.length > 1,
      navigationType: NavigationType.STEPPER,
    };
    this.slideOverService.setSlideOverConfig(this.slideOverConfig);
    this.store.dispatch(ShowConnectorDetail);
  }

  ngOnDestroy(): void {
    this.closeDetailPage();
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  protected readonly getConnectorsTypeText = getConnectorsTypeText;
  protected readonly ConnectorTypeDto = ConnectorTypeDto;
}
