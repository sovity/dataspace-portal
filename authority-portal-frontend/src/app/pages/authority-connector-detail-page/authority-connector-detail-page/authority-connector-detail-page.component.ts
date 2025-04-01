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
import {Component, HostBinding, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {
  ConnectorDetailsDto,
  UserInfo,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {getConnectorsTypeClasses} from 'src/app/core/utils/ui-utils';
import {
  ActionMenu,
  TitleBarConfig,
} from 'src/app/shared/common/portal-layout/title-bar/title-bar.model';
import {ChildComponentInput} from 'src/app/shared/common/slide-over/slide-over.model';
import {
  getConnectorStatusText,
  getConnectorsTypeText,
} from '../../../core/utils/mappers/dto-ui-mapper';
import {DeleteConnector} from '../../authority-connector-list-page/state/authority-connector-list-page-actions';
import {
  RefreshConnector,
  SetConnectorId,
} from '../state/authority-connector-detail-page-actions';
import {
  AuthorityConnectorDetailPageState,
  DEFAULT_AUTHORITY_CONNECTOR_DETAIL_PAGE_STATE,
} from '../state/authority-connector-detail-page-state';
import {AuthorityConnectorDetailPageStateImpl} from '../state/authority-connector-detail-page-state-impl';

@Component({
  selector: 'app-authority-connector-detail-page',
  templateUrl: './authority-connector-detail-page.component.html',
})
export class AuthorityConnectorDetailPageComponent
  implements OnInit, OnDestroy
{
  @HostBinding('class.overflow-y-auto') overflowYAuto = true;
  connectorId!: string;
  titleBarConfig!: TitleBarConfig;
  showModal = false;
  userInfo?: UserInfo;

  state = DEFAULT_AUTHORITY_CONNECTOR_DETAIL_PAGE_STATE;
  getConnectorsTypeClasses = getConnectorsTypeClasses;
  getConnectorStatusText = getConnectorStatusText;

  constructor(
    private store: Store,
    @Inject('childComponentInput') childComponentInput: ChildComponentInput,
    private globalStateUtils: GlobalStateUtils,
  ) {
    this.connectorId = childComponentInput.id;
  }

  ngOnInit() {
    this.store.dispatch(new SetConnectorId(this.connectorId));
    this.store.dispatch(RefreshConnector);

    this.startListeningToUserInfo();
    this.startListeningToState();
  }

  private startListeningToUserInfo() {
    this.globalStateUtils.userInfo$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((userInfo) => {
        this.userInfo = userInfo;
      });
  }

  private startListeningToState() {
    this.store
      .select<AuthorityConnectorDetailPageState>(
        AuthorityConnectorDetailPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
        let actionMenu: ActionMenu = {
          id: 'actionMenu',
          menuOptions: [
            {
              label: 'Delete Connector',
              icon: 'delete',
              event: () => this.deleteConnectorMenuItemClick(),
              isDisabled:
                !this.userInfo?.roles.includes(UserRoleDto.OperatorAdmin) ??
                true,
            },
          ],
        };
        this.state.connector.ifReady((data) =>
          this.setupConnectorTitleBar(data, actionMenu),
        );
      });
  }

  setupConnectorTitleBar(
    connector: ConnectorDetailsDto,
    actionMenu?: ActionMenu,
  ) {
    this.titleBarConfig = {
      title: connector.connectorName,
      icon: 'connector-2',
      status: getConnectorsTypeText(connector.type),
      statusStyle: this.getConnectorsTypeClasses(connector.type),
      tabs: [],
    };
    if (actionMenu) this.titleBarConfig.actionMenu = actionMenu;
  }

  refresh() {
    this.store.dispatch(RefreshConnector);
  }

  cancelDeleteConnector() {
    this.showModal = false;
  }

  confirmDeleteConnector() {
    this.showModal = false;
    this.store.dispatch(new DeleteConnector(this.connectorId));
  }

  deleteConnectorMenuItemClick() {
    this.showModal = true;
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
