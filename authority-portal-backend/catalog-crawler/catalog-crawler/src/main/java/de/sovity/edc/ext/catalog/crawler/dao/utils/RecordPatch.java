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

package de.sovity.edc.ext.catalog.crawler.dao.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jooq.UpdatableRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains planned DB Row changes to be applied as batch.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordPatch<T extends UpdatableRecord<T>> {
    List<T> insertions = new ArrayList<>();
    List<T> updates = new ArrayList<>();
    List<T> deletions = new ArrayList<>();

    public void insert(T record) {
        insertions.add(record);
    }

    public void update(T record) {
        updates.add(record);
    }

    public void delete(T record) {
        deletions.add(record);
    }
}
