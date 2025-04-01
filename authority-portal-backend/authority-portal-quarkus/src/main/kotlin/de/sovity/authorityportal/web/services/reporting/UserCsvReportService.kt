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

package de.sovity.authorityportal.web.services.reporting

import de.sovity.authorityportal.api.model.UserRoleDto
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.web.pages.usermanagement.UserRoleMapper
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserDetailService
import de.sovity.authorityportal.web.services.reporting.utils.CsvColumn
import de.sovity.authorityportal.web.services.reporting.utils.buildCsv
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.io.ByteArrayInputStream

@ApplicationScoped
class UserCsvReportService {

    @Inject
    lateinit var userDetailService: UserDetailService

    @Inject
    lateinit var userRoleMapper: UserRoleMapper

    @Inject
    lateinit var organizationService: OrganizationService

    data class UserReportRow(
        val userId: String,
        val organizationId: String?,
        val organizationName: String?,
        val firstName: String,
        val lastName: String,
        val roles: Set<UserRoleDto>,
        val email: String,
        val position: String?,
        val registrationStatus: UserRegistrationStatus
    )

    val columns = listOf<CsvColumn<UserReportRow>>(
        CsvColumn("User ID") { it.userId },
        CsvColumn("Organization ID") { it.organizationId ?: "" },
        CsvColumn("Organization Name") { it.organizationName ?: "" },
        CsvColumn("First Name") { it.firstName },
        CsvColumn("Last Name") { it.lastName },
        CsvColumn("Roles") { it.roles.toString() },
        CsvColumn("Email") { it.email },
        CsvColumn("Job Title") { it.position ?: "" },
        CsvColumn("Registration Status") { it.registrationStatus.toString() }
    )

    fun generateUserDetailsCsvReport(): ByteArrayInputStream {
        val rows = buildUserReportRows()
        return buildCsv(columns, rows)
    }

    private fun buildUserReportRows(): List<UserReportRow> {
        val userDetails = userDetailService.getAllUserDetails()
        val organizationNames = organizationService.getAllOrganizationNames()

        return userDetails.map {
            UserReportRow(
                userId = it.userId,
                organizationId = it.organizationId,
                organizationName = organizationNames[it.organizationId],
                firstName = it.firstName,
                lastName = it.lastName,
                roles = userRoleMapper.getUserRoles(it.roles),
                email = it.email,
                position = it.position,
                registrationStatus = it.registrationStatus
            )
        }
    }
}
