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

package de.sovity.authorityportal.broker.dao.utils;

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Some things are easier to fetch as json into a string with JooQ.
 * In that case we need to deserialize that string into an object of our choice afterwards.
 */
object JsonDeserializationUtils {
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val TYPE_STRING_LIST_3: TypeReference<List<List<List<String>>>> = object : TypeReference<List<List<List<String>>>>() {}

    fun read3dStringList(json: String): List<List<List<String>>> {
        return objectMapper.readValue(json, TYPE_STRING_LIST_3)
    }
}
