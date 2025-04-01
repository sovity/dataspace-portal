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

package de.sovity.edc.ext.catalog.crawler.crawling.writing.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeTracker {
    private boolean changed = false;

    public <T> void setIfChanged(
            T existing,
            T fetched,
            Consumer<T> setter
    ) {
        setIfChanged(existing, fetched, setter, Objects::equals);
    }

    public <T> void setIfChanged(
            T existing,
            T fetched,
            Consumer<T> setter,
            BiPredicate<T, T> equalityChecker
    ) {
        if (!equalityChecker.test(existing, fetched)) {
            setter.accept(fetched);
            changed = true;
        }
    }
}
