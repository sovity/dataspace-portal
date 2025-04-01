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

package de.sovity.authorityportal.web.utils.idmanagement

import de.sovity.authorityportal.db.jooq.Tables
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jooq.DSLContext

@ApplicationScoped
class OrganizationIdUtils(
    val dsl: DSLContext,
    val idUtils: IdUtils,
    @ConfigProperty(
        name = "authority-portal.organization.id.prefix",
        defaultValue = "BPN"
    ) val organizationIdPrefix: String,
    @ConfigProperty(
        name = "authority-portal.organization.id.length",
        defaultValue = "4"
    ) val organizationIdLength: Int
) {

    fun generateOrganizationId(): String {
        val usedOrganizationIds = getUsedOrganizationIds()
        var organizationId: String

        do {
            organizationId = getOrganizationIdCandidate(organizationIdPrefix, organizationIdLength)
        } while (usedOrganizationIds.contains(organizationId))

        return organizationId
    }

    private fun getOrganizationIdCandidate(prefix: String, identifierLength: Int): String {
        val identifier = idUtils.randomIdentifier(identifierLength)
        val checksum = idUtils.calculateVerificationDigits(identifier)
        return "${prefix}L$identifier$checksum"
    }

    private fun getUsedOrganizationIds(): Set<String> {
        val o = Tables.ORGANIZATION
        return dsl.select(o.ID)
            .from(o)
            .fetchSet(o.ID)
    }
}
