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

package de.sovity.edc.ext.catalog.crawler.dao.data_offers;

import de.sovity.authorityportal.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.utils.ChangeTracker;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.dao.utils.JsonbUtils;
import de.sovity.edc.ext.catalog.crawler.utils.JsonUtils2;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.ShortDescriptionBuilder;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Creates or updates {@link DataOfferRecord} DB Rows.
 * <p>
 * (Or at least prepares them for batch inserts / updates)
 */
@RequiredArgsConstructor
public class DataOfferRecordUpdater {
    private final ShortDescriptionBuilder shortDescriptionBuilder;

    /**
     * Create a new {@link DataOfferRecord}.
     *
     * @param connectorRef connector
     * @param fetchedDataOffer new db row data
     * @return new db row
     */
    public DataOfferRecord newDataOffer(
            ConnectorRef connectorRef,
            FetchedDataOffer fetchedDataOffer
    ) {
        var dataOffer = new DataOfferRecord();
        var connectorId = connectorRef.getConnectorId();

        dataOffer.setConnectorId(connectorId);
        dataOffer.setAssetId(fetchedDataOffer.getAssetId());
        dataOffer.setCreatedAt(OffsetDateTime.now());
        updateDataOffer(dataOffer, fetchedDataOffer, true);
        return dataOffer;
    }


    /**
     * Update existing {@link DataOfferRecord}.
     *
     * @param record existing row
     * @param fetchedDataOffer changes to be incorporated
     * @param changed whether the data offer should be marked as updated simply because the contract offers changed
     * @return whether any fields were updated
     */
    public boolean updateDataOffer(
            DataOfferRecord record,
            FetchedDataOffer fetchedDataOffer,
            boolean changed
    ) {
        var asset = fetchedDataOffer.getUiAsset();
        var changes = new ChangeTracker(changed);

        changes.setIfChanged(
                blankIfNull(record.getAssetTitle()),
                blankIfNull(asset.getTitle()),
                record::setAssetTitle
        );

        changes.setIfChanged(
                blankIfNull(record.getDescriptionNoMarkdown()),
                shortDescriptionBuilder.extractMarkdownText(blankIfNull(asset.getDescription())),
                record::setDescriptionNoMarkdown
        );

        changes.setIfChanged(
                blankIfNull(record.getShortDescriptionNoMarkdown()),
                blankIfNull(asset.getDescriptionShortText()),
                record::setShortDescriptionNoMarkdown
        );

        changes.setIfChanged(
                blankIfNull(record.getDataCategory()),
                blankIfNull(asset.getDataCategory()),
                record::setDataCategory
        );

        changes.setIfChanged(
                blankIfNull(record.getDataSubcategory()),
                blankIfNull(asset.getDataSubcategory()),
                record::setDataSubcategory
        );

        changes.setIfChanged(
                blankIfNull(record.getDataModel()),
                blankIfNull(asset.getDataModel()),
                record::setDataModel
        );

        changes.setIfChanged(
                blankIfNull(record.getTransportMode()),
                blankIfNull(asset.getTransportMode()),
                record::setTransportMode
        );

        changes.setIfChanged(
                blankIfNull(record.getGeoReferenceMethod()),
                blankIfNull(asset.getGeoReferenceMethod()),
                record::setGeoReferenceMethod
        );

        changes.setIfChanged(
                emptyIfNull(record.getKeywords()),
                emptyIfNull(asset.getKeywords()),
                it -> {
                    record.setKeywords(new ArrayList<>(it));
                    record.setKeywordsCommaJoined(String.join(", ", it));
                }
        );

        changes.setIfChanged(
                JsonbUtils.getDataOrNull(record.getUiAssetJson()),
                fetchedDataOffer.getUiAssetJson(),
                it -> record.setUiAssetJson(JSONB.jsonb(it)),
                JsonUtils2::isEqualJson
        );

        if (changes.isChanged()) {
            record.setUpdatedAt(OffsetDateTime.now());
        }

        return changes.isChanged();
    }

    private String blankIfNull(String string) {
        return string == null ? "" : string;
    }

    private <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return collection == null ? List.of() : collection;
    }

    private <T> Collection<T> emptyIfNull(T[] array) {
        return array == null ? List.of() : Arrays.asList(array);
    }

}
