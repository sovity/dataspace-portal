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
package de.sovity.authorityportal.broker.services.api.filtering.model

import de.sovity.authorityportal.api.model.catalog.CnfFilterAttributeDisplayType

/**
 * Implementation of a filter attribute for the catalog.
 *
 * @param name technical id of the attribute
 * @param label attribute label is shown as the title of the filter-box in the UI
 * @param displayType how to display the available values in the UI
 * @param idField get available value's id in the JooQ query
 * @param nameField get available value's name in the JooQ query (optional)
 * @param filterConditionFactory apply the filter
 */
data class FilterAttributeDefinition(
    val name: String,
    val label: String,
    val displayType: CnfFilterAttributeDisplayType,
    val idField: FilterValueFn,
    val nameField: FilterValueFn?,
    val filterConditionFactory: FilterConditionFactory
)
