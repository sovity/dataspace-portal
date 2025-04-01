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
import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {Store} from '@ngxs/store';
import {DeploymentEnvironmentDto} from '@sovity.de/authority-portal-client';
import {GlobalState} from 'src/app/core/global-state/global-state';
import {SwitchEnvironment} from 'src/app/core/global-state/global-state-actions';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';

@Component({
  selector: 'app-env-switcher',
  templateUrl: './env-switcher.component.html',
})
export class EnvSwitcherComponent implements OnInit, OnDestroy {
  @Input() deploymentEnvironments: DeploymentEnvironmentDto[] = [];

  selectedEnvironmentId!: string;
  state!: GlobalState;

  private ngOnDestroy$ = new Subject();

  constructor(
    public store: Store,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  ngOnInit(): void {
    this.listenToAvailableEnvironments();
    this.setDefaultEnvironment();
  }

  selectEnvironment(environment: DeploymentEnvironmentDto) {
    this.selectedEnvironmentId = environment.environmentId;
    this.store.dispatch(new SwitchEnvironment(environment));
  }

  private listenToAvailableEnvironments() {
    this.globalStateUtils
      .getDeploymentEnvironments()
      .subscribe((environments: DeploymentEnvironmentDto[]) => {
        this.deploymentEnvironments = environments;
      });
  }

  private setDefaultEnvironment() {
    this.globalStateUtils
      .getDeploymentEnvironmentId()
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((environmentId: string) => {
        this.selectedEnvironmentId = environmentId;
      });
  }

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
