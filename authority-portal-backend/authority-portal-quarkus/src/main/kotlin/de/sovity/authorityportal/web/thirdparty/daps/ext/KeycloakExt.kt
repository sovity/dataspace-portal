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

package de.sovity.authorityportal.web.thirdparty.daps.ext

import jakarta.ws.rs.client.WebTarget
import org.keycloak.admin.client.Keycloak

inline fun <reified T> Keycloak.instantiateResource(): T {
    val target = Keycloak::class.java.getDeclaredField("target").let {
        it.isAccessible = true
        it.get(this) as WebTarget
    }

    return Keycloak.getClientProvider().targetProxy(
        target,
        T::class.java
    )
}
