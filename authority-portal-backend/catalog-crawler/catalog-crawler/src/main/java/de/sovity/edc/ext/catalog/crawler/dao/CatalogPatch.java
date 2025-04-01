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

package de.sovity.edc.ext.catalog.crawler.dao;

import de.sovity.authorityportal.db.jooq.tables.records.ContractOfferRecord;
import de.sovity.authorityportal.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.catalog.crawler.dao.utils.RecordPatch;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Contains planned DB Row changes to be applied as batch.
 */
@Getter
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CatalogPatch {
    RecordPatch<DataOfferRecord> dataOffers = new RecordPatch<>();
    RecordPatch<ContractOfferRecord> contractOffers = new RecordPatch<>();

    public List<RecordPatch<?>> insertionOrder() {
        return List.of(dataOffers, contractOffers);
    }
}
