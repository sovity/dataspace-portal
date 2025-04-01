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

package de.sovity.authorityportal.web.thirdparty.caas

import de.sovity.authorityportal.web.thirdparty.caas.model.CaasPortalDeploymentDto
import de.sovity.authorityportal.web.thirdparty.caas.model.CaasPortalResponse
import de.sovity.authorityportal.web.thirdparty.caas.model.CaasStatusResponse
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URI

@ApplicationScoped
class CaasClient {

    @ConfigProperty(name = "authority-portal.caas.sovity.url")
    lateinit var caasUrl: String

    lateinit var caasClientResource: CaasClientResource

    @PostConstruct
    fun init() {
        caasClientResource = QuarkusRestClientBuilder.newBuilder().baseUri(URI(caasUrl)).build(CaasClientResource::class.java)
    }

    fun requestCaas(apPortalDeploymentDto: CaasPortalDeploymentDto): CaasPortalResponse {
        return caasClientResource.requestCaas(apPortalDeploymentDto)
    }

    fun deleteCaas(connectorIds: List<String>) {
        val response = caasClientResource.deleteCaas(connectorIds)

        expectStatusCode(response, Response.Status.OK.statusCode, "deleteCaas")
    }

    fun validateSubdomain(name: String): Boolean {
        return caasClientResource.validateSubdomain(name).valid
    }

    fun getCaasStatus(connectorIds: List<String>): List<CaasStatusResponse> {
        return caasClientResource.getCaasStatus(connectorIds).value
    }

    private fun expectStatusCode(response: Response, expectedStatusCode: Int, operationName: String) {
        if (response.status != expectedStatusCode) {
            error("CaaS API returned unexpected status code, when trying to call \"$operationName\" endpoint. Actual: ${response.status}, Expected: $expectedStatusCode")
        }
    }
}
