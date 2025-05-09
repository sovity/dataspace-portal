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

package de.sovity.authorityportal.broker.services.api

import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicy
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class DataOfferMapper(
    val objectMapper: ObjectMapper
) {
    fun readUiAsset(json: String): UiAsset = objectMapper.readValue(json, UiAsset::class.java)
    fun readUiPolicy(json: String): UiPolicy = objectMapper.readValue(json, UiPolicy::class.java)
}
