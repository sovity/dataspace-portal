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

import com.github.t9t.jooq.json.JsonbDSL
import de.sovity.authorityportal.broker.dao.utils.mapInline
import de.sovity.authorityportal.db.jooq.tables.Connector
import de.sovity.authorityportal.db.jooq.tables.DataOffer
import de.sovity.authorityportal.db.jooq.tables.DataOfferViewCount
import de.sovity.authorityportal.db.jooq.tables.Organization
import de.sovity.authorityportal.web.environment.CatalogDataspaceConfig
import org.jooq.Field
import org.jooq.Table
import org.jooq.impl.DSL
import java.time.OffsetDateTime


/**
 * Tables and fields used in the catalog page query.
 *
 *
 * Having this as a class makes access to computed fields (e.g. asset properties) easier.
 */
class CatalogQueryFields(
    var connectorTable: Connector,
    var dataOfferTable: DataOffer,
    var organizationTable: Organization,
    private var dataOfferViewCountTable: DataOfferViewCount,
    private var dataSpaceConfigCatalog: CatalogDataspaceConfig
) {
    // Asset Properties from JSON to be used in sorting / filtering
    var dataSpace: Field<String>

    // This date should always be non-null
    // It's used in the UI to display the last relevant change date of a connector
    var offlineSinceOrLastUpdatedAt: Field<OffsetDateTime> = offlineSinceOrLastUpdatedAt(connectorTable)

    init {
        dataSpace = buildDataSpaceField(connectorTable, dataSpaceConfigCatalog)
    }

    private fun buildDataSpaceField(
        connectorTable: Connector,
        dataSpaceConfigCatalog: CatalogDataspaceConfig
    ): Field<String> {
        return connectorTable.CONNECTOR_ID.mapInline(
            dataSpaceConfigCatalog.namesByConnectorId,
            dataSpaceConfigCatalog.defaultName
        )
    }

    fun withSuffix(additionalSuffix: String): CatalogQueryFields {
        return CatalogQueryFields(
            connectorTable.`as`(withSuffix(connectorTable, additionalSuffix)),
            dataOfferTable.`as`(withSuffix(dataOfferTable, additionalSuffix)),
            organizationTable.`as`(withSuffix(organizationTable, additionalSuffix)),
            dataOfferViewCountTable.`as`(withSuffix(dataOfferViewCountTable, additionalSuffix)),
            dataSpaceConfigCatalog
        )
    }

    private fun withSuffix(table: Table<*>, additionalSuffix: String): String {
        return "%s_%s".format(table.name, additionalSuffix)
    }

    val viewCount: Field<Int>
        get() {
            val subquery = DSL.select(DSL.count())
                .from(dataOfferViewCountTable)
                .where(
                    dataOfferViewCountTable.ASSET_ID.eq(dataOfferTable.ASSET_ID)
                        .and(dataOfferViewCountTable.CONNECTOR_ID.eq(connectorTable.CONNECTOR_ID))
                )

            return subquery.asField()
        }

    val dataSourceAvailabilityLabel: Field<String> =
        DSL.case_(getAssetStringProperty("dataSourceAvailability"))
            .`when`("ON_REQUEST", "On Request")
            .else_("Available")

    val dataSourceAvailability: Field<String> = getAssetStringProperty("dataSourceAvailability")

    fun getAssetStringProperty(name: String): Field<String> {
        return JsonbDSL.fieldByKeyText(dataOfferTable.UI_ASSET_JSON, name)
    }

    companion object {
        fun offlineSinceOrLastUpdatedAt(connectorTable: Connector): Field<OffsetDateTime> {
            return DSL.coalesce(
                connectorTable.LAST_SUCCESSFUL_REFRESH_AT,
                connectorTable.CREATED_AT
            )
        }
    }
}
