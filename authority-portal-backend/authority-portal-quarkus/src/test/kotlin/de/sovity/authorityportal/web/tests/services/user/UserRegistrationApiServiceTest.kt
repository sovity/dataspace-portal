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

package de.sovity.authorityportal.web.tests.services.user

import de.sovity.authorityportal.api.UiResource
import de.sovity.authorityportal.api.model.UserRegistrationStatusDto
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.web.tests.useDevUser
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class UserRegistrationApiServiceTest {

    @Inject
    lateinit var uiResource: UiResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Test
    @TestTransaction
    fun `userRegistrationStatus returns actual status`() {
        // arrange
        ScenarioData().apply {
            user(0, null) { it.registrationStatus = UserRegistrationStatus.ACTIVE }
            user(1, null) { it.registrationStatus = UserRegistrationStatus.PENDING }
            scenarioInstaller.install(this)
        }

        // act
        useDevUser(0, null, emptySet())
        val resultActive = uiResource.userRegistrationStatus()

        useDevUser(1, null, emptySet())
        val resultPending = uiResource.userRegistrationStatus()

        // assert
        assertThat(resultActive.registrationStatus).isEqualTo(UserRegistrationStatusDto.ACTIVE)
        assertThat(resultPending.registrationStatus).isEqualTo(UserRegistrationStatusDto.PENDING)
    }
}
