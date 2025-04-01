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

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.authorityportal.db.jooq.Tables;
import de.sovity.authorityportal.db.jooq.tables.records.ContractOfferRecord;
import de.sovity.authorityportal.db.jooq.tables.records.DataOfferRecord;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

class DataOfferWriterTestResultHelper {
    private final @NotNull Map<String, DataOfferRecord> dataOffers;
    private final @NotNull Map<String, Map<String, ContractOfferRecord>> contractOffers;

    DataOfferWriterTestResultHelper(DSLContext dsl) {
        this.dataOffers = dsl.selectFrom(Tables.DATA_OFFER).fetchMap(Tables.DATA_OFFER.ASSET_ID);
        this.contractOffers = dsl.selectFrom(Tables.CONTRACT_OFFER).stream().collect(groupingBy(
                ContractOfferRecord::getAssetId,
                Collectors.toMap(ContractOfferRecord::getContractOfferId, Function.identity())
        ));
    }

    public DataOfferRecord getDataOffer(String assetId) {
        return dataOffers.get(assetId);
    }

    public int numDataOffers() {
        return dataOffers.size();
    }

    public int numContractOffers(String assetId) {
        return contractOffers.getOrDefault(assetId, Map.of()).size();
    }

    public ContractOfferRecord getContractOffer(String assetId, String contractOfferId) {
        return contractOffers.getOrDefault(assetId, Map.of()).get(contractOfferId);
    }
}
