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

import de.sovity.edc.ext.catalog.crawler.dao.utils.RecordPatch;
import de.sovity.edc.ext.catalog.crawler.utils.CollectionUtils2;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class CatalogPatchApplier {

    @SneakyThrows
    public void applyDbUpdatesBatched(DSLContext dsl, CatalogPatch catalogPatch) {
        var insertionOrder = catalogPatch.insertionOrder();
        var deletionOrder = CollectionUtils2.reverse(insertionOrder);

        insertionOrder.stream()
                .map(RecordPatch::getInsertions)
                .filter(CollectionUtils2::isNotEmpty)
                .forEach(it -> dsl.batchInsert(it).execute());

        insertionOrder.stream()
                .map(RecordPatch::getUpdates)
                .filter(CollectionUtils2::isNotEmpty)
                .forEach(it -> dsl.batchUpdate(it).execute());

        deletionOrder.stream()
                .map(RecordPatch::getDeletions)
                .filter(CollectionUtils2::isNotEmpty)
                .forEach(it -> dsl.batchDelete(it).execute());
    }
}
