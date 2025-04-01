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
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

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

    fun configureMappers(clientId: String, connectorId: String, certificate: String) {
        val client = getClientById(clientId) ?: error("Client not found")
        setAccessTokenClaim(client)

        val datMapper = buildDatMapper(clientId, connectorId)
        datMapper.config["transport-certs-claim"] = generateSha256(certificate)

        keycloak.realm(realmName).clients().get(client.id).protocolMappers.createMapper(datMapper)
    }

    fun configureMappers(clientId: String, connectorId: String) {
        val client = getClientById(clientId) ?: error("Client not found")
        setAccessTokenClaim(client)

        val datMapper = buildDatMapper(clientId, connectorId)
        keycloak.realm(realmName).clients().get(client.id).protocolMappers.createMapper(datMapper)
    }

    private fun setAccessTokenClaim(client: ClientRepresentation) {
        keycloak.realm(realmName).clients().get(client.id).protocolMappers.mappers.forEach {
            it.config["access.token.claim"] = "false"
            keycloak.realm(realmName).clients().get(client.id).protocolMappers.update(it.id, it)
        }
    }

    private fun buildDatMapper(clientId: String, connectorId: String): ProtocolMapperRepresentation {
        val datMapper = ProtocolMapperRepresentation().apply {
            protocol = "openid-connect"
            protocolMapper = "dat-mapper"
            name = "DAT Mapper"
            config = mutableMapOf(
                "security-profile-claim" to "idsc:BASE_SECURITY_PROFILE",
                "audience-claim" to "idsc:IDS_CONNECTORS_ALL",
                "scope-claim" to "idsc:IDS_CONNECTOR_ATTRIBUTES_ALL",
                "subject-claim" to clientId,
                "referring-connector-claim" to connectorId,
                "access.token.claim" to "true"
            )
        }
        return datMapper
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

    private fun generateSha256(certString: String): String {
        // DER
        val certInputStream = certString.byteInputStream()
        val certFactory = CertificateFactory.getInstance("X.509")
        val certificate = certFactory.generateCertificate(certInputStream) as X509Certificate
        val der = certificate.encoded

        // SHA256
        val sha256 = MessageDigest.getInstance("SHA-256")
        val hash = sha256.digest(der)
        return hash.joinToString("") { String.format("%02x", it) }
    }
}
