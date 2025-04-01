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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtils2Test {
    @Test
    void equalityTests() {
        assertTrue(JsonUtils2.isEqualJson(null, null));
        assertTrue(JsonUtils2.isEqualJson("null", "null"));
        assertTrue(JsonUtils2.isEqualJson("{}", "{}"));
        assertTrue(JsonUtils2.isEqualJson("{\"a\": true, \"b\": \"hello\"}", "{\"a\": true,\"b\": \"hello\"}"));
        assertTrue(JsonUtils2.isEqualJson("{\"a\": true, \"b\": \"hello\"}", "{\"b\": \"hello\", \"a\": true}"));

        assertFalse(JsonUtils2.isEqualJson(null, "1"));
        assertFalse(JsonUtils2.isEqualJson("1", null));
    }
}
