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

package de.sovity.authorityportal.web.auth

import de.sovity.authorityportal.api.model.UserRoleDto
import de.sovity.authorityportal.db.jooq.tables.records.UserRecord
import de.sovity.authorityportal.web.auth.providers.ElytronPropertyFileAuthUtils
import de.sovity.authorityportal.web.auth.providers.KeycloakJwtUtils
import de.sovity.authorityportal.web.services.FirstLoginService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.inject.Produces
import jakarta.ws.rs.core.SecurityContext

@ApplicationScoped
class LoggedInUserFactory(
    val context: SecurityContext,
    val keycloakJwtUtils: KeycloakJwtUtils,
    val elytronPropertyFileAuthUtils: ElytronPropertyFileAuthUtils,
    val firstLoginService: FirstLoginService
) {

    @Produces
    @RequestScoped
    fun getLoggedInUser(): LoggedInUser {
        if (context.userPrincipal == null) {
            return unauthenticatedLoggedInUser()
        }

        val userAndRoles = when (context.authenticationScheme) {
            "Basic" -> elytronPropertyFileAuthUtils.getUserAndRoles()
            else -> keycloakJwtUtils.getUserAndRoles()
        }

        // With the first successful login invited users will be approved
        // At this point all Keycloak required actions should be done
        // e.g. confirm E-Mail, setup 2FA
        firstLoginService.approveIfInvited(userAndRoles.user)

        return authenticatedLoggedInUser(userAndRoles)
    }

    private fun authenticatedLoggedInUser(userAndRoles: UserAndRoles) = LoggedInUser(
        true,
        userAndRoles.user.id,
        userAndRoles.user.organizationId,
        userAndRoles.roles
    )

    private fun unauthenticatedLoggedInUser() = LoggedInUser(
        false,
        "",
        null,
        setOf(UserRoleDto.UNAUTHENTICATED.name)
    )

    data class UserAndRoles(val user: UserRecord, val roles: Set<String>)
}
