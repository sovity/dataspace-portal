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
  ChangeApplicationRolesRequest,
  ChangeParticipantRoleRequest,
  ClearApplicationRolesRequest,
  IdResponse,
  InviteParticipantUserRequest,
  MemberInfo,
  OnboardingUserUpdateDto,
  PossibleCreatorSuccessor,
  UpdateUserDto,
  UserDeletionCheck,
  UserDetailDto,
  UserInfo,
  UserRegistrationStatusDto,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {Patcher, patchObj} from 'src/app/core/utils/object-utils';
import {
  isApplicationRole,
  isParticipantRole,
} from '../../../utils/user-role-utils';
import {
  deleteOrganization,
  findOrganizationByUserId,
  getOrganizationDetails,
  getParticipantAdmins,
  updateOrganization,
} from './fake-organizations';

export const ALL_USERS: Record<string, UserDetailDto> = {
  '00000000-0000-0000-0000-000000000001': {
    userId: '00000000-0000-0000-0000-000000000001',
    firstName: 'Authority',
    lastName: 'Admin',
    roles: ['AUTHORITY_ADMIN', 'AUTHORITY_USER', 'ADMIN', 'KEY_USER', 'USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Authority Organization',
    organizationId: 'BPNL1111AA',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '',
    invitingUserFirstName: '',
    invitingUserLastName: '',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-000000000002': {
    userId: '00000000-0000-0000-0000-000000000002',
    firstName: 'Authority',
    lastName: 'User',
    roles: ['AUTHORITY_USER', 'USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Authority Organization',
    organizationId: 'BPNL1111AA',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-000000000003': {
    userId: '00000000-0000-0000-0000-000000000003',
    firstName: 'Participant',
    lastName: 'Admin',
    roles: ['ADMIN', 'KEY_USER', 'USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Participant Organization',
    organizationId: 'BPNL2222BB',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },

  '00000000-0000-0000-0000-000000000004': {
    userId: '00000000-0000-0000-0000-000000000004',
    firstName: 'Participant',
    lastName: 'Key User',
    roles: ['KEY_USER', 'USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Participant Organization',
    organizationId: 'BPNL2222BB',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-000000000005': {
    userId: '00000000-0000-0000-0000-000000000005',
    firstName: 'Participant',
    lastName: 'User',
    roles: ['USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Participant Organization',
    organizationId: 'BPNL2222BB',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-000000000009': {
    userId: '00000000-0000-0000-0000-000000000009',
    firstName: 'Operator',
    lastName: 'Admin',
    roles: ['OPERATOR_ADMIN', 'ADMIN', 'KEY_USER', 'USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Operator Organization',
    organizationId: 'BPNL8888EE',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-000000000007': {
    userId: '00000000-0000-0000-0000-000000000007',
    firstName: 'Service Partner',
    lastName: 'Admin',
    roles: ['SERVICE_PARTNER_ADMIN', 'ADMIN', 'KEY_USER', 'USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Operator Organization',
    organizationId: 'BPNL7777AA',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-00000013': {
    userId: '00000000-0000-0000-0000-00000013',
    firstName: 'Onboarding',
    lastName: 'Organization',
    roles: ['ADMIN'],
    registrationStatus: 'ONBOARDING',
    organizationName: 'Dev Organization 1',
    organizationId: 'BPNL1111AA',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-00000014': {
    userId: '00000000-0000-0000-0000-00000014',
    firstName: 'Onboarding',
    lastName: 'User',
    roles: ['USER'],
    registrationStatus: 'ONBOARDING',
    organizationName: 'Dev Organization 2',
    organizationId: 'BPNL2222BB',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-000000000006': {
    userId: '00000000-0000-0000-0000-000000000006',
    firstName: 'Pending',
    lastName: 'User',
    roles: [],
    registrationStatus: 'PENDING',
    organizationName: '',
    organizationId: 'BPNL5555EE',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-00000011': {
    userId: '00000000-0000-0000-0000-00000011',
    firstName: 'Rejected',
    lastName: 'User',
    roles: ['ADMIN', 'KEY_USER', 'USER'],
    registrationStatus: 'REJECTED',
    organizationName: 'Rejected Organization',
    organizationId: 'BPNL6666EE',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-100000000001': {
    userId: '00000000-0000-0000-0000-100000000001',
    firstName: 'New Participant',
    lastName: 'Admin',
    roles: ['ADMIN', 'KEY_USER', 'USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Three Users',
    organizationId: 'BPNL9111ZZ',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-100000000002': {
    userId: '00000000-0000-0000-0000-100000000002',
    firstName: 'Organization',
    lastName: 'Creator',
    roles: ['ADMIN', 'KEY_USER', 'USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Three Users',
    organizationId: 'BPNL9111ZZ',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
  '00000000-0000-0000-0000-100000000003': {
    userId: '00000000-0000-0000-0000-100000000003',
    firstName: 'Normal',
    lastName: 'User',
    roles: ['AUTHORITY_USER', 'USER'],
    registrationStatus: 'ACTIVE',
    organizationName: 'Three Users',
    organizationId: 'BPNL9111ZZ',
    email: 'email@example.com',
    phone: '+49 231 1234567',
    position: 'Employee',
    onboardingType: 'SELF_REGISTRATION',
    invitingUserId: '00000000-0000-0000-0000-000000000001',
    invitingUserFirstName: 'Authority',
    invitingUserLastName: 'Admin',
    creationDate: new Date('2024-02-01'),
  },
};

export const TEST_USERS: UserInfo[] = [
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-000000000001']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-000000000002']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-000000000003']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-000000000004']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-000000000005']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-000000000009']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-000000000007']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-00000013']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-00000014']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-000000000006']),
  buildUserInfo(ALL_USERS['00000000-0000-0000-0000-00000011']),
  {
    authenticationStatus: 'UNAUTHENTICATED',
    userId: 'unauthenticated',
    roles: ['UNAUTHENTICATED'],
    registrationStatus: undefined,
    firstName: 'Unauthenticated',
    lastName: 'User',
    organizationName: 'No Organization',
    organizationId: 'unauthenticated',
  },
];

/**
 * Currently "logged-in user" for local dev UI
 */
let currentlyLoggedInUser: UserInfo = buildUserInfo(
  ALL_USERS['00000000-0000-0000-0000-000000000001'],
);

/**
 * Update currently logged-in User for local dev UI
 */
export const updateLoggedInUser = (patcher: Patcher<UserInfo>) => {
  currentlyLoggedInUser = patchObj<UserInfo>(currentlyLoggedInUser, patcher);
};

/**
 * Fake implementation for "userInfo" endpoint
 */
export const getUserInfo = (): UserInfo => currentlyLoggedInUser;

export const getUserOrThrow = (userId: string): UserDetailDto => {
  const id = Object.keys(ALL_USERS).find((id) => id === userId);
  if (!id) throw new Error(`User not found ${userId}`);

  return ALL_USERS[id];
};

export const addUser = (user: UserDetailDto) => {
  ALL_USERS[user.userId] = user;
};
export const updateUser = (userId: string, dto: UpdateUserDto): IdResponse => {
  patchUser(userId, () => ({
    firstName: dto.firstName,
    lastName: dto.lastName,
    email: dto.email,
    phone: dto.phone,
    position: dto.jobTitle,
  }));
  return {id: userId, changedDate: new Date()};
};

export const inviteUser = (
  request: InviteParticipantUserRequest,
): IdResponse => {
  const newUserId = generateNewId();

  const newUser: UserDetailDto = {
    userId: newUserId,
    firstName: request.firstName,
    lastName: request.lastName,
    roles: generateRoles(request.role),
    registrationStatus: 'INVITED',
    organizationId: getUserInfo().organizationId,
    organizationName: getUserInfo().organizationName,
    invitingUserId: currentlyLoggedInUser.userId,
    invitingUserFirstName: currentlyLoggedInUser.firstName,
    invitingUserLastName: currentlyLoggedInUser.lastName,
    email: request.email,
    phone: '',
    position: '',
    creationDate: new Date(),
    onboardingType: 'INVITATION',
  };

  updateOrganization(getUserInfo().organizationId, (organization) => {
    return {
      ...organization,
      memberList: [...organization.memberList, newUser as MemberInfo],
      memberCount: organization.memberCount + 1,
    };
  });

  addUser(newUser);

  return {id: newUserId, changedDate: new Date()};
};

const generateNewId = (): string => {
  const usersCounter = Object.keys(ALL_USERS).length;
  const counterStr = usersCounter.toString().padStart(8, '0');
  return `00000000-0000-0000-0000-${counterStr + 1}`;
};

const generateRoles = (userRole: UserRoleDto): UserRoleDto[] => {
  if (userRole === 'ADMIN') {
    return ['ADMIN', 'KEY_USER', 'USER'];
  } else if (userRole === 'KEY_USER') {
    return ['KEY_USER', 'USER'];
  } else if (userRole === 'USER') {
    return ['USER'];
  } else {
    return [];
  }
};

export const changeParticipantRole = (
  request: ChangeParticipantRoleRequest,
): IdResponse => {
  patchUser(request.userId, (user) => {
    let participantRoles: UserRoleDto[] = generateRoles(
      request.body as UserRoleDto,
    );
    let applicationRoles: UserRoleDto[] = user.roles.filter(isApplicationRole);

    return {
      roles: [...applicationRoles, ...participantRoles],
    };
  });
  return {id: request.userId, changedDate: new Date()};
};

export const changeApplicationRoles = (
  request: ChangeApplicationRolesRequest,
): IdResponse => {
  patchUser(request.userId, (user) => {
    let participantRoles: UserRoleDto[] = user.roles.filter(isParticipantRole);
    let applicationRoles: UserRoleDto[] = request.userRoleDto;

    if (
      applicationRoles.includes('AUTHORITY_ADMIN') &&
      !applicationRoles.includes('AUTHORITY_USER')
    ) {
      applicationRoles.push('AUTHORITY_USER');
    }

    return {
      roles: [...applicationRoles, ...participantRoles],
    };
  });
  return {id: request.userId, changedDate: new Date()};
};

export const clearApplicationRoles = (
  request: ClearApplicationRolesRequest,
): IdResponse => {
  patchUser(request.userId, (user) => ({
    roles: user.roles.filter(isParticipantRole),
  }));
  return {id: request.userId, changedDate: new Date()};
};

const updateUserStatusOfOrganizationById = (
  userId: string,
  status: UserRegistrationStatusDto,
) => {
  const organizationId = findOrganizationByUserId(userId);
  if (organizationId) {
    updateOrganization(organizationId, (organization) => {
      return {
        ...organization,
        memberList: organization.memberList.map((member) => {
          if (member.userId === userId) {
            return {
              ...member,
              registrationStatus: status,
            };
          }
          return member;
        }),
      };
    });
  }
};

export const deactivateUser = (userId: string): IdResponse => {
  updateUserStatusOfOrganizationById(userId, 'DEACTIVATED');
  patchUser(userId, () => ({registrationStatus: 'DEACTIVATED'}));
  return {id: userId, changedDate: new Date()};
};

export const reactivateUser = (userId: string): IdResponse => {
  updateUserStatusOfOrganizationById(userId, 'ACTIVE');
  patchUser(userId, () => ({registrationStatus: 'ACTIVE'}));
  return {id: userId, changedDate: new Date()};
};

export const onboardUser = (request: OnboardingUserUpdateDto): IdResponse => {
  return {id: '000001', changedDate: new Date()};
};

export const cascadeDeleteUser = (
  userId: string,
  successorUserId: string | null,
): IdResponse => {
  const userDeletionCheck = checkUserDeletion(userId);
  const user = getUserOrThrow(userId);
  const organization = getOrganizationDetails(user.organizationId);

  if (userDeletionCheck.isLastParticipantAdmin) {
    deleteOrganization(organization.id);
  } else {
    if (userDeletionCheck.isOrganizationCreator) {
      if (!successorUserId) {
        throw new Error("Can't delete organization creator without successor");
      }
      const successorUser = getUserOrThrow(successorUserId);
      updateOrganization(organization.id, () => ({
        createdByUserId: successorUser.userId,
        createdByFirstName: successorUser.firstName,
        createdByLastName: successorUser.lastName,
      }));
    }
    updateOrganization(organization.id, (o) => ({
      memberList: o.memberList.filter((x) => x.userId !== userId),
      memberCount: o.memberCount - 1,
    }));
  }

  return deleteUser(userId);
};

const patchUser = (userId: string, patcher: Patcher<UserDetailDto>) => {
  const user = getUserOrThrow(userId);
  ALL_USERS[userId] = patchObj<UserDetailDto>(user, patcher);
};

function buildUserInfo(user: UserDetailDto): UserInfo {
  return {
    authenticationStatus: 'AUTHENTICATED',
    userId: user.userId,
    roles: user.roles,
    registrationStatus: user.registrationStatus,
    firstName: user.firstName,
    lastName: user.lastName,
    organizationName: user.organizationName,
    organizationId: user.organizationId,
  };
}

const deleteUser = (userId: string): IdResponse => {
  delete ALL_USERS[userId];
  return {id: userId, changedDate: new Date()};
};

export const checkUserDeletion = (userId: string): UserDeletionCheck => {
  const user = getUserOrThrow(userId);
  const organization = getOrganizationDetails(user.organizationId);
  const participantAdmins = getParticipantAdmins(organization);

  const isLastParticipantAdmin =
    participantAdmins.length === 1 && userId === participantAdmins[0].userId;
  const isOrganizationCreator = organization.createdByUserId === userId;
  let possibleCreatorSuccessors: PossibleCreatorSuccessor[] = [];

  if (!isLastParticipantAdmin && isOrganizationCreator) {
    possibleCreatorSuccessors = participantAdmins.filter(
      (x) => x.userId !== userId,
    );
  }

  const authorityAdmins = Object.values(ALL_USERS).filter((u) =>
    u.roles.includes('AUTHORITY_ADMIN'),
  );

  const canBeDeletedCheck = !(
    authorityAdmins.length === 1 && userId === authorityAdmins[0].userId
  );

  return {
    userId,
    isLastParticipantAdmin,
    isOrganizationCreator,
    possibleCreatorSuccessors,
    canBeDeleted: canBeDeletedCheck,
  };
};
