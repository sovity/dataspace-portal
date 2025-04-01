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

package de.sovity.edc.ext.catalog.crawler.dao.connectors;


import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ConnectorRefTest {

    @Test
    void testEqualsTrue() {
        // arrange
        var a = new ConnectorRef("a", "1", "1", "1", "1");
        var b = new ConnectorRef("a", "2", "2", "2", "2");

        // act
        var result = a.equals(b);

        // assert
        assertThat(result).isTrue();
    }

    @Test
    void testEqualsFalse() {
        // arrange
        var a = new ConnectorRef("a", "1", "1", "1", "1");
        var b = new ConnectorRef("b", "1", "1", "1", "1");

        // act
        var result = a.equals(b);

        // assert
        assertThat(result).isFalse();
    }

    @Test
    void testSet() {
        // arrange
        var a = new ConnectorRef("a", "1", "1", "1", "1");
        var a2 = new ConnectorRef("a", "2", "2", "2", "2");
        var b = new ConnectorRef("b", "1", "1", "1", "1");

        // act
        var result = new HashSet<>(List.of(a, a2, b)).stream().map(ConnectorRef::getConnectorId).toList();

        // assert
        assertThat(result).containsExactlyInAnyOrder("a", "b");
    }
}
