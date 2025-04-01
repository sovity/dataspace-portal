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
import {Pipe, PipeTransform} from '@angular/core';
import {Observable, distinctUntilChanged} from 'rxjs';
import {map} from 'rxjs/operators';
import {UserRoleDto} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';

@Pipe({name: 'hasAnyRole'})
export class HasAnyRolePipe implements PipeTransform {
  constructor(private globalStateUtils: GlobalStateUtils) {}

  transform(value: UserRoleDto[]): Observable<boolean> {
    return this.globalStateUtils.userRoles$.pipe(
      map((rolesOfUser) =>
        Array.from(rolesOfUser)
          .filter((role) => value.includes(role))
          .some((x) => x),
      ),
      distinctUntilChanged(),
    );
  }
}
