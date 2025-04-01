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

import {UiPolicyExpressionType} from '@sovity.de/authority-portal-client';

export interface PolicyMultiExpressionConfig {
  expressionType: UiPolicyExpressionType;
  title: string;
  description: string;
}

export const SUPPORTED_MULTI_EXPRESSIONS: PolicyMultiExpressionConfig[] = [
  {
    expressionType: 'AND',
    title: 'AND',
    description:
      'Conjunction of several expressions. Evaluates to true if and only if all child expressions are true',
  },
  {
    expressionType: 'OR',
    title: 'OR',
    description:
      'Disjunction of several expressions. Evaluates to true if and only if at least one child expression is true',
  },
  {
    expressionType: 'XONE',
    title: 'XONE',
    description:
      'XONE operation. Evaluates to true if and only if exactly one child expression is true',
  },
];
