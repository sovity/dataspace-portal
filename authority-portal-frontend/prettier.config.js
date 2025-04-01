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

module.exports = {
  tabWidth: 2,
  useTabs: false,
  singleQuote: true,
  semi: true,
  arrowParens: 'always',
  trailingComma: 'all',
  bracketSameLine: true,
  printWidth: 80,
  bracketSpacing: false,
  proseWrap: 'always',

  attributeGroups: [
    '$ANGULAR_STRUCTURAL_DIRECTIVE',
    '^(id|name)$',
    '^class$',
    '$DEFAULT',
    '^aria-',
    '$ANGULAR_INPUT',
    '$ANGULAR_TWO_WAY_BINDING',
    '$ANGULAR_OUTPUT',
  ],

  // @trivago/prettier-plugin-sort-imports
  importOrder: [
    // this import needs to be on top or tests fail
    '^zone.js/testing$',
    // third parties first
    '^@angular/(.*)$',
    '^rxjs(/(.*))?$',
    '<THIRD_PARTY_MODULES>',
    // rest after
    '^[./]',
  ],
  importOrderParserPlugins: [
    'typescript',
    'classProperties',
    'decorators-legacy',
  ],
  importOrderSeparation: false,
  importOrderSortSpecifiers: true,
};
