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
import {
  UserDetailDto,
  UserInfo,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {
  getApplicationRoles,
  getAvailableRoles,
  getHighestParticipantRole,
  getHighestRolesString,
  isApplicationRole,
  isParticipantRole,
} from 'src/app/core/utils/user-role-utils';

export interface UserRoleUpdateConfig {
  canEdit: boolean;

  currentParticipantRole: UserRoleDto;
  currentApplicationRoles: UserRoleDto[];

  canChangeApplicationRole: boolean;
  canChangeParticipantRole: boolean;

  availableParticipantRoles: UserRoleDto[];
  availableApplicationRoles: UserRoleDto[];

  readableRolesAsString: string;
  onRoleUpdateSuccessful: () => void;
}

export function buildUserRoleUpdateConfig(options: {
  ownUserId: string;
  ownUserOrganizationId: string;
  ownRoles: UserRoleDto[];
  targetUserId: string;
  targetUserOrganizationId: string;
  targetRoles: UserRoleDto[];
  onRoleUpdateSuccessful: () => void;
}): UserRoleUpdateConfig {
  const {
    ownUserId,
    ownUserOrganizationId,
    ownRoles,
    targetUserId,
    targetUserOrganizationId,
    targetRoles,
    onRoleUpdateSuccessful,
  } = options;

  const currentApplicationRoles = getApplicationRoles(targetRoles);
  const currentParticipantRole = getHighestParticipantRole(targetRoles);

  const availableRoles = getAvailableRoles(
    ownRoles,
    ownUserOrganizationId === targetUserOrganizationId,
  );
  const canChangeApplicationRole = availableRoles.some(isApplicationRole);
  const canChangeParticipantRole = availableRoles.some(isParticipantRole);

  const filterAvailableRoles = (
    filter: (role: UserRoleDto) => boolean,
    mustInclude: UserRoleDto[],
  ) => {
    const available = availableRoles.filter(filter);

    for (const role of mustInclude) {
      if (!available.includes(role)) {
        available.push(role);
      }
    }

    return available;
  };

  const availableApplicationRoles = filterAvailableRoles(
    isApplicationRole,
    currentApplicationRoles,
  );

  const availableParticipantRoles = filterAvailableRoles(isParticipantRole, [
    currentParticipantRole,
  ]);

  const readableRoleList = getHighestRolesString(targetRoles);

  return {
    canEdit:
      ownUserId !== targetUserId &&
      (canChangeApplicationRole || canChangeParticipantRole),
    currentApplicationRoles,
    currentParticipantRole,
    canChangeApplicationRole,
    canChangeParticipantRole,
    availableApplicationRoles,
    availableParticipantRoles,
    readableRolesAsString: readableRoleList,
    onRoleUpdateSuccessful,
  };
}

export function buildUserRoleUpdateConfigFromUserInfo(options: {
  currentUser: UserInfo;
  target: UserInfo | UserDetailDto;
  onRoleUpdateSuccessful: () => void;
}): UserRoleUpdateConfig {
  const {currentUser, target, onRoleUpdateSuccessful} = options;

  return buildUserRoleUpdateConfig({
    ownRoles: currentUser.roles,
    ownUserId: currentUser.userId,
    ownUserOrganizationId: currentUser.organizationId,
    targetRoles: target.roles,
    targetUserId: target.userId,
    targetUserOrganizationId: target.organizationId,
    onRoleUpdateSuccessful,
  });
}

export function buildUserRoleUpdateConfigUneditable(
  roles: UserRoleDto[],
): UserRoleUpdateConfig {
  return buildUserRoleUpdateConfig({
    ownRoles: [],
    ownUserId: '',
    ownUserOrganizationId: '',
    targetRoles: roles,
    targetUserId: '',
    targetUserOrganizationId: '',
    onRoleUpdateSuccessful: () => {},
  });
}
