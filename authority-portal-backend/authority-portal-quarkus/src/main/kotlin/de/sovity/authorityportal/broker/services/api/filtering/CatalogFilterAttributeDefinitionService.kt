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
package de.sovity.authorityportal.broker.services.api.filtering

import de.sovity.authorityportal.api.model.catalog.CnfFilterAttributeDisplayType
import de.sovity.authorityportal.broker.dao.pages.catalog.CatalogQueryFields
import de.sovity.authorityportal.broker.dao.utils.eqAny
import de.sovity.authorityportal.broker.services.api.filtering.model.FilterAttributeDefinition
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.Field

@ApplicationScoped
class CatalogFilterAttributeDefinitionService {
    fun forIdOnlyField(
        fieldExtractor: (CatalogQueryFields) -> Field<String>,
        name: String,
        label: String
    ): FilterAttributeDefinition {
        return FilterAttributeDefinition(
            name = name,
            label = label,
            displayType = CnfFilterAttributeDisplayType.TITLE_ONLY,
            idField = fieldExtractor,
            nameField = null
        ) { fields, values -> fieldExtractor(fields).eqAny(values) }
    }

    fun forIdNameProperty(
        idFieldExtractor: (CatalogQueryFields) -> Field<String>,
        nameFieldExtractor: (CatalogQueryFields) -> Field<String>,
        name: String,
        label: String
    ): FilterAttributeDefinition {
        return FilterAttributeDefinition(
            name = name,
            label = label,
            displayType = CnfFilterAttributeDisplayType.ID_AND_TITLE,
            idField = idFieldExtractor,
            nameField = nameFieldExtractor
        ) { fields, values -> idFieldExtractor(fields).eqAny(values) }
    }

    fun buildDataSpaceFilter(): FilterAttributeDefinition {
        return FilterAttributeDefinition(
            name = "dataSpace",
            label = "Data Space",
            displayType = CnfFilterAttributeDisplayType.TITLE_ONLY,
            idField = CatalogQueryFields::dataSpace,
            nameField = null
        ) { fields, values -> fields.dataSpace.eqAny(values) }
    }
}
