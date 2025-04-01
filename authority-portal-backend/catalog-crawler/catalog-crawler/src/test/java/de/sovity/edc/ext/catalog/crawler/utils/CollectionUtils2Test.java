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

package de.sovity.edc.ext.catalog.crawler.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class CollectionUtils2Test {
    @Test
    void difference() {
        assertThat(CollectionUtils2.difference(List.of(1, 2, 3), List.of(2, 3, 4))).containsExactly(1);
    }

    @Test
    void isNotEmpty_withEmptyList() {
        assertThat(CollectionUtils2.isNotEmpty(List.of())).isFalse();
    }

    @Test
    void isNotEmpty_withNull() {
        assertThat(CollectionUtils2.isNotEmpty(null)).isFalse();
    }

    @Test
    void isNotEmpty_withNonEmptyList() {
        assertThat(CollectionUtils2.isNotEmpty(List.of(1))).isTrue();
    }
}
