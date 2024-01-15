import {UserRoleDto} from '@sovity.de/authority-portal-client';

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

export function getHighestApplicationRole(currentUserRoles: UserRoleDto[]) {
  return getHighestRole(
    currentUserRoles.filter((it) => !it.startsWith('PARTICIPANT')),
  );
}

export function getHighestParticipantRole(currentUserRoles: UserRoleDto[]) {
  return getHighestRole(
    currentUserRoles.filter((it) => it.startsWith('PARTICIPANT')),
  );
}

/**
 * returns the highest role from the given roles
 * the roles are ordered from highest to lowest
 * @param roles
 * @returns
 */
export function getHighestRole(userRoles: UserRoleDto[]): UserRoleDto {
  const allRoles = Object.values(UserRoleDto);

  return allRoles[
    Math.min(
      ...userRoles.map((uRr) => allRoles.findIndex((aRr) => uRr === aRr)),
    )
  ];
}
