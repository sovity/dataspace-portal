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
import {Injectable} from '@angular/core';
import {
  OperatorDto,
  UiPolicyExpression,
  UiPolicyExpressionType,
  UiPolicyLiteral,
} from '@sovity.de/authority-portal-client';
import {associateBy} from '../../../../core/utils/map-utils';
import {PolicyExpressionMapped} from './policy-expression-mapped';
import {
  PolicyMultiExpressionConfig,
  SUPPORTED_MULTI_EXPRESSIONS,
} from './policy-multi-expressions';
import {
  PolicyOperatorConfig,
  SUPPORTED_POLICY_OPERATORS,
  defaultPolicyOperatorConfig,
} from './policy-operators';
import {
  PolicyVerbConfig,
  SUPPORTED_POLICY_VERBS,
  defaultPolicyVerbConfig,
} from './policy-verbs';

@Injectable({providedIn: 'root'})
export class PolicyMapper {
  verbs = associateBy(SUPPORTED_POLICY_VERBS, (it) => it.operandLeftId);
  operators = associateBy(SUPPORTED_POLICY_OPERATORS, (it) => it.id);
  multiExpressionTypes = associateBy(
    SUPPORTED_MULTI_EXPRESSIONS,
    (it) => it.expressionType,
  );

  buildPolicy(expression: UiPolicyExpression): PolicyExpressionMapped {
    if (expression.type === 'EMPTY') {
      return {type: 'EMPTY'};
    }

    if (expression.type === 'CONSTRAINT') {
      return this.mapConstraint(expression);
    }

    return this.mapMultiExpression(expression);
  }

  private mapConstraint(
    expression: UiPolicyExpression,
  ): PolicyExpressionMapped {
    const verb = this.getVerbConfig(expression.constraint?.left!);
    const operator = this.getOperatorConfig(expression.constraint?.operator!);
    const value = expression.constraint?.right;

    return {
      type: 'CONSTRAINT',
      verb,
      operator,
      valueRaw: value,
      valueJson: this.formatJson(value!),
      displayValue: this.formatValue(value, verb) ?? 'null',
    };
  }

  private mapMultiExpression(
    expression: UiPolicyExpression,
  ): PolicyExpressionMapped {
    const multiExpression = this.getMultiExpressionConfig(expression.type);
    const expressions = (expression.expressions ?? []).map((it) =>
      this.buildPolicy(it),
    );
    return {
      type: 'MULTI',
      multiExpression,
      expressions,
    };
  }

  private getVerbConfig(verb: string): PolicyVerbConfig {
    const verbConfig = this.verbs.get(verb);
    if (verbConfig) {
      return verbConfig;
    }

    return defaultPolicyVerbConfig(verb);
  }

  private getOperatorConfig(operator: OperatorDto): PolicyOperatorConfig {
    const operatorConfig = this.operators.get(operator);
    if (operatorConfig) {
      return operatorConfig;
    }

    return defaultPolicyOperatorConfig(operator);
  }

  private getMultiExpressionConfig(
    expressionType: UiPolicyExpressionType,
  ): PolicyMultiExpressionConfig {
    const multiExpressionType = this.multiExpressionTypes.get(expressionType);
    if (multiExpressionType) {
      return multiExpressionType;
    }

    return {
      expressionType,
      title: expressionType,
      description: '',
    };
  }

  private formatValue(
    value: UiPolicyLiteral | undefined,
    verbConfig: PolicyVerbConfig,
  ) {
    if (value == null) {
      return '';
    }

    return verbConfig.adapter.displayText(value);
  }

  private formatJson(value: UiPolicyLiteral): string {
    if (value.type === 'STRING_LIST') {
      return JSON.stringify(value.valueList);
    }

    if (value.type === 'JSON') {
      return value.value ?? '';
    }

    return JSON.stringify(value.value);
  }
}
