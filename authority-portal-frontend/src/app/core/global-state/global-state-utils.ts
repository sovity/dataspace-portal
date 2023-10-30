import {Injectable} from '@angular/core';
import {Observable, distinctUntilChanged, filter, first, share} from 'rxjs';
import {
  distinctUntilKeyChanged,
  map,
  shareReplay,
  skip,
  switchMap,
  take,
  takeUntil,
  tap,
} from 'rxjs/operators';
import {Select, Store, createSelector} from '@ngxs/store';
import {
  DeploymentEnvironmentDto,
  UserInfo,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {GlobalState} from './global-state';
import {GlobalStateImpl} from './global-state-impl';

@Injectable({providedIn: 'root'})
export class GlobalStateUtils {
  userInfo$: Observable<UserInfo> = this.store
    .select<GlobalState>(GlobalStateImpl)
    .pipe(
      map((state) => state.userInfo),
      distinctUntilChanged(),
      filter((it) => it.isReady),
      map((it) => it.data),
      shareReplay(),
    );

  deploymentEnvironment$: Observable<DeploymentEnvironmentDto> = this.store
    .select<GlobalState>(GlobalStateImpl)
    .pipe(
      map((state) => state.selectedEnvironment),
      filter(
        (environment): environment is DeploymentEnvironmentDto =>
          environment !== null,
      ),
      distinctUntilKeyChanged('environmentId'),
      shareReplay(),
    );

  @Select(GlobalStateImpl.roles)
  userRoles$!: Observable<Set<UserRoleDto>>;

  constructor(private store: Store) {}

  getDeploymentEnvironmentId(): Observable<string> {
    return this.deploymentEnvironment$.pipe(
      take(1),
      map((environment) => environment.environmentId),
    );
  }

  getDeploymentEnvironment(): Observable<DeploymentEnvironmentDto> {
    return this.deploymentEnvironment$.pipe(map((environment) => environment));
  }

  onDeploymentEnvironmentChangeSkipFirst(opt: {
    onChanged: (environment: DeploymentEnvironmentDto) => void;
    ngOnDestroy$: Observable<any>;
  }) {
    this.deploymentEnvironment$
      .pipe(takeUntil(opt.ngOnDestroy$), skip(1))
      .subscribe((selectedEnvironment) => {
        opt.onChanged(selectedEnvironment);
      });
  }

  public awaitLoadedUserRoles(): Observable<Set<UserRoleDto>> {
    return this.userInfo$.pipe(first()).pipe(
      switchMap(() => this.userRoles$),
      first(),
    );
  }
}
