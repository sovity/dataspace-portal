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

package de.sovity.authorityportal.web.thirdparty.keycloak

import de.sovity.authorityportal.web.thirdparty.keycloak.model.ApplicationRole
import de.sovity.authorityportal.web.thirdparty.keycloak.model.KeycloakUserDto
import de.sovity.authorityportal.web.thirdparty.keycloak.model.OrganizationRole
import de.sovity.authorityportal.web.thirdparty.keycloak.model.RequiredAction
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.GroupsResource
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation

@ApplicationScoped
class KeycloakService(
    val keycloak: Keycloak,

    val keycloakUserMapper: KeycloakUserMapper,

    @ConfigProperty(name = "quarkus.keycloak.admin-client.realm")
    val keycloakRealm: String,
) {
    internal fun realmApi(): RealmResource = keycloak.realm(keycloakRealm)
    internal fun groupsApi(): GroupsResource = realmApi().groups()
    internal fun usersApi(): UsersResource = realmApi().users()
    internal fun rolesApi(): RolesResource = realmApi().roles()

    fun createUser(email: String, firstName: String, lastName: String, password: String? = null): String {
        require(email.isNotBlank()) { "Email must not be blank" }
        val user = UserRepresentation().also {
            it.isEnabled = true
            it.requiredActions = listOfNotNull(
                RequiredAction.UPDATE_PASSWORD.stringRepresentation.takeIf { password == null },
                RequiredAction.CONFIGURE_TOTP.stringRepresentation,
                RequiredAction.VERIFY_EMAIL.stringRepresentation
            )
            it.email = email.trim()
            it.firstName = firstName.trim()
            it.lastName = lastName.trim()

            if (password != null) {
                it.credentials = listOf(
                    CredentialRepresentation().also { credentials ->
                        credentials.isTemporary = false
                        credentials.type = CredentialRepresentation.PASSWORD
                        credentials.value = password
                    }
                )
            }
        }

        val response = usersApi().create(user)

        if (response.status == Response.Status.CONFLICT.statusCode) {
            throw WebApplicationException("User already exists", response.status)
        } else if (response.status != Response.Status.CREATED.statusCode) {
            throw WebApplicationException("Request failed: ${response.statusInfo.reasonPhrase}", response.status)
        }

        return getUserIdByEmailOrNull(email)!!
    }

    fun createKeycloakUserAndOrganization(
        organizationId: String,
        userEmail: String,
        userFirstName: String,
        userLastName: String,
        userOrganizationRole: OrganizationRole,
        userPassword: String?
    ): String {
        require(organizationId.isNotBlank()) { "Organization ID must not be blank" }
        require(userEmail.isNotBlank()) { "User email must not be blank" }

        // To avoid syncing issues, we assume our DB to be the source of truth, so we need to delete the potentially
        // existing user in KC
        val userIdIfExists = getUserIdByEmailOrNull(userEmail)
        if (userIdIfExists != null) {
            deleteUserSafely(userIdIfExists)
        }

        val userId = createUser(
            email = userEmail,
            firstName = userFirstName,
            lastName = userLastName,
            password = userPassword
        )

        try {
            createOrganization(organizationId)
            joinOrganization(userId, organizationId, userOrganizationRole)
        } catch (e: Exception) {
            deleteUserSafely(userId)
            deleteOrganization(organizationId)
            throw e
        }

        return userId
    }

    fun getUserIdByEmailOrNull(email: String): String? {
        require(email.isNotBlank()) { "Email must not be blank" }
        return usersApi().searchByEmail(email, true).firstOrNull()?.id
    }

    fun deleteUserSafely(userId: String) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        usersApi().delete(userId)
    }

    fun deleteUsersSafely(userIds: List<String>) {
        userIds.forEach {
            require(it.isNotBlank()) { "User ID must not be blank" }
        }
        userIds.forEach { deleteUserSafely(it) }
    }

    fun deactivateUser(userId: String) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        setUserEnabled(userId, false)
    }

    fun reactivateUser(userId: String) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        setUserEnabled(userId, true)
    }

    private fun setUserEnabled(userId: String, enabled: Boolean) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val user = usersApi().get(userId).toRepresentation()
        user.isEnabled = enabled
        usersApi().get(userId).update(user)
    }

    fun updateUser(userId: String, firstName: String, lastName: String, email: String?) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        require(email == null || email.isNotBlank()) { "Email must be null or not blank"}

        val userResource = usersApi().get(userId)
        val user = userResource.toRepresentation()
        user.firstName = firstName.trim()
        user.lastName = lastName.trim()

        val trimmedEmail = email?.trim()

        if (trimmedEmail != null && user.email != trimmedEmail) {
            user.email = trimmedEmail
            user.isEmailVerified = false
            user.requiredActions = (user.requiredActions + RequiredAction.VERIFY_EMAIL.stringRepresentation).distinct()
            forceLogout(user.id)
        }

        usersApi().get(userId).update(user)
    }

    fun getUserRoles(userId: String): Set<String> {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        return usersApi().get(userId).roles().realmLevel()
            .listEffective()
            .filter { it.name.startsWith("UR_") || it.name.startsWith("AR_") }
            .map { it.name }
            .toSet()
    }

    fun getOrganizationMembers(organizationId: String): List<KeycloakUserDto> {
        require(organizationId.isNotBlank()) { "Organization ID must not be blank" }
        val orgGroupId = getOrganizationByOrganizationIdOrNull(organizationId)?.id ?: return emptyList()
        val subGroupIds = getSubGroups(orgGroupId).map { it.id }

        var orgMembers: List<KeycloakUserDto> = emptyList()
        subGroupIds.forEach() { subGroupId ->
            val subGroupMembers = groupsApi().group(subGroupId).members().mapNotNull {
                keycloakUserMapper.buildKeycloakUserDto(it)
            }
            orgMembers = orgMembers.plus(subGroupMembers)
        }

        return orgMembers
    }

    fun getAuthorityAdmins(): List<KeycloakUserDto> {
        val authorityAdminGroupId =
            getOrganizationByOrganizationIdOrNull(ApplicationRole.AUTHORITY_ADMIN.kcGroupName)!!.id
        return groupsApi().group(authorityAdminGroupId).members().mapNotNull {
            keycloakUserMapper.buildKeycloakUserDto(it)
        }
    }

    fun getParticipantAdmins(organizationId: String): List<KeycloakUserDto> {
        require(organizationId.isNotBlank()) { "Organization ID must not be blank" }
        val orgGroupId = getOrganizationByOrganizationIdOrNull(organizationId)!!.id
        val subGroupNameToIdMap = getSubGroupNameToIdMap(orgGroupId)
        val participantAdminGroupId = subGroupNameToIdMap[OrganizationRole.PARTICIPANT_ADMIN.kcSubGroupName]

        return groupsApi().group(participantAdminGroupId).members().mapNotNull {
            keycloakUserMapper.buildKeycloakUserDto(it)
        }
    }

    fun createOrganization(organizationId: String): String {
        require(organizationId.isNotBlank()) { "Organization ID must not be blank" }
        val organization = GroupRepresentation().apply {
            name = organizationId
        }

        // Create organization group
        val organizationCreateResponse = groupsApi().add(organization)
        if (organizationCreateResponse.status != Response.Status.CREATED.statusCode) {
            throw WebApplicationException(
                "Request failed: ${organizationCreateResponse.statusInfo.reasonPhrase}",
                organizationCreateResponse.status
            )
        }

        // Create role-based subgroups
        val orgGroupId = getOrganizationByOrganizationIdOrNull(organization.name)!!.id

        OrganizationRole.entries.forEach {
            val subGroup = GroupRepresentation().apply {
                name = it.kcSubGroupName
            }
            groupsApi().group(orgGroupId).subGroup(subGroup)
        }

        // Add roles to subgroups
        val subGroupNameToIdMap = getSubGroupNameToIdMap(orgGroupId)
        val roles: Map<String, RoleRepresentation> = rolesApi().list().associateBy { it.name }

        OrganizationRole.entries.forEach {
            val subGroupId = subGroupNameToIdMap[it.kcSubGroupName]!!
            val role = roles[it.userRole]!!

            groupsApi().group(subGroupId)
                .roles().realmLevel().add(listOf(role))
        }

        return orgGroupId
    }

    fun deleteOrganization(organizationId: String) {
        require(organizationId.isNotBlank()) { "Organization ID must not be blank" }
        getOrganizationByOrganizationIdOrNull(organizationId)?.let {
            groupsApi().group(it.id).remove()
        }
    }

    /**
     * Join the user to the organization.
     * Can also be used to change the user's role in the organization.
     *
     * @param userId The user's ID
     * @param organizationId The organization's ID
     * @param role The user's role in the organization
     */
    fun joinOrganization(userId: String, organizationId: String, role: OrganizationRole) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        require(organizationId.isNotBlank()) { "Organization ID must not be blank" }
        val user = usersApi().get(userId)
        val orgGroupId = getOrganizationByOrganizationIdOrNull(organizationId)!!.id
        val subGroupNameToIdMap = getSubGroupNameToIdMap(orgGroupId)

        OrganizationRole.entries.forEach {
            val subGroupId = subGroupNameToIdMap[it.kcSubGroupName]!!

            if (it == role) {
                user.joinGroup(subGroupId)
            } else {
                user.leaveGroup(subGroupId)
            }
        }
    }

    /**
     * Set the application roles of a user.
     * Can also be used to change the user's roles in the authority or application.
     *
     * @param userId The user's ID
     * @param roles The user's roles in the authority
     */
    fun setApplicationRoles(userId: String, roles: List<ApplicationRole>) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val user = usersApi().get(userId)

        ApplicationRole.entries.forEach {
            val roleGroupId = getOrganizationByOrganizationIdOrNull(it.kcGroupName)!!.id

            if (roles.contains(it)) {
                user.joinGroup(roleGroupId)
            } else {
                user.leaveGroup(roleGroupId)
            }
        }
    }

    fun clearApplicationRoles(userId: String) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val user = usersApi().get(userId)

        ApplicationRole.entries.forEach {
            val roleGroupId = getOrganizationByOrganizationIdOrNull(it.kcGroupName)!!.id

            user.leaveGroup(roleGroupId)
        }
    }

    fun forceLogout(userId: String) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        usersApi().get(userId).logout()
        Log.info("Logging out user forcefully. userId: $userId")
    }

    fun sendInvitationEmail(userId: String) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val actions = listOf(
            RequiredAction.CONFIGURE_TOTP.stringRepresentation,
            RequiredAction.VERIFY_EMAIL.stringRepresentation
        )
        // The overloads of executeActionsEmail with clientId and redirectUri return 400 for some reason
        usersApi().get(userId).executeActionsEmail(actions)
    }

    fun sendInvitationEmailWithPasswordReset(userId: String) {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val actions = listOf(
            RequiredAction.UPDATE_PASSWORD.stringRepresentation,
            RequiredAction.CONFIGURE_TOTP.stringRepresentation,
            RequiredAction.VERIFY_EMAIL.stringRepresentation
        )
        // The overloads of executeActionsEmail with clientId and redirectUri return 400 for some reason
        usersApi().get(userId).executeActionsEmail(actions)
    }

    internal fun getOrganizationByOrganizationIdOrNull(organizationId: String): GroupRepresentation? {
        require(organizationId.isNotBlank()) { "Organization ID must not be blank" }
        return getOrganizations().find { it.name == organizationId }
    }

    internal fun getOrganizations(): List<GroupRepresentation> =
        groupsApi().groups()

    private fun getSubGroupNameToIdMap(orgGroupId: String): Map<String, String> {
        require(orgGroupId.isNotBlank()) { "Group ID must not be blank" }
        return getSubGroups(orgGroupId).associate { it.name to it.id }
    }

    internal fun getSubGroups(orgGroupId: String): List<GroupRepresentation> {
        require(orgGroupId.isNotBlank()) { "Group ID must not be blank" }
        return groupsApi()
        .group(orgGroupId)
        .getSubGroups(0, 1_000_000, true)
        ?: error("Failed to find group with ID: $orgGroupId")
    }
}
