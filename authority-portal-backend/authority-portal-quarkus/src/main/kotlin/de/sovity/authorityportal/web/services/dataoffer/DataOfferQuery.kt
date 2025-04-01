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

package de.sovity.authorityportal.web.services.dataoffer

import com.github.t9t.jooq.json.JsonbDSL
import de.sovity.authorityportal.api.model.catalog.ConnectorOnlineStatusDto
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.tables.Connector
import de.sovity.edc.ext.wrapper.api.common.model.DataSourceAvailability
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL
import java.time.OffsetDateTime

@ApplicationScoped
class DataOfferQuery(
    val dsl: DSLContext
) {

    fun getConnectorCountsByOrganizationIdsForEnvironment(environmentId: String): Map<String, Int> {
        val c = Tables.CONNECTOR

        return dsl.select(c.ORGANIZATION_ID, DSL.count())
            .from(c)
            .where(c.ENVIRONMENT.eq(environmentId))
            .groupBy(c.ORGANIZATION_ID)
            .fetchMap(c.ORGANIZATION_ID, DSL.count())
    }

    data class DataOfferCountRs(
        val organizationId: String,
        val liveOffers: Int,
        val onRequestOffers: Int
    )

    fun countAllOrganizationDataOffers(
        environmentId: String,
        conditionFn: (Connector) -> Condition = { DSL.trueCondition() }
    ): List<DataOfferCountRs> {
        val c = Tables.CONNECTOR

        return dsl.select(
            c.ORGANIZATION_ID.`as`("organizationId"),
            getDataOfferCount(c.ORGANIZATION_ID, environmentId, DataSourceAvailability.LIVE, "live").`as`("liveOffers"),
            getDataOfferCount(c.ORGANIZATION_ID, environmentId, DataSourceAvailability.ON_REQUEST, "on_request").`as`("onRequestOffers")
        )
            .from(c)
            .where(conditionFn(c))
            .fetchInto(DataOfferCountRs::class.java)
    }

    private fun getDataOfferCount(
        organizationId: Field<String>,
        environmentId: String,
        dataSourceAvailability: DataSourceAvailability,
        suffix: String
    ): Field<Int> {
        val c = Tables.CONNECTOR.`as`("c_$suffix")
        val d = Tables.DATA_OFFER.`as`("d_$suffix")

        val availability = JsonbDSL.fieldByKeyText(d.UI_ASSET_JSON, "dataSourceAvailability")
        val isSameAvailability = when (dataSourceAvailability) {
            DataSourceAvailability.ON_REQUEST -> availability.eq("ON_REQUEST")
            DataSourceAvailability.LIVE -> availability.ne("ON_REQUEST").or(availability.isNull())
        }

        return DSL.select(DSL.coalesce(DSL.count(), DSL.`val`(0)))
            .from(d)
            .join(c).on(c.CONNECTOR_ID.eq(d.CONNECTOR_ID))
            .where(
                c.ORGANIZATION_ID.eq(organizationId),
                c.ENVIRONMENT.eq(environmentId),
                isSameAvailability
            )
            .asField()
    }

    fun countOrganizationDataOffers(
        environmentId: String,
        organizationId: String
    ): DataOfferCountRs? =
        countAllOrganizationDataOffers(environmentId) { c -> c.ORGANIZATION_ID.eq(organizationId) }
            .singleOrNull()

    data class DataOfferInfoRs(
        val connectorId: String,
        val organizationId: String,
        val onlineStatus: ConnectorOnlineStatusDto,
        val offlineSinceOrLastUpdatedAt: OffsetDateTime,
        val dataOfferName: String,
        val dataOfferId: String,
        val dataSourceAvailability: String
    )

    fun getDataOffersForConnectorIdsAndEnvironment(
        environmentId: String,
        connectorIds: List<String>
    ): List<DataOfferInfoRs> {
        val c = Tables.CONNECTOR
        val d = Tables.DATA_OFFER

        val dataSourceAvailabilityField = JsonbDSL.fieldByKeyText(d.UI_ASSET_JSON, "dataSourceAvailability")

        return dsl.select(
            c.CONNECTOR_ID,
            c.ORGANIZATION_ID,
            c.ONLINE_STATUS,
            c.LAST_SUCCESSFUL_REFRESH_AT.`as`("offlineSinceOrLastUpdatedAt"),
            d.ASSET_TITLE.`as`("dataOfferName"),
            d.ASSET_ID.`as`("dataOfferId"),
            dataSourceAvailabilityField.`as`("dataSourceAvailability")
        )
            .from(d)
            .join(c).on(c.CONNECTOR_ID.eq(d.CONNECTOR_ID))
            .where(c.ENVIRONMENT.eq(environmentId))
            .fetchInto(DataOfferInfoRs::class.java)
    }
}
