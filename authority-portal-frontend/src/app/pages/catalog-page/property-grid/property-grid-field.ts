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
import {PolicyExpressionMapped} from '../policy-editor/model/policy-expression-mapped';

export interface PropertyGridField {
  icon: string;

  /**
   * Title of Property
   */
  label: string;

  /**
   * Adds "title"-Attribute to Label HTML Element
   */
  labelTitle?: string;

  /**
   * Property Value
   */
  text?: string;

  url?: string;
  onclick?: () => void;

  /**
   * Additional classes for the value text.
   */
  additionalClasses?: string;

  /**
   * Additional classes for the container
   */
  additionalContainerClasses?: string;

  /**
   * Additional classes for the icon.
   */
  additionalIconClasses?: string;

  copyButton?: boolean;
  tooltip?: string | null;
  textIconAfter?: string | null;

  policy?: PolicyExpressionMapped;
  policyErrors?: string[];

  /**
   * Hide text
   */
  hideFieldValue?: boolean;
}
