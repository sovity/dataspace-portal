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
import {MatDialog} from '@angular/material/dialog';
import {Title} from '@angular/platform-browser';
import {Router} from '@angular/router';
import {Subject} from 'rxjs';
import {filter, takeUntil} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {
  CentralComponentDto,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {HeaderBarConfig} from 'src/app/shared/common/header-bar/header-bar.model';
import {ConfirmationDialogComponent} from '../../../shared/common/confirmation-dialog/confirmation-dialog.component';
import {ConfirmationDialog} from '../../../shared/common/confirmation-dialog/confirmation-dialog.model';
import {
  DeleteCentralComponent,
  RefreshCentralComponents,
} from '../state/central-component-list-page-actions';
import {
  CentralComponentListPageState,
  DEFAULT_CENTRAL_COMPONENT_LIST_PAGE_STATE,
} from '../state/central-component-list-page-state';
import {CentralComponentListPageStateImpl} from '../state/central-component-list-page-state-impl';

@Component({
  selector: 'app-central-component-list-page',
  templateUrl: './central-component-list-page.component.html',
})
export class CentralComponentListPageComponent implements OnInit, OnDestroy {
  @HostBinding('class.overflow-y-auto')
  @HostBinding('class.overflow-x-hidden')
  cls = true;

  state = DEFAULT_CENTRAL_COMPONENT_LIST_PAGE_STATE;

  headerConfig!: HeaderBarConfig;

  private ngOnDestroy$ = new Subject();

  constructor(
    private store: Store,
    private globalStateUtils: GlobalStateUtils,
    private router: Router,
    private dialog: MatDialog,
    private titleService: Title,
  ) {
    this.titleService.setTitle('Central Components');
  }

  ngOnInit() {
    this.refresh();
    this.startListeningToState();
    this.startRefreshingOnEnvChange();
    this.initializeHeaderBar();
  }

  refresh() {
    this.store.dispatch(RefreshCentralComponents);
  }

  initializeHeaderBar() {
    this.headerConfig = {
      title: 'Central Components',
      subtitle: 'List of Central Dataspace Components registered at the DAPS.',
      headerActions: [
        {
          label: 'Provide Central Component',
          action: () =>
            this.router.navigate(['/operator/central-components/provide']),
          permissions: [UserRoleDto.OperatorAdmin],
        },
      ],
    };
  }

  showDeleteModal(centralComponent: CentralComponentDto) {
    const data: ConfirmationDialog = {
      title: `Delete Central Component`,
      messageBody: `Central Component '${centralComponent.name}' with ID '${centralComponent.centralComponentId}' will be unregistered from the DAPS.`,
      actionButtons: [
        {
          action: 'DELETE',
          label: 'Delete',
          style: 'btn-accent-danger',
        },
      ],
    };
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: window.innerWidth > 640 ? '40%' : '60%',
      data: data,
    });
    dialogRef
      .afterClosed()
      .pipe(filter((it) => it === 'DELETE'))
      .subscribe(() =>
        this.store.dispatch(new DeleteCentralComponent(centralComponent)),
      );
  }

  private startListeningToState() {
    this.store
      .select<CentralComponentListPageState>(CentralComponentListPageStateImpl)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
      });
  }

  private startRefreshingOnEnvChange() {
    this.globalStateUtils.onDeploymentEnvironmentChangeSkipFirst({
      ngOnDestroy$: this.ngOnDestroy$,
      onChanged: () => {
        this.refresh();
      },
    });
  }

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
