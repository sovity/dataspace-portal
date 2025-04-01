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

package de.sovity.edc.ext.catalog.crawler.dao.contract_offers;

import de.sovity.authorityportal.db.jooq.Tables;
import de.sovity.authorityportal.db.jooq.tables.records.ContractOfferRecord;
import org.jooq.DSLContext;

import java.util.List;

public class ContractOfferQueries {

    public List<ContractOfferRecord> findByConnectorId(DSLContext dsl, String connectorId) {
        var co = Tables.CONTRACT_OFFER;
        return dsl.selectFrom(co).where(co.CONNECTOR_ID.eq(connectorId)).stream().toList();
    }
}
