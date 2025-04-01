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

package de.sovity.authorityportal.web.tests

import de.sovity.authorityportal.api.model.UserRoleDto
import de.sovity.authorityportal.seeds.utils.dummyDevOrganizationId
import de.sovity.authorityportal.seeds.utils.dummyDevUserUuid
import de.sovity.authorityportal.web.Roles
import de.sovity.authorityportal.web.auth.LoggedInUser
import de.sovity.authorityportal.web.utils.TimeUtils
import io.quarkus.test.junit.QuarkusMock
import org.assertj.core.api.RecursiveComparisonAssert
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Duration
import java.time.OffsetDateTime

inline fun <reified T : Any> installMockitoMock(): T {
    return installMock(mock<T>())
}

inline fun <reified T> installMock(mock: T): T {
    QuarkusMock.installMockForType(mock, T::class.java)
    return mock
}

fun <T : RecursiveComparisonAssert<T>> RecursiveComparisonAssert<T>.withOffsetDateTimeComparator(): RecursiveComparisonAssert<T> {
    return withEqualsForType({ a, b ->
        if (a == null || b == null) {
            a == b
        } else {
            Duration.between(a, b).abs().nano < 1_000_000
        }
    }, OffsetDateTime::class.java)
}

fun useDevUser(userUuidNr: Int, orgIdNr: Int?, roles: Set<String> = setOf(Roles.UserRoles.AUTHORITY_ADMIN)) {
    val loggedInUser = LoggedInUser(
        authenticated = true,
        userId = dummyDevUserUuid(userUuidNr),
        organizationId = orgIdNr?.let { dummyDevOrganizationId(it) },
        roles = roles
    )
    installMock(loggedInUser)
}

fun useUnauthenticated() {
    val loggedInUser = LoggedInUser(
        authenticated = false,
        userId = "",
        organizationId = null,
        roles = setOf(UserRoleDto.UNAUTHENTICATED.name)
    )
    installMock(loggedInUser)
}

fun useMockNow(now: OffsetDateTime) {
    val timeUtils = installMockitoMock<TimeUtils>()
    whenever(timeUtils.now()).thenReturn(now)
}

inline fun <reified T> T.loadTestResource(name: String): String {
    val fullPath = "/${T::class.simpleName}/$name"
    return T::class.java.getResourceAsStream(fullPath)?.reader()
        .use { it?.readText() ?: error("Can't read resource $fullPath") }
}
