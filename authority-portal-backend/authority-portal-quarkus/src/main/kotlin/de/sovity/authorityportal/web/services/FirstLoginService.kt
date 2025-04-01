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

import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.db.jooq.tables.records.UserRecord
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class FirstLoginService {

    @Inject
    lateinit var organizationService: OrganizationService

    fun approveIfInvited(user: UserRecord) {
        if (user.registrationStatus == UserRegistrationStatus.INVITED) {
            user.registrationStatus = UserRegistrationStatus.ONBOARDING
            user.update()

            // Check if user is Participant Admin of an invited organization
            val organization = organizationService.getOrganizationOrThrow(user.organizationId)
            if (organization.registrationStatus == OrganizationRegistrationStatus.INVITED && organization.createdBy == user.id) {
                organization.registrationStatus = OrganizationRegistrationStatus.ONBOARDING
                organization.update()
            }
        }
    }
}
