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
import {
  PolicyFormAdapter,
  jsonAdapter,
  localDateAdapter,
  stringArrayOrCommaJoinedAdapter,
} from './policy-form-adapter';
import {policyLeftExpressions} from './policy-left-expressions';
import {SUPPORTED_POLICY_OPERATORS} from './policy-operators';

export interface PolicyVerbConfig {
  operandLeftId: string;
  operandLeftTitle: string;
  operandLeftDescription: string;
  operandRightType: 'DATE' | 'TEXT' | 'PARTICIPANT_ID';
  operandRightHint?: string;
  operandRightPlaceholder?: string;
  supportedOperators: OperatorDto[];
  adapter: PolicyFormAdapter<any>;
}

export const SUPPORTED_POLICY_VERBS: PolicyVerbConfig[] = [
  {
    operandLeftId: policyLeftExpressions.policyEvaluationTime,
    operandLeftTitle: 'Evaluation Time',
    operandLeftDescription:
      'Time at which the policy is evaluated. This can be used to restrict the data offer to certain time periods',
    supportedOperators: ['GEQ', 'LEQ', 'GT', 'LT'],
    operandRightType: 'DATE',
    operandRightPlaceholder: 'MM/DD/YYYY',
    operandRightHint: 'MM/DD/YYYY',
    adapter: localDateAdapter,
  },
  {
    operandLeftId: policyLeftExpressions.referringConnector,
    operandLeftTitle: 'Participant ID',
    operandLeftDescription:
      'Participant ID, also called Connector ID, of the counter-party connector.',
    operandRightType: 'PARTICIPANT_ID',
    supportedOperators: ['EQ', 'IN'],
    operandRightPlaceholder: 'MDSL1234XX.C1234YY',
    operandRightHint: 'Multiple values can be joined by comma',
    adapter: stringArrayOrCommaJoinedAdapter,
  },
];

export const defaultPolicyVerbConfig = (verb: string): PolicyVerbConfig => ({
  operandLeftId: verb,
  operandLeftTitle: verb,
  operandLeftDescription: '',
  supportedOperators: SUPPORTED_POLICY_OPERATORS.map((it) => it.id),
  operandRightType: 'TEXT',
  adapter: jsonAdapter,
});
