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
import {UserRoleDto} from '@sovity.de/authority-portal-client';
import {nonNull} from './type-utils';

export function mapRolesToReadableFormat(
  role: string | UserRoleDto | null,
): string {
  if (!role) {
    return 'None';
  }

  const words = role
    .split('_')
    .map(
      (word: string) =>
        word.charAt(0).toUpperCase() + word.slice(1).toLowerCase(),
    );
  return words.join(' ');
}

export function getApplicationRoles(
  currentUserRoles: UserRoleDto[],
): UserRoleDto[] {
  return currentUserRoles.filter(isApplicationRole);
}

export function getHighestApplicationRole(
  currentUserRoles: UserRoleDto[],
): UserRoleDto | null {
  return getHighestRole(currentUserRoles.filter(isApplicationRole));
}

export function getHighestParticipantRole(
  currentUserRoles: UserRoleDto[],
): UserRoleDto {
  return getHighestRole(currentUserRoles.filter(isParticipantRole)) ?? 'USER';
}

/**
 * returns the highest role from the given roles
 * the roles are ordered from highest to lowest
 */
export function getHighestRole(userRoles: UserRoleDto[]): UserRoleDto | null {
  return Object.values(UserRoleDto).find((it) =>
    userRoles.includes(it),
  ) as UserRoleDto | null;
}

export function getAvailableRoles(
  ownRoles: UserRoleDto[],
  isSameOrg: boolean,
): UserRoleDto[] {
  let roles: UserRoleDto[] = [];
  if (ownRoles.includes('AUTHORITY_ADMIN')) {
    roles.push('AUTHORITY_ADMIN', 'AUTHORITY_USER');
  }
  if (
    ownRoles.includes('AUTHORITY_ADMIN') ||
    ownRoles.includes('SERVICE_PARTNER_ADMIN')
  ) {
    roles.push('SERVICE_PARTNER_ADMIN');
  }
  if (
    ownRoles.includes('AUTHORITY_ADMIN') ||
    ownRoles.includes('OPERATOR_ADMIN')
  ) {
    roles.push('OPERATOR_ADMIN');
  }
  if (isSameOrg && ownRoles.includes('ADMIN')) {
    roles.push('ADMIN', 'KEY_USER', 'USER');
  }
  return roles;
}

export function isParticipantRole(role: UserRoleDto): boolean {
  return !isApplicationRole(role) && role !== UserRoleDto.Unauthenticated;
}

export function getParticipantRoles(): UserRoleDto[] {
  return Object.values(UserRoleDto).filter(isParticipantRole);
}

export function isApplicationRole(role: UserRoleDto): boolean {
  return (
    role !== UserRoleDto.Admin &&
    role !== UserRoleDto.KeyUser &&
    role !== UserRoleDto.User &&
    role !== UserRoleDto.Unauthenticated
  );
}

export function getHighestRolesString(userRoles: UserRoleDto[]): string {
  return nonNull([
    ...getApplicationRoles(userRoles),
    getHighestParticipantRole(userRoles),
  ])
    .map(mapRolesToReadableFormat)
    .join(', ');
}

export function getHighestRoleString(userRoles: UserRoleDto[]): string {
  return mapRolesToReadableFormat(
    nonNull([
      getHighestApplicationRole(userRoles),
      getHighestParticipantRole(userRoles),
    ])[0],
  );
}
