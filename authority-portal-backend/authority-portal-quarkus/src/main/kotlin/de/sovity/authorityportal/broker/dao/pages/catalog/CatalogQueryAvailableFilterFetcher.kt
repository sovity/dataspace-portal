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
package de.sovity.authorityportal.broker.dao.pages.catalog

import de.sovity.authorityportal.broker.services.api.filtering.model.FilterAttributeApplied
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.Field
import org.jooq.JSON
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType

@ApplicationScoped
class CatalogQueryAvailableFilterFetcher(
    val catalogQueryFilterService: CatalogQueryFilterService
) {
    /**
     * Query available filter values.
     *
     * @param fields      query fields
     * @param searchQuery search query
     * @param filters     filters (values + filter clauses)
     * @return [Field] with field[iFilter][iValue]
     */
    fun queryAvailableFilterValues(
        environment: String,
        fields: CatalogQueryFields,
        searchQuery: String?,
        filters: List<FilterAttributeApplied>
    ): Field<JSON> {
        val resultFields = filters.mapIndexed { i, currentFilter ->
            // When querying a filter's values we apply all filters except for the current filter
            val otherFilters = filters.filterIndexed { j, _ -> i != j }
            queryFilterValues(environment, fields, currentFilter, searchQuery, otherFilters)
        }
        return DSL.select(DSL.jsonArray(resultFields)).asField()
    }

    private fun queryFilterValues(
        environment: String,
        parentQueryFields: CatalogQueryFields,
        currentFilter: FilterAttributeApplied,
        searchQuery: String?,
        otherFilters: List<FilterAttributeApplied>
    ): Field<JSON> {
        val fields = parentQueryFields.withSuffix("filter_" + currentFilter.name)

        val idField: Field<String> = currentFilter.idField(fields)
        val nameField: Field<String>? = currentFilter.nameField?.invoke(fields)

        val idNameArray = if (nameField == null) {
            DSL.array(idField)
        } else {
            DSL.array(idField, nameField)
        }

        return DSL.select(
            DSL.coalesce(
                DSL.arrayAggDistinct(idNameArray),
                emptyStringArray()
            )
        )
            .fromCatalogQueryTables(fields)
            .where(catalogQueryFilterService.filterDbQuery(environment, fields, searchQuery, otherFilters))
            .asField()
    }

    private fun emptyStringArray() = DSL.value(arrayOf<String>()).cast<Array<String>>(SQLDataType.VARCHAR.array())
}
