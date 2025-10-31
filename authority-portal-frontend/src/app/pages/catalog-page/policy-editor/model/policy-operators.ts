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
import {OperatorDto} from '@sovity.de/authority-portal-client';

export interface PolicyOperatorConfig {
  id: OperatorDto;
  title: string;
  description: string;
}

export const SUPPORTED_POLICY_OPERATORS: PolicyOperatorConfig[] = [
  {
    id: 'EQ',
    title: '=',
    description: 'Equal to',
  },
  {
    id: 'NEQ',
    title: '≠',
    description: 'Not equal to',
  },
  {
    id: 'GEQ',
    title: '≥',
    description: 'Greater than or equal to',
  },
  {
    id: 'GT',
    title: '>',
    description: 'Greater than',
  },
  {
    id: 'LEQ',
    title: '≤',
    description: 'Less than or equal to',
  },
  {
    id: 'LT',
    title: '<',
    description: 'Less than',
  },
  {
    id: 'IN',
    title: 'IN',
    description: 'In',
  },
  {
    id: 'HAS_PART',
    title: 'HAS PART',
    description: 'Has Part',
  },
  {
    id: 'IS_A',
    title: 'IS A',
    description: 'Is a',
  },
  {
    id: 'IS_NONE_OF',
    title: 'IS NONE OF',
    description: 'Is none of',
  },
  {
    id: 'IS_ANY_OF',
    title: 'IS ANY OF',
    description: 'Is any of',
  },
  {
    id: 'IS_ALL_OF',
    title: 'IS ALL OF',
    description: 'Is all of',
  },
];
export const defaultPolicyOperatorConfig = (
  operator: OperatorDto,
): PolicyOperatorConfig => ({
  id: operator,
  title: operator,
  description: '',
});
