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

package de.sovity.authorityportal.web.pages.usermanagement

import de.sovity.authorityportal.api.model.IdResponse
import de.sovity.authorityportal.api.model.UserDeletionCheck
import de.sovity.authorityportal.web.services.ConnectorService
import de.sovity.authorityportal.web.services.OrganizationDeletionService
import de.sovity.authorityportal.web.services.OrganizationService
import de.sovity.authorityportal.web.services.UserDeletionService
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.utils.TimeUtils
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserDeletionApiService(
    val keycloakService: KeycloakService,
    val userService: UserService,
    val organizationService: OrganizationService,
    val connectorService: ConnectorService,
    val timeUtils: TimeUtils,
    val userDeletionService: UserDeletionService,
    val organizationDeletionService: OrganizationDeletionService
) {

    fun checkUserDeletion(userId: String): UserDeletionCheck {
        return userDeletionService.checkUserDeletion(userId)
    }

    fun handleUserDeletion(userId: String, successorUserId: String?, adminUserId: String): IdResponse {
        val userDeletionCheck = userDeletionService.checkUserDeletion(userId)
        val user = userService.getUserOrThrow(userId)
        val organization = organizationService.getOrganizationOrThrow(user.organizationId)

        if (!userDeletionCheck.canBeDeleted) {
            Log.error("User can not be deleted. The reason could be, that they are the last Authority Admin. userId=$userId, adminUserId=$adminUserId.")
            error("User can not be deleted. The reason could be, that they are the last Authority Admin.")
        }

        if (userDeletionCheck.isLastParticipantAdmin) {
            organizationDeletionService.deleteOrganizationAndDependencies(organization.id, adminUserId)
        } else {
            userDeletionService.deleteUserAndHandleDependencies(userDeletionCheck, successorUserId, userId, adminUserId, organization)
        }

        return IdResponse(userId, timeUtils.now())
    }
}
