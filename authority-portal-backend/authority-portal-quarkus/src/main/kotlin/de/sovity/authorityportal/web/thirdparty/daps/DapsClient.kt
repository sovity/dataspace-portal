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

package de.sovity.authorityportal.web.thirdparty.daps

import de.sovity.authorityportal.web.environment.DeploymentEnvironmentConfiguration.DeploymentEnvironment.DapsConfig
import de.sovity.authorityportal.web.thirdparty.daps.ext.CustomKeycloakResource
import de.sovity.authorityportal.web.thirdparty.daps.ext.instantiateResource
import io.quarkus.logging.Log
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.ProtocolMapperRepresentation
import java.nio.charset.StandardCharsets

class DapsClient(dapsConfig: DapsConfig): AutoCloseable {

    private val realmName = dapsConfig.realmName()

    private val keycloak = KeycloakBuilder.builder()
        .serverUrl(dapsConfig.url())
        .realm(realmName)
        .grantType("client_credentials")
        .clientId(dapsConfig.clientId())
        .clientSecret(dapsConfig.clientSecret())
        .build()

    private val customKeycloakResource by lazy { keycloak.instantiateResource<CustomKeycloakResource>() }

    override fun close() {
        keycloak.close()
    }

    fun createClient(clientId: String) {
        Log.info("Creating client $clientId in realm $realmName")
        keycloak.realm(realmName).clients().create(buildClientRepresentation(clientId))
        Log.info("Client $clientId created in realm $realmName")
    }

    fun deleteClient(clientId: String) {
        val client = getClientById(clientId)
        if (client != null) {
            keycloak.realm(realmName).clients().get(client.id).remove()
        }
    }

    fun addCertificate(clientId: String, certificate: String) {
        val client = getClientById(clientId) ?: error("Client not found")

        customKeycloakResource.uploadJksCertificate(
            realmName,
            client.id,
            "jwt.credential",
            certificate.toByteArray(StandardCharsets.UTF_8),
            "Certificate PEM"
        )
    }

    fun addJwksUrl(clientId: String, jwksUrl: String) {
        Log.info("Getting client $clientId in realm $realmName")
        val client = getClientById(clientId) ?: error("Client not found")

        client.attributes["jwks.url"] = jwksUrl
        client.attributes["use.jwks.url"] = "true"
        keycloak.realm(realmName).clients().get(client.id).update(client)
    }

    fun configureMappers(clientId: String) {
        val client = getClientById(clientId) ?: error("Client not found")

        configureAudienceMapper(client)

        addHardcodedClaim(client, "nbf", "0", "long")
    }

    private fun configureAudienceMapper(client: ClientRepresentation) {
        val audience = "edc:dsp-api"

        val clientRes = keycloak.realm(realmName).clients().get(client.id)

        val rep = ProtocolMapperRepresentation().apply {
            name = "audience:$audience"
            protocol = "openid-connect"
            protocolMapper = "oidc-audience-mapper"
            config = mutableMapOf(
                "included.custom.audience" to audience,
                "access.token.claim" to "true",
                "id.token.claim" to "false",
                "userinfo.token.claim" to "false"
            )
        }
        clientRes.protocolMappers.createMapper(rep)
    }

    private fun addHardcodedClaim(
        client: ClientRepresentation,
        claimName: String,
        claimValue: String,
        jsonType: String = "String"
    ) {
        val clientRes = keycloak.realm(realmName).clients().get(client.id)

        val rep = ProtocolMapperRepresentation().apply {
            name = "hardcoded:$claimName"
            protocol = "openid-connect"
            protocolMapper = "oidc-hardcoded-claim-mapper"
            config = mutableMapOf(
                "claim.name" to claimName,
                "claim.value" to claimValue,
                "jsonType.label" to jsonType,
                "access.token.claim" to "true",
                "id.token.claim" to "false",
                "userinfo.token.claim" to "false"
            )
        }
        clientRes.protocolMappers.createMapper(rep)
    }

    private fun getClientById(clientId: String): ClientRepresentation? =
        keycloak.realm(realmName).clients().findByClientId(clientId).firstOrNull()

    private fun buildClientRepresentation(clientId: String): ClientRepresentation {
        return ClientRepresentation().apply {
            this.clientId = clientId
            isStandardFlowEnabled = false
            isDirectAccessGrantsEnabled = false
            isServiceAccountsEnabled = true
            clientAuthenticatorType = "client-jwt"
        }
    }
}
