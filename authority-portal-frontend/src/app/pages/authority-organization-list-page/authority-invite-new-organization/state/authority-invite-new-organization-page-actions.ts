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
import {InviteOrganizationRequest} from '@sovity.de/authority-portal-client';

const tag = 'AuthorityInviteNewOrganizationPage';

export class Reset {
  static readonly type = `[${tag}] Reset`;
}

export class InviteNewOrganization {
  static readonly type = `[${tag}] Invite New Organization`;
  constructor(
    public request: InviteOrganizationRequest,
    public enableForm: () => void,
    public disableForm: () => void,
  ) {}
}
