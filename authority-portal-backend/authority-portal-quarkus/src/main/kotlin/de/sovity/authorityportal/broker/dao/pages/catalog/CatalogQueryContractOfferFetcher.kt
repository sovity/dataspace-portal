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

import de.sovity.authorityportal.broker.dao.pages.dataoffer.model.ContractOfferRs
import de.sovity.authorityportal.broker.dao.utils.MultisetUtils
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.tables.DataOffer
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.Field
import org.jooq.impl.DSL

@ApplicationScoped
class CatalogQueryContractOfferFetcher {
    /**
     * Query a data offer's contract offers.
     *
     * @param d Data offer table
     * @return [Field] of [ContractOfferRs]s
     */
    fun getContractOffers(d: DataOffer): Field<List<ContractOfferRs>> {
        val co = Tables.CONTRACT_OFFER

        val query = DSL.select(
            co.CONTRACT_OFFER_ID,
            co.UI_POLICY_JSON.cast(String::class.java).`as`("policyUiJson"),
            co.CREATED_AT,
            co.UPDATED_AT
        ).from(co).where(
            co.CONNECTOR_ID.eq(d.CONNECTOR_ID),
            co.ASSET_ID.eq(d.ASSET_ID)
        ).orderBy(
            co.CREATED_AT.desc()
        )

        return MultisetUtils.multiset(query, ContractOfferRs::class)
    }
}
