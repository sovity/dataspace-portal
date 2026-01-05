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
import {UiPolicy, UiPolicyExpression} from '@sovity.de/authority-portal-client';
import {policyLeftExpressions} from '../../../../../pages/catalog-page/policy-editor/model/policy-left-expressions';
import {
  constraint,
  constraintList,
  multi,
} from '../../../../../pages/catalog-page/policy-editor/model/ui-policy-expression-utils';

export namespace TestPolicies {
  const policy = (
    expression: UiPolicyExpression,
    errors: string[] = [],
  ): UiPolicy => ({
    policyJsonLd: JSON.stringify({
      _description:
        'The actual JSON-LD will look different. This is just data from the fake backend.',
      expression,
    }),
    expression,
    errors,
  });

  export const connectorRestricted: UiPolicy = policy(
    multi(
      'AND',
      constraint(
        policyLeftExpressions.policyEvaluationTime,
        'GEQ',
        '2021-01-01',
      ),
      constraint(
        policyLeftExpressions.policyEvaluationTime,
        'LT',
        '2025-01-01',
      ),
      multi(
        'OR',
        constraint('REFERRING_CONNECTOR', 'EQ', 'BPNL1234XX.C1234XX'),
        constraint('REFERRING_CONNECTOR', 'EQ', 'BPNL1234XX.C1235YY'),
      ),
      constraint('ALWAYS_TRUE', 'EQ', 'true'),
    ),
  );

  export const warnings: UiPolicy = policy(
    constraintList('SOME_UNKNOWN_PROP', 'HAS_PART', ['A', 'B', 'C']),
    ['$.duties: Duties are currently unsupported.'],
  );

  export const unrestricted: UiPolicy = policy({
    type: 'EMPTY',
  });
}
