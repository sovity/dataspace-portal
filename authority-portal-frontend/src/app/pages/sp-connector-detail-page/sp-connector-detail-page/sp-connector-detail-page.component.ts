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
import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {ConnectorDetailsDto} from '@sovity.de/authority-portal-client';
import {getConnectorsTypeClasses} from 'src/app/core/utils/ui-utils';
import {TitleBarConfig} from 'src/app/shared/common/portal-layout/title-bar/title-bar.model';
import {ChildComponentInput} from 'src/app/shared/common/slide-over/slide-over.model';
import {getConnectorsTypeText} from '../../../core/utils/mappers/dto-ui-mapper';
import {DeleteProvidedConnector} from '../../sp-connector-list-page/state/sp-connector-list-page-actions';
import {
  RefreshConnector,
  SetConnectorId,
} from '../state/sp-connector-detail-page-actions';
import {
  DEFAULT_SP_CONNECTOR_DETAIL_PAGE_STATE,
  SpConnectorDetailPageState,
} from '../state/sp-connector-detail-page-state';
import {SpConnectorDetailPageStateImpl} from '../state/sp-connector-detail-page-state-impl';

@Component({
  selector: 'app-sp-connector-detail-page',
  templateUrl: './sp-connector-detail-page.component.html',
})
export class SpConnectorDetailPageComponent implements OnInit, OnDestroy {
  connectorId!: string;
  titleBarConfig!: TitleBarConfig;
  showModal = false;

  state = DEFAULT_SP_CONNECTOR_DETAIL_PAGE_STATE;

  private ngOnDestroy$ = new Subject();

  constructor(
    private store: Store,
    @Inject('childComponentInput') childComponentInput: ChildComponentInput,
  ) {
    this.connectorId = childComponentInput.id;
  }

  ngOnInit() {
    this.store.dispatch(new SetConnectorId(this.connectorId));
    this.store.dispatch(RefreshConnector);

    this.startListeningToState();
  }

  private startListeningToState() {
    this.store
      .select<SpConnectorDetailPageState>(SpConnectorDetailPageStateImpl)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
        this.state.connector.ifReady((data) =>
          this.setupConnectorTitleBar(data),
        );
      });
  }

  setupConnectorTitleBar(connector: ConnectorDetailsDto) {
    this.titleBarConfig = {
      title: connector.connectorName,
      icon: 'connector-2',
      status: getConnectorsTypeText(connector.type),
      statusStyle: getConnectorsTypeClasses(connector.type),
      tabs: [],
      actionMenu: {
        id: 'actionMenu',
        menuOptions: [
          {
            label: 'Delete Connector',
            icon: 'delete',
            event: () => this.deleteConnector(),
            isDisabled: false,
          },
        ],
      },
    };
  }

  deleteConnector() {
    this.showModal = true;
  }

  confirmDeleteConnector() {
    this.store.dispatch(new DeleteProvidedConnector(this.connectorId));
    this.showModal = false;
  }

  cancelDeleteConnector() {
    this.showModal = false;
  }

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
