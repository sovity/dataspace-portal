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

package de.sovity.authorityportal.seeds.utils

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Our Dev User IDs also need to be UUIDs, as they are expected to exist in the keycloak.
 */
fun dummyDevUserUuid(i: Int): String = "00000000-0000-0000-0000-${i.toString().padStart(12, '0')}"
fun dummyDevOrganizationId(i: Int): String = "MDSL${i.toString().padStart(6, '0')}"
fun dummyDevOrganizationName(i: Int): String = "Organization $i"
fun dummyDevConnectorId(orgId: Int, connectorId: Int) =
    "${dummyDevOrganizationId(orgId)}.${dummyDevConnectorIdComponent(connectorId)}"
fun dummyDevConnectorIdComponent(i: Int): String = "CONN${i.toString().padStart(3, '0')}"
fun dummyDevAssetId(i: Int): String = "ASSET-${i.toString().padStart(8, '0')}"
fun dummyDate(i: Int): OffsetDateTime = LocalDate.parse("2024-01-01").atStartOfDay().atOffset(ZoneOffset.UTC).plusDays(i.toLong())
fun dummyDevContractOfferId(i: Int): String = "COFFER-${i.toString().padStart(8, '0')}"
