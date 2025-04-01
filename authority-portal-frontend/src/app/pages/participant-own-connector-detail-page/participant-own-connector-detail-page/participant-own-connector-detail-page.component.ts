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
import {Clipboard} from '@angular/cdk/clipboard';
import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {
  Subject,
  distinctUntilChanged,
  map,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs';
import {Store} from '@ngxs/store';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {getConnectorsTypeClasses} from 'src/app/core/utils/ui-utils';
import {TitleBarConfig} from 'src/app/shared/common/portal-layout/title-bar/title-bar.model';
import {ChildComponentInput} from 'src/app/shared/common/slide-over/slide-over.model';
import {
  getConnectorStatusText,
  getConnectorsTypeText,
} from '../../../core/utils/mappers/dto-ui-mapper';
import {DeleteOwnConnector} from '../../participant-own-connector-list-page/state/participant-own-connector-list-page-actions';
import {
  RefreshConnector,
  SetConnectorId,
} from '../state/participant-own-connector-detail-page-actions';
import {
  DEFAULT_PARTICIPANT_OWN_CONNECTOR_DETAIL_PAGE_STATE,
  ParticipantOwnConnectorDetailPageState,
} from '../state/participant-own-connector-detail-page-state';
import {ParticipantOwnConnectorDetailPageStateImpl} from '../state/participant-own-connector-detail-page-state-impl';

@Component({
  selector: 'app-participant-own-connector-detail-page',
  templateUrl: './participant-own-connector-detail-page.component.html',
})
export class ParticipantOwnConnectorDetailPageComponent
  implements OnInit, OnDestroy
{
  connectorId!: string;
  titleBarConfig!: TitleBarConfig;
  showModal = false;

  state = DEFAULT_PARTICIPANT_OWN_CONNECTOR_DETAIL_PAGE_STATE;

  getConnectorsTypeClasses = getConnectorsTypeClasses;
  getConnectorStatusText = getConnectorStatusText;

  private ngOnDestroy$ = new Subject();

  constructor(
    private store: Store,
    @Inject('childComponentInput') childComponentInput: ChildComponentInput,
    private globalStateUtils: GlobalStateUtils,
    private clipboard: Clipboard,
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
      .select<ParticipantOwnConnectorDetailPageState>(
        ParticipantOwnConnectorDetailPageStateImpl,
      )
      .pipe(
        takeUntil(this.ngOnDestroy$),
        tap((state) => (this.state = state)),
        switchMap(() => {
          return this.globalStateUtils.userRoles$.pipe(
            map((roles) => roles.has('KEY_USER')),
            distinctUntilChanged(),
          );
        }),
      )
      .subscribe((hasRole) => {
        this.state.connector.ifReady((data) => {
          this.titleBarConfig = {
            title: data.connectorName,
            icon: 'connector-2',
            status: getConnectorsTypeText(data.type),
            statusStyle: this.getConnectorsTypeClasses(data.type),
            tabs: [],
            actionMenu: hasRole
              ? {
                  id: 'actionMenu',
                  menuOptions: [
                    {
                      label: 'Delete Connector',
                      icon: 'delete',
                      event: () => this.deleteConnector(),
                      isDisabled: false,
                    },
                  ],
                }
              : undefined,
          };
        });
      });
  }

  deleteConnector() {
    this.showModal = true;
  }

  confirmDeleteConnector() {
    this.store.dispatch(new DeleteOwnConnector(this.connectorId));
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
