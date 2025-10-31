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

import de.sovity.authorityportal.api.model.catalog.CnfFilter
import de.sovity.authorityportal.api.model.catalog.CnfFilterAttribute
import de.sovity.authorityportal.api.model.catalog.CnfFilterItem
import de.sovity.authorityportal.api.model.catalog.CnfFilterValue
import de.sovity.authorityportal.broker.dao.pages.catalog.CatalogQueryFields
import de.sovity.authorityportal.broker.dao.utils.JsonDeserializationUtils.read3dStringList
import de.sovity.authorityportal.broker.services.api.filtering.model.FilterAttributeApplied
import de.sovity.authorityportal.broker.services.api.filtering.model.FilterAttributeDefinition
import de.sovity.authorityportal.broker.services.api.filtering.model.FilterCondition
import de.sovity.authorityportal.web.environment.CatalogDataspaceConfigService
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.impl.DSL

@ApplicationScoped
class CatalogFilterService(
    val catalogFilterAttributeDefinitionService: CatalogFilterAttributeDefinitionService,
    val catalogDataspaceConfigService: CatalogDataspaceConfigService
) {

    private val caseInsensitiveEmptyStringLast = Comparator<String> { s1, s2 ->
        when {
            s1.isEmpty() && s2.isNotEmpty() -> 1 // Empty string comes after non-empty
            s1.isNotEmpty() && s2.isEmpty() -> -1 // Non-empty string comes before empty
            else -> String.CASE_INSENSITIVE_ORDER.compare(s1, s2) // Case-insensitive comparison
        }
    }

    private val availableFilters: List<FilterAttributeDefinition>
        /**
         * Currently supported filters for the catalog page.
         *
         * @return attribute definitions
         */
        get() = listOfNotNull(
            catalogFilterAttributeDefinitionService.forIdOnlyField(
                { fields: CatalogQueryFields -> fields.dataSourceAvailabilityLabel },
                "dataSourceAvailability",
                "Data Offer Type"
            ),
            catalogFilterAttributeDefinitionService.buildDataSpaceFilter()
                .takeIf { catalogDataspaceConfigService.hasMultipleDataspaces },
            catalogFilterAttributeDefinitionService.forIdOnlyField(
                { fields: CatalogQueryFields -> fields.dataOfferTable.DATA_MODEL },
                "dataModel",
                "Data Model"
            ),
            catalogFilterAttributeDefinitionService.forIdNameProperty(
                { fields: CatalogQueryFields -> fields.organizationTable.ID },
                { fields: CatalogQueryFields -> fields.organizationTable.NAME },
                "organization",
                "Organization"
            ),
            catalogFilterAttributeDefinitionService.forIdNameProperty(
                { fields: CatalogQueryFields -> fields.connectorTable.CONNECTOR_ID },
                { fields: CatalogQueryFields ->
                    DSL.concat(
                        fields.connectorTable.NAME,
                        DSL.`val`(" - "),
                        fields.organizationTable.NAME
                    )
                },
                "connectorId",
                "Connector"
            ),
        )

    fun getCatalogQueryFilters(cnfFilterValue: CnfFilterValue?): List<FilterAttributeApplied> {
        val values = getCnfFilterValuesMap(cnfFilterValue)
        return availableFilters
            .map { filter: FilterAttributeDefinition ->
                val queryFilter = getQueryFilter(filter, values[filter.name])
                FilterAttributeApplied(
                    name = filter.name,
                    idField = filter.idField,
                    nameField = filter.nameField,
                    filterConditionOrNull = queryFilter
                )
            }
            .toList()
    }

    private fun getQueryFilter(
        filter: FilterAttributeDefinition,
        values: List<String>?
    ): FilterCondition? {
        if (values.isNullOrEmpty()) {
            return null
        }
        return { fields: CatalogQueryFields -> filter.filterConditionFactory(fields, values) }
    }

    fun buildAvailableFilters(filterValuesJson: String): CnfFilter {
        val filterValues = read3dStringList(filterValuesJson)
        val filterAttributes = zipAvailableFilters(availableFilters, filterValues)
            .map { availableFilter: AvailableFilter ->
                CnfFilterAttribute(
                    id = availableFilter.definition.name,
                    title = availableFilter.definition.label,
                    values = buildAvailableFilterValues(availableFilter),
                    displayType = availableFilter.definition.displayType
                )
            }
        return CnfFilter(filterAttributes)
    }

    private fun buildAvailableFilterValues(availableFilter: AvailableFilter): List<CnfFilterItem> {
        return availableFilter.availableValues
            .map {
                CnfFilterItem(
                    id = it.first(),
                    title = it.last(),
                )
            }
            .sortedWith(java.util.Comparator.comparing({ it.title }, caseInsensitiveEmptyStringLast))
    }

    private fun zipAvailableFilters(
        availableFilters: List<FilterAttributeDefinition>,
        filterValues: List<List<List<String>>>
    ): List<AvailableFilter> {
        require(availableFilters.size == filterValues.size) {
            "Number of available filters and filter values must match: ${availableFilters.size} != ${filterValues.size}"
        }
        return availableFilters.mapIndexed { i, it ->
            AvailableFilter(it, filterValues[i])
        }
    }

    private data class AvailableFilter(
        val definition: FilterAttributeDefinition,
        val availableValues: List<List<String>>
    )

    private fun getCnfFilterValuesMap(cnfFilterValue: CnfFilterValue?): Map<String, List<String>> {
        if (cnfFilterValue?.selectedAttributeValues == null) {
            return emptyMap()
        }
        return cnfFilterValue.selectedAttributeValues
            .filter { it.selectedIds.isNotEmpty() }
            .associate { it.id to it.selectedIds }
    }
}
