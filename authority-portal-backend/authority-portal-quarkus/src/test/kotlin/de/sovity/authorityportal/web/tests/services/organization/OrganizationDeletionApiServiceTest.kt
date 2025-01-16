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
package de.sovity.authorityportal.web.tests.services.organization

import de.sovity.authorityportal.api.UiResource
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.seeds.utils.dummyDevOrganizationId
import de.sovity.authorityportal.seeds.utils.dummyDevUserUuid
import de.sovity.authorityportal.web.Roles
import de.sovity.authorityportal.web.tests.useDevUser
import de.sovity.authorityportal.web.thirdparty.daps.DapsClient
import de.sovity.authorityportal.web.thirdparty.daps.DapsClientService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.thirdparty.keycloak.model.KeycloakUserDto
import io.quarkus.test.InjectMock
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.NotAuthorizedException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class OrganizationDeletionApiServiceTest {

    @Inject
    lateinit var uiResource: UiResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Inject
    lateinit var dsl: DSLContext

    @InjectMock
    lateinit var keycloakService: KeycloakService

    @InjectMock
    lateinit var dapsClientService: DapsClientService

    @Test
    fun `checkOrganizationDeletion fails because user is not admin`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_USER))

        // act & assert
        assertThatThrownBy {
            uiResource.checkOrganizationDeletion(dummyDevOrganizationId(1))
        }.isInstanceOf(NotAuthorizedException::class.java)
    }

    @Test
    @TestTransaction
    fun `checkOrganizationDeletion fails because all remaining authority admins are part of it`() {
        // arrange
        val lastAuthorityAdmin = KeycloakUserDto(dummyDevUserUuid(0), "Authority", "Admin", "authority@test.sovity.io")

        useDevUser(0, 0)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            scenarioInstaller.install(this)
        }

        whenever(keycloakService.getAuthorityAdmins()).thenReturn(listOf(lastAuthorityAdmin))

        // act
        val result = uiResource.checkOrganizationDeletion(dummyDevOrganizationId(0))

        // assert
        assertThat(result).isNotNull
        assertThat(result.organizationId).isEqualTo(dummyDevOrganizationId(0))
        assertThat(result.canBeDeleted).isFalse()
    }

    @Test
    @TestTransaction
    fun checkOrganizationDeletion() {
        // arrange
        val lastAuthorityAdmin = KeycloakUserDto(dummyDevUserUuid(99), "Authority", "Admin", "authority@test.sovity.io")

        val dapsClient = mock<DapsClient>()

        useDevUser(0, 0)

        ScenarioData().apply {
            organization(0, 0)
            organization(1, 99)
            user(0, 0)
            user(99, 1)
            connector(0, 0, 0)
            component(0, 0, 0)
            scenarioInstaller.install(this)
        }

        whenever(keycloakService.getAuthorityAdmins()).thenReturn(listOf(lastAuthorityAdmin))
        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)

        // act
        val result = uiResource.checkOrganizationDeletion(dummyDevOrganizationId(0))

        // assert
        assertThat(result).isNotNull
        assertThat(result.organizationId).isEqualTo(dummyDevOrganizationId(0))
        assertThat(result.canBeDeleted).isTrue()
    }

    @Test
    fun `deleteOrganization fails because user is not admin`() {
        // arrange
        useDevUser(0, 0, setOf(Roles.UserRoles.PARTICIPANT_USER))

        // act & assert
        assertThatThrownBy {
            uiResource.deleteOrganization(dummyDevOrganizationId(1))
        }.isInstanceOf(NotAuthorizedException::class.java)
    }

    @Test
    @TestTransaction
    fun `deleteOrganization fails because all remaining authority admins are part of it`() {
        // arrange
        val lastAuthorityAdmin = KeycloakUserDto(dummyDevUserUuid(0), "Authority", "Admin", "authority@test.sovity.io")

        useDevUser(0, 0)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)
            scenarioInstaller.install(this)
        }

        whenever(keycloakService.getAuthorityAdmins()).thenReturn(listOf(lastAuthorityAdmin))

        // act & assert
        assertThatThrownBy {
            uiResource.deleteOrganization(dummyDevOrganizationId(0))
        }.isInstanceOf(IllegalStateException::class.java).message()
            .contains("Organization can not be deleted. The last Authority Admins are part of this organization.")
    }

    @Test
    @TestTransaction
    fun deleteOrganization() {
        // arrange
        val lastAuthorityAdmin = KeycloakUserDto(dummyDevUserUuid(99), "Authority", "Admin", "authority@test.sovity.io")

        val dapsClient = mock<DapsClient>()

        useDevUser(0, 0)

        ScenarioData().apply {
            organization(0, 0)
            organization(1, 99)
            user(0, 0)
            user(99, 1)
            connector(0, 0, 0)
            component(0, 0, 0)
            scenarioInstaller.install(this)
        }

        whenever(keycloakService.getAuthorityAdmins()).thenReturn(listOf(lastAuthorityAdmin))
        whenever(dapsClientService.forEnvironment(any())).thenReturn(dapsClient)

        // act
        val result = uiResource.deleteOrganization(dummyDevOrganizationId(0))

        // assert
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(dummyDevOrganizationId(0))

        val organizationDbQuery = dsl.selectOne()
            .from(Tables.ORGANIZATION)
            .where(Tables.ORGANIZATION.ID.eq(dummyDevOrganizationId(0)))

        assertThat(dsl.fetchExists(organizationDbQuery)).isEqualTo(false)

        val userDbQuery = dsl.selectOne()
            .from(Tables.USER)
            .where(Tables.USER.ORGANIZATION_ID.eq(dummyDevOrganizationId(0)))

        assertThat(dsl.fetchExists(userDbQuery)).isEqualTo(false)

        val connectorDbQuery = dsl.selectOne()
            .from(Tables.CONNECTOR)
            .where(Tables.CONNECTOR.ORGANIZATION_ID.eq(dummyDevOrganizationId(0)))

        assertThat(dsl.fetchExists(connectorDbQuery)).isEqualTo(false)

        val componentDbQuery = dsl.selectOne()
            .from(Tables.COMPONENT)
            .where(Tables.COMPONENT.ORGANIZATION_ID.eq(dummyDevOrganizationId(0)))

        assertThat(dsl.fetchExists(componentDbQuery)).isEqualTo(false)
    }
}
