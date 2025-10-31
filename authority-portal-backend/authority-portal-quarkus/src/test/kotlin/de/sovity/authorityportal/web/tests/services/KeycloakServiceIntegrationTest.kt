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

package de.sovity.authorityportal.web.tests.services

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetupTest
import de.sovity.authorityportal.seeds.DevScenario
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.thirdparty.keycloak.model.ApplicationRole
import de.sovity.authorityportal.web.thirdparty.keycloak.model.KeycloakUserDto
import de.sovity.authorityportal.web.thirdparty.keycloak.model.OrganizationRole
import de.sovity.authorityportal.web.thirdparty.keycloak.model.RequiredAction
import io.quarkus.logging.Log
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import jakarta.inject.Inject
import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.WebApplicationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.awaitility.Awaitility.await
import org.eclipse.microprofile.context.ManagedExecutor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.TimeUnit.SECONDS

/**
 * This test is slow because it also starts a Keycloak!
 */
@QuarkusTest
@TestProfile(KeycloakServiceIntegrationTest.KeycloakTestProfile::class)
class KeycloakServiceIntegrationTest {

    @Inject
    lateinit var keycloakService: KeycloakService

    @Inject
    lateinit var managedExecutor: ManagedExecutor

    private lateinit var greenMail: GreenMail

    @BeforeEach
    fun setup() {
        awaitKeycloakReady()
        clearNonDefaultKeycloakGroups()
        clearNonDefaultKeycloakUsers()
        greenMail = GreenMail(ServerSetupTest.SMTP)
        greenMail.start()
    }

    @AfterEach
    fun stopMailServer() {
        greenMail.stop()
    }

    /* createUser */
    @Test
    fun `given createUser, when user created with password, then user created and email verification required`() {
        // act
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )

        // assert
        assertThat(userId).isNotBlank()
        val actualUserIdByEmail = keycloakService.getUserIdByEmailOrNull("a@a.de")
        assertThat(actualUserIdByEmail).isEqualTo(userId)

        val actual: UserRepresentation = getUserByEmailOrNull("a@a.de") ?: error("User not found")
        assertThat(actual.id).isEqualTo(userId)
        assertThat(actual.email).isEqualTo("a@a.de")
        assertThat(actual.firstName).isEqualTo("A")
        assertThat(actual.lastName).isEqualTo("B")
        assertThat(actual.isEnabled).isTrue()
        assertThat(actual.requiredActions).containsExactlyInAnyOrder(
            RequiredAction.CONFIGURE_TOTP.stringRepresentation,
            RequiredAction.VERIFY_EMAIL.stringRepresentation
        )
        assertThat(actual.credentials).isNull()
    }

    @Test
    fun `given createUser, when user created without password, then user created and email verification and password update required`() {
        // act
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B"
        )

        // assert
        assertThat(userId).isNotBlank()
        val actualUserIdByEmail = keycloakService.getUserIdByEmailOrNull("a@a.de")
        assertThat(actualUserIdByEmail).isEqualTo(userId)

        val actual: UserRepresentation = getUserByEmailOrNull("a@a.de") ?: error("User not found")
        assertThat(actual.id).isEqualTo(userId)
        assertThat(actual.email).isEqualTo("a@a.de")
        assertThat(actual.firstName).isEqualTo("A")
        assertThat(actual.lastName).isEqualTo("B")
        assertThat(actual.isEnabled).isTrue()
        assertThat(actual.requiredActions).containsExactlyInAnyOrder(
            RequiredAction.UPDATE_PASSWORD.stringRepresentation,
            RequiredAction.CONFIGURE_TOTP.stringRepresentation,
            RequiredAction.VERIFY_EMAIL.stringRepresentation
        )
        assertThat(actual.credentials).isNull()
    }

    @Test
    fun `given createUser, when user with email exists, then exception thrown`() {
        // act
        keycloakService.createUser(
            email = "a@a.de",
            firstName = "A1",
            lastName = "B1",
            password = "C1"
        )

        // assert
        assertThatThrownBy {
            keycloakService.createUser(
                email = "a@a.de",
                firstName = "A2",
                lastName = "B2",
                password = "C2"
            )
        }
            .hasMessage("User already exists")
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(409)
    }

    @Test
    fun `given createUser, when empty email, then exception thrown`() {
        // arrange
        assertThat(getNonDefaultUsers()).isEmpty()

        // assert
        assertThatThrownBy {
            keycloakService.createUser(
                email = "",
                firstName = "A",
                lastName = "B",
                password = "C"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Email must not be blank")
        assertThat(getNonDefaultUsers()).isEmpty()
    }



    /* createKeycloakUserAndOrganization */
    @Test
    @TestTransaction
    fun `given createKeycloakUserAndOrganization, when using regular credentials, then organization and user created and user member of organization`() {
        // act
        val userId = keycloakService.createKeycloakUserAndOrganization(
            organizationId = "organization-id",
            userEmail = "a@a.de",
            userFirstName = "A",
            userLastName = "B",
            userOrganizationRole = OrganizationRole.PARTICIPANT_CURATOR,
            userPassword = "C"
        )

        // assert
        val actualOrganization = getOrganizationWithSubGroupsByOrganizationIdOrNull("organization-id")
            ?: error("Organization not found")
        assertThat(actualOrganization.name).isEqualTo("organization-id")

        val actualUser = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actualUser.email).isEqualTo("a@a.de")
        assertThat(actualUser.firstName).isEqualTo("A")
        assertThat(actualUser.lastName).isEqualTo("B")
        assertThat(actualUser.credentials).isNull()

        val userRoles = keycloakService.getUserRoles(userId)
        assertThat(userRoles).containsExactlyInAnyOrder(
            OrganizationRole.PARTICIPANT_USER.userRole,
            OrganizationRole.PARTICIPANT_CURATOR.userRole
        )

        val organizationMembers = keycloakService.getOrganizationMembers("organization-id")
        assertThat(organizationMembers).containsOnly(
            KeycloakUserDto(
                userId = userId,
                firstName = "A",
                lastName = "B",
                email = "a@a.de"
            )
        )
    }

    @Test
    @TestTransaction
    fun `given createKeycloakUserAndOrganization, when empty organization id, then exception thrown and user and organization not created`() {
        // assert
        assertThatThrownBy {
            keycloakService.createKeycloakUserAndOrganization(
                organizationId = "",
                userEmail = "a@a.de",
                userFirstName = "A",
                userLastName = "B",
                userOrganizationRole = OrganizationRole.PARTICIPANT_CURATOR,
                userPassword = "C"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Organization ID must not be blank")
        assertThat(getNonDefaultUsers()).isEmpty()
        assertThat(getNonDefaultOrganizations()).isEmpty()
    }

    @Test
    @TestTransaction
    fun `given createKeycloakUserAndOrganization, when empty email, then exception thrown and user and organization not created`() {
        // assert
        assertThatThrownBy {
            keycloakService.createKeycloakUserAndOrganization(
                organizationId = "organization-id",
                userEmail = "",
                userFirstName = "A",
                userLastName = "B",
                userOrganizationRole = OrganizationRole.PARTICIPANT_CURATOR,
                userPassword = "C"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User email must not be blank")
        assertThat(getNonDefaultUsers()).isEmpty()
        assertThat(getNonDefaultOrganizations()).isEmpty()
    }



    /* getUserIdByEmailOrNull */
    @Test
    fun `given getUserIdByEmailOrNull, when getting user, then return user id`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )

        // act
        val actualUserId = keycloakService.getUserIdByEmailOrNull("a@a.de")

        // assert
        assertThat(actualUserId).isEqualTo(userId)
    }

    @Test
    fun `given getUserIdByEmailOrNull, when no user, then return null`() {
        // act
        val actualUserId = keycloakService.getUserIdByEmailOrNull("a@a.de")

        // assert
        assertThat(actualUserId).isNull()
    }



    /* deleteUserSafely */
    @Test
    fun `given deleteUserSafely, when user exists, then user deleted`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        assertThat(getUserByIdOrNull(userId)).isNotNull

        // act
        keycloakService.deleteUserSafely(userId)

        // assert
        assertThat(getUserByIdOrNull(userId)).isNull()
    }

    @Test
    fun `given deleteUserSafely, when no user, then no exception thrown`() {
        // arrange
        assertThat(getNonDefaultUsers())
        assertThat(getUserByIdOrNull("non-existent-user-id")).isNull()

        // act
        keycloakService.deleteUserSafely("non-existent-user-id")

        // assert
        // no exception thrown
    }



    /* deleteUsersSafely */
    @Test
    fun `given deleteUsersSafely, when users exist, then users deleted`() {
        // arrange
        val userId1 = keycloakService.createUser(
            email = "a1@a.de",
            firstName = "A1",
            lastName = "B1",
            password = "C1"
        )
        val userId2 = keycloakService.createUser(
            email = "a2@a.de",
            firstName = "A2",
            lastName = "B2",
            password = "C2"
        )
        assertThat(getUserByIdOrNull(userId1)).isNotNull
        assertThat(getUserByIdOrNull(userId2)).isNotNull

        // act
        keycloakService.deleteUsersSafely(listOf(userId1))

        // assert
        assertThat(getUserByIdOrNull(userId1)).isNull()
        assertThat(getUserByIdOrNull(userId2)).isNotNull
    }

    @Test
    fun `given deleteUsersSafely, when no user, then no exception thrown`() {
        // arrange
        assertThat(getUserByIdOrNull("non-existent-user-id")).isNull()

        // act
        keycloakService.deleteUsersSafely(listOf("non-existent-user-id"))

        // assert
        // No exception should be thrown
    }



    /* deactivateUser */
    @Test
    fun `given deactivateUser, when user activated, then user deactivated`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        var actualUser = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actualUser.isEnabled).isTrue()

        // act
        keycloakService.deactivateUser(userId)

        // assert
        actualUser = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actualUser.isEnabled).isFalse()
    }

    @Test
    fun `given deactivateUser, when user deactivated, then user deactivated`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        keycloakService.deactivateUser(userId)
        var actualUser = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actualUser.isEnabled).isFalse()

        // act
        keycloakService.deactivateUser(userId)

        // assert
        actualUser = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actualUser.isEnabled).isFalse()
    }

    @Test
    fun `given deactivateUser, when no user, then exception thrown`() {
        // act
        assertThatThrownBy {
            keycloakService.deactivateUser("user-id-not-exists")
        }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }



    /* reactivateUser */
    @Test
    fun `given reactivateUser, when user deactivated, then user activated`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        keycloakService.deactivateUser(userId)
        var actualUser = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actualUser.isEnabled).isFalse()

        // act
        keycloakService.reactivateUser(userId)

        // assert
        actualUser = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actualUser.isEnabled).isTrue()
    }

    @Test
    fun `given reactivateUser, when user activated, then user activated`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        var actualUser = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actualUser.isEnabled).isTrue()

        // act
        keycloakService.reactivateUser(userId)

        // assert
        actualUser = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actualUser.isEnabled).isTrue()
    }

    @Test
    fun `given reactivateUser, when no user, then exception thrown`() {
        // act
        assertThatThrownBy {
            keycloakService.reactivateUser("user-id-not-exists")
        }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }


    /* updateUser */
    @Test
    @TestTransaction
    fun `given updateUser, when updating email as new user, then email updated and verification required`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )

        // act
        keycloakService.updateUser(
            userId = userId,
            firstName = "A",
            lastName = "B",
            email = "updated@a.de"
        )

        // assert
        val actual = getUserByEmailOrNull("updated@a.de") ?: error("User not found")
        assertThat(actual.id).isEqualTo(userId)
        assertThat(actual.email).isEqualTo("updated@a.de")
        assertThat(actual.firstName).isEqualTo("A")
        assertThat(actual.lastName).isEqualTo("B")
        assertThat(actual.isEnabled).isTrue()
        assertThat(actual.isEmailVerified).isFalse()
        assertThat(actual.requiredActions).containsExactlyInAnyOrder(
            RequiredAction.CONFIGURE_TOTP.stringRepresentation,
            RequiredAction.VERIFY_EMAIL.stringRepresentation
        )
        assertThat(actual.credentials).isNull()
    }

    @Test
    @TestTransaction
    fun `given updateUser, when updating email, email is verified and no required actions, then email updated and verification required`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        removeRequiredActionsForUser(userId)
        setIsEmailVerifiedForUser(userId, true)

        // act
        keycloakService.updateUser(
            userId = userId,
            firstName = "A",
            lastName = "B",
            email = "updated@a.de"
        )

        // assert
        val actual = getUserByEmailOrNull("updated@a.de") ?: error("User not found")
        assertThat(actual.id).isEqualTo(userId)
        assertThat(actual.email).isEqualTo("updated@a.de")
        assertThat(actual.isEmailVerified).isFalse()
        assertThat(actual.requiredActions).containsExactly(RequiredAction.VERIFY_EMAIL.stringRepresentation)
    }

    @Test
    @TestTransaction
    fun `given updateUser, when updating first last name and not email and email verification required, then first and last name updated and same email`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        removeRequiredActionsForUser(userId)
        var actual = getUserByEmailOrNull("a@a.de") ?: error("User not found")
        assertThat(actual.requiredActions).isEmpty()

        // act
        keycloakService.updateUser(
            userId = userId,
            firstName = "updated first name",
            lastName = "updated last name",
            email = null
        )

        // assert
        actual = getUserByEmailOrNull("a@a.de") ?: error("User not found")
        assertThat(actual.id).isEqualTo(userId)
        assertThat(actual.firstName).isEqualTo("updated first name")
        assertThat(actual.lastName).isEqualTo("updated last name")
        assertThat(actual.requiredActions).isEmpty()
    }

    // regression test against trimming happening too late and unnecessary verifying of e-mail addresses being sent
    @Test
    @TestTransaction
    fun `given updateUser, when updating email with whitespace and no email verification required, then email not updated and no email verification required`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B"
        )
        removeRequiredActionsForUser(userId)
        setIsEmailVerifiedForUser(userId, true)

        // act
        keycloakService.updateUser(
            userId = userId,
            firstName = "A",
            lastName = "B",
            email = "a@a.de "
        )

        // assert
        val actual = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actual.id).isEqualTo(userId)
        assertThat(actual.requiredActions).isEmpty()
        assertThat(actual.isEmailVerified).isTrue()
    }

    @Test
    @TestTransaction
    fun `given updateUser, when no email verification required and email is null, then no verification required and email not updated`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B"
        )
        removeRequiredActionsForUser(userId)
        setIsEmailVerifiedForUser(userId, true)

        // act
        keycloakService.updateUser(
            userId = userId,
            firstName = "A",
            lastName = "B",
            email = null
        )

        // assert
        val actual = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(actual.id).isEqualTo(userId)
        assertThat(actual.requiredActions).isEmpty()
        assertThat(actual.isEmailVerified).isTrue()
    }

    @Test
    @TestTransaction
    fun `given updateUser, when empty email, then exception thrown`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B"
        )

        // act
        assertThatThrownBy {
            keycloakService.updateUser(
                userId = userId,
                email = "",
                firstName = "updated first name",
                lastName = "updated last name"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Email must be null or not blank")
    }

    @Test
    @TestTransaction
    fun `given updateUser, when no user, then exception thrown`() {
        // assert
        assertThatThrownBy {
            keycloakService.updateUser(
                userId = "non-existent-user-id",
                email = "updated@a.de",
                firstName = "updated first name",
                lastName = "updated last name"
            )
        }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }

    @Test
    @TestTransaction
    fun `given updateUser, when empty user id, then exception thrown`() {
        // assert
        assertThatThrownBy {
            keycloakService.updateUser(
                userId = "",
                email = "updated@a.de",
                firstName = "updated first name",
                lastName = "updated last name"
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User ID must not be blank")
    }



    /* getOrganizationMembers */
    @Test
    @TestTransaction
    fun `given getOrganizationMembers, when user member of organization, then return user`() {
        // arrange
        val userId = keycloakService.createKeycloakUserAndOrganization(
            organizationId = "organization-id",
            userEmail = "a@a.de",
            userFirstName = "A",
            userLastName = "B",
            userOrganizationRole = OrganizationRole.PARTICIPANT_CURATOR,
            userPassword = "C"
        )

        // act
        val actual = keycloakService.getOrganizationMembers("organization-id")

        // assert
        assertThat(actual).isEqualTo(listOf(
            KeycloakUserDto(
                userId = userId,
                firstName = "A",
                lastName = "B",
                email = "a@a.de"
            )
        ))
    }

    @Test
    @TestTransaction
    fun `given getOrganizationMembers, when no organization members, then return empty list`() {
        // arrange
        keycloakService.createOrganization(organizationId = "organization-id")

        // act
        val actual = keycloakService.getOrganizationMembers("organization-id")

        // assert
        assertThat(actual).isEmpty()
    }

    @Test
    @TestTransaction
    fun `given getOrganizationMembers, when empty organization id, then return empty list`() {
        // act / assert
        assertThatThrownBy{
            keycloakService.getOrganizationMembers("")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Organization ID must not be blank")
    }

    @Test
    @TestTransaction
    fun `given getOrganizationMembers, when no organization, then return empty list`() {
        // act
        val actual = keycloakService.getOrganizationMembers("non-existent-organization-id")

        // assert
        assertThat(actual).isEmpty()
    }



    /* createOrganization */
    @Test
    fun `given createOrganization, when organization id ok, then organization created, check with getOrganizationByOrgGroupId`() {
        // act
        val actualOrgGroupId = keycloakService.createOrganization("organization-id")

        // assert
        val actualOrganization = getOrganizationWithSubGroupsByGroupIdOrNull(actualOrgGroupId)
            ?: error("Organization not found")
        assertThat(actualOrganization.id).isEqualTo(actualOrgGroupId)
        assertThat(actualOrganization.name).isEqualTo("organization-id")
        assertThat(actualOrganization.subGroups.map { it.name }).containsExactlyInAnyOrder(
            "Participant Admin",
            "Participant Curator",
            "Participant User"
        )
    }

    @Test
    fun `given createOrganization, when organization id ok, then organization created, check with getOrganizationByOrgId`() {
        // act
        keycloakService.createOrganization("organization-id")

        // assert
        val actualOrganization = getOrganizationWithSubGroupsByOrganizationIdOrNull("organization-id")
            ?: error("Organization not found")
        assertThat(actualOrganization.name).isEqualTo("organization-id")
        assertThat(actualOrganization.subGroups.map { it.name }).containsExactlyInAnyOrder(
            "Participant Admin",
            "Participant Curator",
            "Participant User"
        )
    }

    @Test
    fun `given createOrganization, when organization already exists, then throw error`() {
        // arrange
        val orgGroupId = keycloakService.createOrganization("organization-id")
        assertThat(getOrganizationWithSubGroupsByGroupIdOrNull(orgGroupId)).isNotNull

        // act / assert
        assertThatThrownBy {
            keycloakService.createOrganization("organization-id")
        }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(409)
    }

    @Test
    fun `given createOrganization, when organization id empty, then throw error`() {
        // arrange
        assertThat(keycloakService.getOrganizations()).hasSize(9)

        // act / assert
        assertThatThrownBy {
            keycloakService.createOrganization("")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Organization ID must not be blank")
        assertThat(keycloakService.getOrganizations()).hasSize(9)
    }



    /* deleteOrganization */
    @Test
    fun `given deleteOrganization, when organization exists, then delete organization`() {
        // arrange
        val actualOrgGroupId = keycloakService.createOrganization("organization-id")
        assertThat(getOrganizationWithSubGroupsByGroupIdOrNull(actualOrgGroupId)).isNotNull

        // act
        keycloakService.deleteOrganization("organization-id")

        // assert
        assertThat(getOrganizationWithSubGroupsByGroupIdOrNull(actualOrgGroupId)).isNull()
    }

    @Test
    fun `given deleteOrganization, when organization already deleted, then no exception thrown`() {
        // arrange
        assertThat(getOrganizationWithSubGroupsByOrganizationIdOrNull("non-existent-organization-id")).isNull()

        // act
        keycloakService.deleteOrganization("non-existent-organization-id")

        // assert
        assertThat(getOrganizationWithSubGroupsByOrganizationIdOrNull("non-existent-organization-id")).isNull()
        // no exception thrown
    }

    @Test
    fun `given deleteOrganization, when organization id empty, then exception thrown and no organization deleted`() {
        // arrange
        assertThat(keycloakService.getOrganizations()).hasSize(9)

        // act / assert
        assertThatThrownBy{
            keycloakService.deleteOrganization("")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Organization ID must not be blank")
        assertThat(keycloakService.getOrganizations()).hasSize(9)
    }

    @Test
    fun `given deleteOrganization, when no organization, then no exception thrown and no organization deleted`() {
        // arrange
        assertThat(keycloakService.getOrganizations()).hasSize(9)
        assertThat(keycloakService.getOrganizationByOrganizationIdOrNull("non-existent-organization-id")).isNull()

        // act
        keycloakService.deleteOrganization("non-existent-organization-id")

        // assert
        assertThat(keycloakService.getOrganizations()).hasSize(9)
    }



    /* joinOrganization */
    @Test
    fun `given joinOrganization, when organization and user exist, then user joined organization`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        keycloakService.createOrganization("organization-id")
        var userRoles = keycloakService.getUserRoles(userId)
        assertThat(userRoles).isEmpty()

        // act
        keycloakService.joinOrganization(
            userId,
            "organization-id",
            role = OrganizationRole.PARTICIPANT_CURATOR
        )

        // assert
        val memberUserIds = keycloakService.getOrganizationMembers("organization-id")
            .map { it.userId }.toSet()
        assertThat(memberUserIds).containsExactly(userId)

        userRoles = keycloakService.getUserRoles(userId)
        assertThat(userRoles).containsExactlyInAnyOrder(
            OrganizationRole.PARTICIPANT_USER.userRole,
            OrganizationRole.PARTICIPANT_CURATOR.userRole
        )
    }

    @Test
    fun `given joinOrganization, when user id empty, then exception thrown`() {
        // arrange
        keycloakService.createOrganization("organization-id")

        // act
        assertThatThrownBy {
            keycloakService.joinOrganization(
                "",
                "organization-id",
                role = OrganizationRole.PARTICIPANT_CURATOR
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User ID must not be blank")
    }

    @Test
    fun `given joinOrganization, when no user, then exception thrown`() {
            // arrange
            keycloakService.createOrganization("organization-id")

            // act
            assertThatThrownBy {
                keycloakService.joinOrganization(
                    "non-existent-user-id",
                    "organization-id",
                    role = OrganizationRole.PARTICIPANT_CURATOR
                )
            }
                .isInstanceOf(WebApplicationException::class.java)
                .extracting { (it as WebApplicationException).response.status }
                .isEqualTo(404)
        }

    @Test
    fun `given joinOrganization, when organization id empty, then exception thrown`() {
        // arrange
        val userId = dummyUuidString(DevScenario.Users.PARTICIPANT_USER)

        // act / assert
        assertThatThrownBy {
            keycloakService.joinOrganization(
                userId,
                "",
                role = OrganizationRole.PARTICIPANT_CURATOR
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Organization ID must not be blank")
    }

    @Test
    fun `given joinOrganization, when no organization, then exception thrown`() {
        // arrange
        val userId = dummyUuidString(DevScenario.Users.PARTICIPANT_USER)

        // act / assert
        assertThatThrownBy {
            keycloakService.joinOrganization(
                userId,
                "non-existent-organization-id",
                role = OrganizationRole.PARTICIPANT_CURATOR
            )
        }
            .isInstanceOf(NullPointerException::class.java)
    }



    /* getAuthorityAdmins */
    @Test
    fun `given getAuthorityAdmins, when standard realm json loaded, then return authority admin`() {
        // act
        val actual = keycloakService.getAuthorityAdmins()

        // assert
        assertThat(actual).hasSize(1)
        assertThat(actual.first().userId).isEqualTo("00000000-0000-0000-0000-000000000001")
    }



    /* getParticipantAdmins */
    @Test
    fun `given getParticipantAdmins, when participant admin exists, then return participant admin`() {
        // arrange
        val userId = keycloakService.createKeycloakUserAndOrganization(
            organizationId = "organization-id",
            userEmail = "a@a.de",
            userFirstName = "A",
            userLastName = "B",
            userOrganizationRole = OrganizationRole.PARTICIPANT_ADMIN,
            userPassword = "C"
        )

        // act
        val actual = keycloakService.getParticipantAdmins("organization-id")

        // assert
        assertThat(actual).hasSize(1)
        assertThat(actual.first().userId).isEqualTo(userId)
    }

    @Test
    fun `given getParticipantAdmins, when no participant admin exists, then return empty list`() {
        // arrange
        keycloakService.createOrganization(organizationId = "organization-id")

        // act
        val actual = keycloakService.getParticipantAdmins("organization-id")

        // assert
        assertThat(actual).isEmpty()
    }

    @Test
    fun `given getParticipantAdmins, when organization id empty, then exception thrown`() {
        // act / assert
        assertThatThrownBy {
            keycloakService.getParticipantAdmins("")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Organization ID must not be blank")
    }

    @Test
    fun `given getParticipantAdmins, when no organization, then exception thrown`() {
        // act / assert
        assertThatThrownBy {
            keycloakService.getParticipantAdmins("non-existent-organization-id")
        }
            .isInstanceOf(NullPointerException::class.java)
    }



    /* setApplicationRoles */
    @Test
    @TestTransaction
    fun `given setApplicationRoles, when user exists, then user is application admin`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        var userGroups = keycloakService.usersApi().get(userId).groups()
        assertThat(userGroups).isEmpty()

        // act
        keycloakService.setApplicationRoles(userId, listOf(ApplicationRole.AUTHORITY_ADMIN))

        // assert
        userGroups = keycloakService.usersApi().get(userId).groups()
        assertThat(userGroups).hasSize(1)
        assertThat(userGroups.first().name).isEqualTo("ROLE_AUTHORITY_ADMIN")
    }

    @Test
    @TestTransaction
    fun `given setApplicationRoles, when user exists and role list empty, then user has no roles`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        var userGroups = keycloakService.usersApi().get(userId).groups()
        assertThat(userGroups).isEmpty()

        // act
        keycloakService.setApplicationRoles(userId, emptyList())

        // assert
        userGroups = keycloakService.usersApi().get(userId).groups()
        assertThat(userGroups).isEmpty()
    }

    @Test
    @TestTransaction
    fun `given setApplicationRoles, when user exists and already has application role, then user is application admin and no exception thrown`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        var userGroups = keycloakService.usersApi().get(userId).groups()
        assertThat(userGroups).isEmpty()

        // act
        keycloakService.setApplicationRoles(userId, listOf(ApplicationRole.AUTHORITY_ADMIN))
        keycloakService.setApplicationRoles(userId, listOf(ApplicationRole.AUTHORITY_ADMIN))

        // assert
        userGroups = keycloakService.usersApi().get(userId).groups()
        assertThat(userGroups).hasSize(1)
        assertThat(userGroups.first().name).isEqualTo("ROLE_AUTHORITY_ADMIN")
    }

    @Test
    @TestTransaction
    fun `given setApplicationRoles, when user id empty, then exception thrown`() {
        // act / assert
        assertThatThrownBy {
            keycloakService.setApplicationRoles(
                "",
                listOf(ApplicationRole.AUTHORITY_ADMIN)
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User ID must not be blank")
    }

    @Test
    @TestTransaction
    fun `given setApplicationRoles, when no user, then exception thrown`() {
        // act / assert
        assertThatThrownBy {
            keycloakService.setApplicationRoles("non-existent-user-id", listOf(ApplicationRole.AUTHORITY_ADMIN))
        }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }



    /* clearApplicationRoles */
    @Test
    @TestTransaction
    fun `given clearApplicationRoles, when user exists with application role, then user has no application role`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        keycloakService.setApplicationRoles(userId, listOf(ApplicationRole.AUTHORITY_ADMIN))
        var userGroups = keycloakService.usersApi().get(userId).groups()
        assertThat(userGroups).hasSize(1)
        assertThat(userGroups.first().name).isEqualTo("ROLE_AUTHORITY_ADMIN")

        // act
        keycloakService.clearApplicationRoles(userId)

        // assert
        userGroups = keycloakService.usersApi().get(userId).groups()
        assertThat(userGroups).isEmpty()
    }

    @Test
    @TestTransaction
    fun `given clearApplicationRoles, when user id empty, then exception thrown`() {
        // act / assert
        assertThatThrownBy {
            keycloakService.clearApplicationRoles("")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User ID must not be blank")
    }

    @Test
    @TestTransaction
    fun `given clearApplicationRoles, when no user, then exception thrown`() {
        // act / assert
        assertThatThrownBy {
            keycloakService.clearApplicationRoles("non-existent-user-id")
        }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }



    /* forceLogout */
    @Test
    fun `given forceLogout, when user session exists, then user session deleted`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        removeRequiredActionsForUser(userId)

        var userSessions = keycloakService.usersApi().get(userId).userSessions
        assertThat(userSessions).isEmpty()

        val client: HttpClient = HttpClient.newHttpClient()
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9091/realms/authority-portal/protocol/openid-connect/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    "client_id=authority-portal-client" +
                        "&username=a@a.de" +
                        "&password=C" +
                        "&grant_type=password" +
                        "&client_secret=NKV91vM0KfWeXzaNGaH6fF2z4o01tugl"
                )
            )
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        assertThat(response.statusCode()).isEqualTo(200)

        userSessions = keycloakService.usersApi().get(userId).userSessions
        assertThat(userSessions).hasSize(1)

        // act
        keycloakService.forceLogout(userId)

        // assert
        userSessions = keycloakService.usersApi().get(userId).userSessions
        assertThat(userSessions).isEmpty()
    }

    @Test
    fun `given forceLogout, when no user session exists, then no exception thrown`() {
        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )
        var userSessions = keycloakService.usersApi().get(userId).userSessions
        assertThat(userSessions).isEmpty()

        // act
        keycloakService.forceLogout(userId)

        // assert
        userSessions = keycloakService.usersApi().get(userId).userSessions
        assertThat(userSessions).isEmpty()
    }

    @Test
    fun `given forceLogout, when user id empty, then exception thrown`() {
        // act / assert
        assertThatThrownBy {
            keycloakService.forceLogout("")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User ID must not be blank")
    }

    @Test
    fun `given forceLogout, when no user, then exception thrown`() {
        // act / assert
        assertThatThrownBy {
            keycloakService.forceLogout("non-existent-user-id")
        }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }



    /* sendInvitationEmail */
    @Test
    fun `given sendInvitationEmail, when user exists, then invitation email sent`() {
        assumeNotRunningOnLinux()

        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )

        // act
        keycloakService.sendInvitationEmail(userId)

        // assert
        val receivedMessages = greenMail.receivedMessages
        assertThat(receivedMessages).hasSize(1)
        assertThat(receivedMessages.first().subject).isEqualTo("Update Your Account")
    }

    @Test
    fun `given sendInvitationEmail, when user id empty, then exception thrown`() {
        assumeNotRunningOnLinux()

        // act / assert
        assertThatThrownBy {
            keycloakService.sendInvitationEmail("")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User ID must not be blank")
    }

    @Test
    fun `given sendInvitationEmail, when no user, then exception thrown`() {
        assumeNotRunningOnLinux()

        // act / assert
        assertThatThrownBy {
            keycloakService.sendInvitationEmail("non-existent-user-id")
        }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }



    /* sendInvitationEmailWithPasswordReset */
    @Test
    fun `given sendInvitationEmailWithPasswordReset, when user exists, then account update email sent`() {
        assumeNotRunningOnLinux()

        // arrange
        val userId = keycloakService.createUser(
            email = "a@a.de",
            firstName = "A",
            lastName = "B",
            password = "C"
        )

        // act
        keycloakService.sendInvitationEmailWithPasswordReset(userId)

        // assert
        val receivedMessages = greenMail.receivedMessages
        assertThat(receivedMessages).hasSize(1)
        assertThat(receivedMessages.first().subject).isEqualTo("Update Your Account")
    }

    @Test
    fun `given sendInvitationEmailWithPasswordReset, when user id empty, then exception thrown`() {
        assumeNotRunningOnLinux()

        // act / assert
        assertThatThrownBy {
            keycloakService.sendInvitationEmailWithPasswordReset("")
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User ID must not be blank")
    }

    @Test
    fun `given sendInvitationEmailWithPasswordReset, when no user, then exception thrown`() {
        assumeNotRunningOnLinux()

        // act / assert
        assertThatThrownBy {
            keycloakService.sendInvitationEmailWithPasswordReset("non-existent-user-id")
        }
            .isInstanceOf(WebApplicationException::class.java)
            .extracting { (it as WebApplicationException).response.status }
            .isEqualTo(404)
    }


    /* Helper functions */
    private fun clearNonDefaultKeycloakGroups() {
        val groups = getNonDefaultOrganizations().ifEmpty { return }

        Log.warn(
            "Clearing groups for the sake of the test. " +
                "Keycloak has groups: ${groups.map { it.name }}"
        )

        groups.forEach {
            keycloakService.deleteOrganization(it.name)
        }

        assertThat(getNonDefaultOrganizations()).isEmpty()
        assertThat(keycloakService.getOrganizations()).hasSize(9)
    }

    private fun getNonDefaultOrganizations(): List<GroupRepresentation> {
        return keycloakService.getOrganizations().filter {
            !it.name.startsWith("MDSL00000") && !it.name.startsWith("ROLE_")
        }
    }

    private fun clearNonDefaultKeycloakUsers() {
        val users =
            getNonDefaultUsers().ifEmpty {
                return
            }

        Log.warn(
            "Clearing non-default users for the sake of the test. " +
                "Keycloak has non-default users: ${users.map { it.email }}"
        )

        users.forEach { keycloakService.deleteUserSafely(it.id) }

        assertThat(getNonDefaultUsers()).isEmpty()
        assertThat(getUsers()).hasSize(8)
    }

    private fun getNonDefaultUsers(): List<UserRepresentation> {
        return getUsers().filter {
            !it.id.startsWith("00000000-0000-0000-0000-00000000000")
                // exclude service account user
                && it.serviceAccountClientId == null
        }
    }

    private fun getUsers(): List<UserRepresentation> =
        keycloakService.usersApi().list()

    private fun awaitKeycloakReady() {
        await()
            .pollExecutorService(managedExecutor)
            .atMost(30, SECONDS)
            .untilAsserted {
                try {
                    keycloakService.realmApi().toRepresentation()
                } catch (e: Exception) {
                    throw AssertionError("Keycloak not ready", e)
                }
            }
    }

    private fun removeRequiredActionsForUser(userId: String) {
        setRequiredActionsForUser(userId, emptyList())
    }

    private fun setRequiredActionsForUser(userId: String, requiredActions: List<String>) {
        val userAttributesToUpdate = UserRepresentation().also {
            it.requiredActions = requiredActions
        }
        keycloakService.usersApi().get(userId).update(userAttributesToUpdate)
        val user = getUserByIdOrNull(userId) ?: error("User not found")
        assertThat(user.requiredActions).containsExactlyInAnyOrderElementsOf(requiredActions)
    }

    private fun setIsEmailVerifiedForUser(userId: String, isEmailVerified: Boolean) {
        val userAttributesToUpdate = UserRepresentation().also {
            it.isEmailVerified = isEmailVerified
        }
        keycloakService.usersApi().get(userId).update(userAttributesToUpdate)

    }

    private fun getOrganizationWithSubGroupsByGroupIdOrNull(orgGroupId: String): GroupRepresentation? {
        return getOrganizationByGroupIdOrNull(orgGroupId)?.also {
            it.subGroups = keycloakService.getSubGroups(orgGroupId)
        }
    }

    private fun getOrganizationByGroupIdOrNull(orgGroupId: String): GroupRepresentation? {
        return try {
            keycloakService.groupsApi().group(orgGroupId).toRepresentation()
        } catch (e: Exception) {
            null
        }
    }

    private fun getOrganizationWithSubGroupsByOrganizationIdOrNull(organizationId: String): GroupRepresentation? =
        keycloakService.getOrganizationByOrganizationIdOrNull(organizationId)?.also {
            it.subGroups = keycloakService.getSubGroups(it.id)
        }

    /**
     * Our Dev User IDs also need to be UUIDs, as they are expected to exist in the keycloak.
     */
    private fun dummyUuidString(i: Int): String = "00000000-0000-0000-0000-${i.toString().padStart(12, '0')}"

    private fun getUserByIdOrNull(userId: String): UserRepresentation? {
        return try {
            keycloakService.usersApi().get(userId).toRepresentation()
        } catch (e: Exception) {
            null
        }
    }

    private fun getUserByEmailOrNull(email: String): UserRepresentation? = keycloakService.usersApi()
        .searchByEmail(email, true)
        .firstOrNull()

    private fun assumeNotRunningOnLinux() {
        val os = System.getProperty("os.name").lowercase()
        assumeTrue(!os.contains("linux")) {
            "Test skipped because it is running on Linux. On Linux the greenmail SMTP server is not set up properly."
        }
    }

    class KeycloakTestProfile : QuarkusTestProfile {
        override fun getConfigProfile() = "kcIntegrationTest"
    }
}
