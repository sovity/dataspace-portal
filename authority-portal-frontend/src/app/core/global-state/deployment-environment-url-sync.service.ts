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
import {Injectable} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable, combineLatest, takeUntil} from 'rxjs';
import {take} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {SwitchEnvironment} from './global-state-actions';
import {GlobalStateUtils} from './global-state-utils';
import {DeploymentEnvironmentDto} from "@sovity.de/authority-portal-client";

@Injectable({providedIn: 'root'})
export class DeploymentEnvironmentUrlSyncService {
  private readonly environmentIdQueryParam = 'environmentId';

  constructor(
    private store: Store,
    private router: Router,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  updateFromUrlOnce(route: ActivatedRoute, until$: Observable<unknown>) {
    const environmentId: string | undefined =
      route.snapshot.queryParams[this.environmentIdQueryParam];
    if (!environmentId) {
      return;
    }

    this.withAvailableEnvironments((available, current) => {
      if (current.environmentId === environmentId) {
        return;
      }

      const desiredEnvironment = available.find(
        (it) => it.environmentId === environmentId,
      );

      if (desiredEnvironment) {
        this.store.dispatch(new SwitchEnvironment(desiredEnvironment));
      }
    }, until$);
  }

  syncToUrl(activatedRoute: ActivatedRoute, until$: Observable<unknown>) {
    let queryParamEnvironmentId: string | undefined;

    activatedRoute.queryParams
      .pipe(takeUntil(until$))
      .subscribe(
        (params) =>
          (queryParamEnvironmentId = params[this.environmentIdQueryParam]),
      );

    this.globalStateUtils.deploymentEnvironment$
      .pipe(takeUntil(until$))
      .subscribe((environment) => {
        if (environment.environmentId === queryParamEnvironmentId) {
          return;
        }

        this.router.navigate([], {
          queryParams: {
            [this.environmentIdQueryParam]: environment.environmentId,
          },
          queryParamsHandling: 'merge',
        });
      });
  }

  private withAvailableEnvironments(
    fn: (
      available: DeploymentEnvironmentDto[],
      current: DeploymentEnvironmentDto,
    ) => void,
    until$: Observable<unknown>,
  ) {
    combineLatest([
      this.globalStateUtils.getDeploymentEnvironments(),
      this.globalStateUtils.deploymentEnvironment$,
    ])
      .pipe(takeUntil(until$), take(1))
      .subscribe(([deploymentEnvironments, selectedEnvironment]) => {
        fn(deploymentEnvironments, selectedEnvironment);
      });
  }
}
