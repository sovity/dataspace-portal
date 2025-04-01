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

const tag = 'AuthorityOrganizationDetailPage';

export class SetOrganizationId {
  static readonly type = `[${tag}] Set Organization Id`;
  constructor(public organizationId: string) {}
}

export class RefreshOrganization {
  static readonly type = `[${tag}] Refresh Organization`;
  constructor(public cb?: () => void) {}
}

export class ApproveOrganization {
  static readonly type = `[${tag}] Approve Organization`;
}

export class RejectOrganization {
  static readonly type = `[${tag}] Reject Organization`;
}

// Opened User detail Actions

export class SetOrganizationUserId {
  static readonly type = `[${tag}] Set Currently Opened Organization User Id`;
  constructor(public organizationId: string, public userId: string) {}
}

export class RefreshOrganizationUser {
  static readonly type = `[${tag}] Refresh Currently Opened Organization User`;
}

export class DeactivateUser {
  static readonly type = `[${tag}] Deactivate Currently Opened Organization User`;
  constructor(public userId: string) {}
}

export class ReactivateUser {
  static readonly type = `[${tag}] Reactivate Currently Opened Organization User`;
  constructor(public userId: string) {}
}
