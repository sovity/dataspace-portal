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
import {inject} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivateFn, Router} from '@angular/router';
import {map} from 'rxjs/operators';
import {UserRoleDto} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from '../../global-state/global-state-utils';

export const requiresRole: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const globalStateUtils = inject(GlobalStateUtils);

  const requiredRoles = getAllowedRoles(mergeData(route)['requiresRole']);

  if (!requiredRoles.length) {
    return true;
  }

  return globalStateUtils.awaitLoadedUserRoles().pipe(
    map((roles) => {
      let hasAnyRole = requiredRoles.some((role) => roles.has(role));

      if (hasAnyRole) {
        return true;
      } else {
        router.navigate(['/unauthorized']);
        return false;
      }
    }),
  );
};

const getAllowedRoles = (value: UserRoleDto | UserRoleDto[]): UserRoleDto[] => {
  if (typeof value === 'string') {
    return [value as UserRoleDto];
  }
  if (Array.isArray(value)) {
    return value as UserRoleDto[];
  }
  return [];
};

const mergeData = (route: ActivatedRouteSnapshot): any => {
  let data = {};
  let current: ActivatedRouteSnapshot | null = route;
  while (current) {
    data = {...data, ...current.data};
    current = current.parent;
  }
  return data;
};
