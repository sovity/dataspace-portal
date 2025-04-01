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
  ConnectorOverviewEntryDto,
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
import {ParticipantOwnConnectorDetailPageComponent} from '../../participant-own-connector-detail-page/participant-own-connector-detail-page/participant-own-connector-detail-page.component';
import {RefreshConnectorSilent} from '../../participant-own-connector-detail-page/state/participant-own-connector-detail-page-actions';
import {
  CloseConnectorDetail,
  GetOwnOrganizationConnectors,
  GetOwnOrganizationConnectorsSilent,
  ShowConnectorDetail,
} from '../state/participant-own-connector-list-page-actions';
import {
  DEFAULT_PARTICIPANT_OWN_CONNECTOR_LIST_PAGE_STATE,
  ParticipantOwnConnectorListPageState,
} from '../state/participant-own-connector-list-page-state';
import {ParticipantOwnConnectorListPageStateImpl} from '../state/participant-own-connector-list-page-state-impl';

@Component({
  selector: 'app-participant-own-connector-list-page',
  templateUrl: './participant-own-connector-list-page.component.html',
})
export class ParticipantOwnConnectorListPageComponent
  implements OnInit, OnDestroy
{
  state = DEFAULT_PARTICIPANT_OWN_CONNECTOR_LIST_PAGE_STATE;
  showDetail: boolean = false;
  slideOverConfig!: SlideOverConfig;
  componentToRender = ParticipantOwnConnectorDetailPageComponent;
  headerConfig!: HeaderBarConfig;
  filterBarConfig!: FilterBarConfig;
  frontendLinkClicked: boolean = false;

  getConnectorsTypeClasses = getConnectorsTypeClasses;

  private ngOnDestroy$ = new Subject();

  constructor(
    private store: Store,
    private globalStateUtils: GlobalStateUtils,
    private router: Router,
    private slideOverService: SlideOverService,
    private titleService: Title,
  ) {
    this.titleService.setTitle('Connectors');
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
      title: 'Connectors',
      subtitle: 'List of all connectors of your organization',
      headerActions: [
        {
          label: 'Add Connector',
          action: () =>
            this.router.navigate(['/my-organization/connectors/new']),
          permissions: [UserRoleDto.KeyUser],
        },
      ],
    };
  }

  refresh() {
    this.store.dispatch(GetOwnOrganizationConnectors);

    interval(30000)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe(() => {
        this.store.dispatch(GetOwnOrganizationConnectorsSilent);
        if (this.state.showDetail) {
          this.store.dispatch(RefreshConnectorSilent);
        }
      });
  }

  private startListeningToState() {
    this.store
      .select<ParticipantOwnConnectorListPageState>(
        ParticipantOwnConnectorListPageStateImpl,
      )
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

  openDetailPage(connector: ConnectorOverviewEntryDto) {
    if (this.frontendLinkClicked) {
      this.frontendLinkClicked = false;
      return;
    }

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
}
