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

package de.sovity.authorityportal.web.auth

import de.sovity.authorityportal.web.utils.unauthorized
import jakarta.ws.rs.container.ContainerRequestContext
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.resteasy.reactive.server.ServerRequestFilter

class ConfigApiKeyFilter {

    @ConfigProperty(name = "authority-portal.config.api-key")
    lateinit var apiKey: String

    @ServerRequestFilter(preMatching = true)
    fun filterApiKey(containerRequestContext: ContainerRequestContext) {
        val requestUri = containerRequestContext.uriInfo.requestUri.path

        if (requestUri.startsWith("/api/config")) {
            val apiKeyHeader = containerRequestContext.getHeaderString("x-api-key")

            if (apiKey != apiKeyHeader) {
                unauthorized("Invalid API key")
            }
        }
    }
}
