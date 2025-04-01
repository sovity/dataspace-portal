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
import {E2eDevUser} from './e2e-dev-user';

export const E2E_DEV_USERS: E2eDevUser[] = [
  {
    user: '00000000-0000-0000-0000-000000000001',
    password: '111',
    role: 'Authority Admin',
    organization: 'Company 1',
  },
  {
    user: '00000000-0000-0000-0000-000000000002',
    password: '222',
    role: 'Authority User',
    organization: 'Company 1',
  },
  {
    user: '00000000-0000-0000-0000-000000000003',
    password: '333',
    role: 'Participant Admin',
    organization: 'Company 2',
  },

  {
    user: '00000000-0000-0000-0000-000000000004',
    password: '444',
    role: 'Participant User',
    organization: 'Company 2',
  },
  {
    user: '00000000-0000-0000-0000-000000000005',
    password: '555',
    role: 'Pending User',
    organization: 'Company 3',
  },
  {
    user: '00000000-0000-0000-0000-000000000006',
    password: '666',
    role: 'New User',
    organization: null,
  },
  {
    user: '00000000-0000-0000-0000-000000000007',
    password: '777',
    role: 'Service Partner Admin',
    organization: 'Company 5',
  },
  {
    user: '00000000-0000-0000-0000-000000000008',
    password: '888',
    role: 'Operator Admin',
    organization: 'Company 6',
  },
  {
    user: '00000000-0000-0000-0000-000000000010',
    password: '888',
    role: 'Rejected User',
    organization: 'Company 7',
  },
  {
    user: '00000000-0000-0000-0000-00000013',
    password: '888',
    role: 'Onboarding Organization',
    organization: 'Company 9',
  },
];
