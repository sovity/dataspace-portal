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

import de.sovity.edc.ext.catalog.crawler.utils.MapUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiffUtils {
    /**
     * Tries to match two collections by a key, then collects planned change sets as {@link DiffResult}.
     *
     * @param existing list of existing elements
     * @param existingKeyFn existing elements key extractor
     * @param fetched list of fetched elements
     * @param fetchedKeyFn fetched elements key extractor
     * @param <A> first collection type
     * @param <B> second collection type
     * @param <K> key type
     */
    public static <A, B, K> DiffResult<A, B> compareLists(
            Collection<A> existing,
            Function<A, K> existingKeyFn,
            Collection<B> fetched,
            Function<B, K> fetchedKeyFn
    ) {
        var existingByKey = MapUtils.associateBy(existing, existingKeyFn);
        var fetchedByKey = MapUtils.associateBy(fetched, fetchedKeyFn);

        var keys = new HashSet<>(existingByKey.keySet());
        keys.addAll(fetchedByKey.keySet());

        var result = new DiffResult<A, B>();

        keys.forEach(key -> {
            var existingItem = existingByKey.get(key);
            var fetchedItem = fetchedByKey.get(key);

            if (existingItem == null) {
                result.added.add(fetchedItem);
            } else if (fetchedItem == null) {
                result.removed.add(existingItem);
            } else {
                result.updated.add(new DiffResultMatch<>(existingItem, fetchedItem));
            }
        });

        return result;
    }

    /**
     * Result of comparing two collections by keys.
     *
     * @param added elements that are present in fetched collection but not in existing
     * @param updated elements that are present in both collections
     * @param removed elements that are present in existing collection but not in fetched
     * @param <A> existing item type
     * @param <B> fetched item type
     */
    public record DiffResult<A, B>(List<B> added, List<DiffResultMatch<A, B>> updated, List<A> removed) {
        DiffResult() {
            this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
    }

    /**
     * Pair of elements that are present in both collections.
     *
     * @param existing existing item
     * @param fetched fetched item
     * @param <A> existing item type
     * @param <B> fetched item type
     */
    public record DiffResultMatch<A, B>(A existing, B fetched) {
    }
}
