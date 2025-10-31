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

package de.sovity.authorityportal.web.services

import de.sovity.authorityportal.api.model.CentralComponentCreateRequest
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.tables.records.ComponentRecord
import de.sovity.authorityportal.web.utils.TimeUtils
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext

@ApplicationScoped
class CentralComponentService(
    val dsl: DSLContext,
    val timeUtils: TimeUtils
) {

    fun getCentralComponentOrThrow(centralComponentId: String): ComponentRecord {
        return getComponent(centralComponentId) ?: error("Component with id $centralComponentId not found")
    }

    fun getCentralComponentsByEnvironment(envId: String): List<ComponentRecord> {
        val c = Tables.COMPONENT

        return dsl.selectFrom(c)
            .where(c.ENVIRONMENT.eq(envId))
            .fetch()
    }

    fun getCentralComponentsByOrganizationId(organizationId: String): List<ComponentRecord> {
        val c = Tables.COMPONENT

        return dsl.selectFrom(c)
            .where(c.ORGANIZATION_ID.eq(organizationId))
            .fetch()
    }

    private fun getComponent(centralComponentId: String): ComponentRecord? {
        val c = Tables.COMPONENT

        return dsl.selectFrom(c)
            .where(c.ID.eq(centralComponentId))
            .fetchOne()
    }

    fun createCentralComponent(
        centralComponentId: String,
        environment: String,
        clientId: String,
        centralComponentCreateRequest: CentralComponentCreateRequest,
        organizationId: String?,
        createdBy: String?
    ) {
        dsl.newRecord(Tables.COMPONENT).also {
            it.id = centralComponentId
            it.organizationId = organizationId
            it.environment = environment
            it.clientId = clientId
            it.name = centralComponentCreateRequest.name.trim()
            it.homepageUrl = centralComponentCreateRequest.homepageUrl?.trim()
            it.endpointUrl = centralComponentCreateRequest.endpointUrl.trim()
            it.createdBy = createdBy
            it.createdAt = timeUtils.now()

            it.insert()
        }
    }

    fun deleteCentralComponent(centralComponentId: String) {
        val c = Tables.COMPONENT

        dsl.deleteFrom(c)
            .where(c.ID.eq(centralComponentId))
            .execute()
    }

    fun updateCentralComponentsCreator(newCreatedBy: String, oldCreatedBy: String) {
        val c = Tables.COMPONENT
        dsl.update(c)
            .set(c.CREATED_BY, newCreatedBy)
            .where(c.CREATED_BY.eq(oldCreatedBy))
            .execute()
    }
}
