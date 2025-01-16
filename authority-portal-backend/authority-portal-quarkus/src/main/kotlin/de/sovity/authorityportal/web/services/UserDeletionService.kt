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

import de.sovity.authorityportal.api.model.PossibleCreatorSuccessor
import de.sovity.authorityportal.api.model.UserDeletionCheck
import de.sovity.authorityportal.db.jooq.tables.records.OrganizationRecord
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserDeletionService(
    val userService: UserService,
    val organizationService: OrganizationService,
    val keycloakService: KeycloakService,
    val connectorService: ConnectorService,
    val centralComponentService: CentralComponentService
) {

    fun checkUserDeletion(userId: String): UserDeletionCheck {
        val user = userService.getUserOrThrow(userId)
        val organization = organizationService.getOrganizationOrThrow(user.organizationId)
        val authorityAdmins = keycloakService.getAuthorityAdmins()
        val participantAdmins = keycloakService.getParticipantAdmins(organization.id)

        val isLastAuthorityAdmin = authorityAdmins.singleOrNull()?.userId == userId
        val isLastParticipantAdmin = participantAdmins.singleOrNull()?.userId == userId
        val isOrganizationCreator = organization.createdBy == userId

        val possibleCreatorSuccessors = if (!isLastParticipantAdmin && isOrganizationCreator) {
            participantAdmins
                .filter { it.userId != userId }
                .map {
                    PossibleCreatorSuccessor(
                        userId = it.userId,
                        firstName = it.firstName,
                        lastName = it.lastName
                    )
                }
        } else {
            emptyList()
        }

        return UserDeletionCheck(
            userId = userId,
            canBeDeleted = !isLastAuthorityAdmin,
            isLastParticipantAdmin = isLastParticipantAdmin,
            isOrganizationCreator = isOrganizationCreator,
            possibleCreatorSuccessors = possibleCreatorSuccessors
        )
    }

    fun deleteUserAndHandleDependencies(
        userDeletionCheck: UserDeletionCheck,
        successorUserId: String?,
        userId: String,
        adminUserId: String,
        organization: OrganizationRecord
    ) {
        if (userDeletionCheck.isOrganizationCreator) {
            changeOrganizationCreator(successorUserId, userId, adminUserId, organization)
        }
        connectorService.updateConnectorsCreator(organization.createdBy, userId)
        centralComponentService.updateCentralComponentsCreator(organization.createdBy, userId)
        userService.deleteInvitationReference(userId)
        userService.deleteUser(userId)
        keycloakService.deleteUser(userId)

        Log.info(
            "User deleted. Ownership of connectors and central components handed over to organization creator. " +
                "userId=$userId, organizationCreator=${organization.createdBy}, adminUserId=$adminUserId."
        )
    }

    private fun changeOrganizationCreator(
        successorUserId: String?,
        userId: String,
        adminUserId: String,
        organization: OrganizationRecord
    ) {
        if (successorUserId == null) {
            Log.error("Trying to delete organization creator without without a successor. userId=$userId, adminUserId=$adminUserId.")
            error("Trying to delete organization creator without without a successor.")
        }
        organization.createdBy = successorUserId
        organization.update()

        Log.info("Organization creator changed. organizationId=${organization.id}, successorUserId=$successorUserId, adminUserId=$adminUserId.")
    }
}
