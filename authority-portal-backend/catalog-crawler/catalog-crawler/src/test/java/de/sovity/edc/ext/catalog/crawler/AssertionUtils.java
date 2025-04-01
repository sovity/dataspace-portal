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

package de.sovity.edc.ext.catalog.crawler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertionUtils {
    @SneakyThrows
    public static void assertEqualJson(String expected, String actual) {
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    public static void assertEqualUsingJson(Object expected, Object actual) {
        assertEqualJson(JsonTestUtils.serialize(expected), JsonTestUtils.serialize(actual));
    }
}
