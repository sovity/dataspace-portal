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
package de.sovity.authorityportal.broker.dao.pages.dataoffer

import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.web.utils.TimeUtils
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext

@ApplicationScoped
class ViewCountLogger(
    val dsl: DSLContext,
    val timeUtils: TimeUtils
) {
    fun increaseDataOfferViewCount(assetId: String, endpoint: String) {
        val v = Tables.DATA_OFFER_VIEW_COUNT
        dsl.insertInto(v, v.ASSET_ID, v.CONNECTOR_ID, v.DATE).values(assetId, endpoint, timeUtils.now()).execute()
    }
}
