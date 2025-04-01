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

import de.sovity.authorityportal.api.model.organization.OrganizationDeletionCheck
import de.sovity.authorityportal.web.pages.centralcomponentmanagement.CentralComponentManagementApiService
import de.sovity.authorityportal.web.pages.connectormanagement.ConnectorManagementApiService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrganizationDeletionService(
    val connectorManagementApiService: ConnectorManagementApiService,
    val connectorService: ConnectorService,
    val centralComponentManagementApiService: CentralComponentManagementApiService,
    val userService: UserService,
    val organizationService: OrganizationService,
    val keycloakService: KeycloakService
) {

    fun checkOrganizationDeletion(organizationId: String): OrganizationDeletionCheck {
        return OrganizationDeletionCheck(
            organizationId = organizationId,
            canBeDeleted = !hasLastAuthorityAdmins(organizationId)
        )
    }

    fun deleteOrganizationAndDependencies(organizationId: String, adminUserId: String) {
        if (!checkOrganizationDeletion(organizationId).canBeDeleted) {
            Log.error("Organization can not be deleted. The last Authority Admins are part of this organization. organizationId=$organizationId, adminUserId=$adminUserId.")
            error("Organization can not be deleted. The last Authority Admins are part of this organization.")
        }

        connectorManagementApiService.deleteAllOrganizationConnectors(organizationId)
        connectorService.deleteProviderReferences(organizationId)
        centralComponentManagementApiService.deleteAllOrganizationCentralComponents(organizationId)

        val orgMemberIds = userService.getUsersByOrganizationId(organizationId).map { it.id }
        userService.deleteInvitationReferencesToOrgMembers(orgMemberIds)
        userService.deleteOrganizationIds(orgMemberIds)

        organizationService.deleteOrganization(organizationId)
        userService.deleteUsers(orgMemberIds)

        keycloakService.deleteUsers(orgMemberIds)
        keycloakService.deleteOrganization(organizationId)

        Log.info(
            "Organization and related users, connectors and central components deleted. " +
                "organization=$organizationId, adminUserId=$adminUserId."
        )
    }

    private fun hasLastAuthorityAdmins(organizationId: String): Boolean {
        val adminUserIds = keycloakService.getAuthorityAdmins().map { it.userId }
        return !userService.userExistsOutsideOrg(adminUserIds, organizationId)
    }
}
