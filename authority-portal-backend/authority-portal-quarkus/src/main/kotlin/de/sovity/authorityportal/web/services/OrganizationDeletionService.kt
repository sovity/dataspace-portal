/*
 * Copyright (c) 2024 sovity GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      sovity GmbH - initial implementation
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
