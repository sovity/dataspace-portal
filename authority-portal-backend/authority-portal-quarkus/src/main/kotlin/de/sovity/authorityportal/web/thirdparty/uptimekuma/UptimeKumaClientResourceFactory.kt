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

package de.sovity.authorityportal.web.thirdparty.uptimekuma

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URI
import java.util.Optional

@ApplicationScoped
class UptimeKumaClientResourceFactory(
    @ConfigProperty(name = "authority-portal.kuma.metrics-url")
    val uptimeKumaUrl: Optional<String>
) {

    @Produces
    @ApplicationScoped
    fun uptimeKumaRestClient(): UptimeKumaClientResource? {
        if (uptimeKumaUrl.isEmpty) {
            return null
        }

        return QuarkusRestClientBuilder.newBuilder()
            .baseUri(URI.create(uptimeKumaUrl.get()))
            .build(UptimeKumaClientResource::class.java)!!
    }
}
