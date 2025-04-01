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

import de.sovity.edc.ext.catalog.crawler.crawling.writing.utils.DiffUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DiffUtilsTest {

    @Test
    void testCompareLists() {
        // arrange
        List<Integer> existing = List.of(1, 2);
        List<String> fetched = List.of("1", "3");

        // act
        var actual = DiffUtils.compareLists(existing, Function.identity(), fetched, Integer::parseInt);

        // assert
        assertThat(actual.added()).containsExactly("3");
        assertThat(actual.updated()).containsExactly(new DiffUtils.DiffResultMatch<>(1, "1"));
        assertThat(actual.removed()).containsExactly(2);
    }
}
