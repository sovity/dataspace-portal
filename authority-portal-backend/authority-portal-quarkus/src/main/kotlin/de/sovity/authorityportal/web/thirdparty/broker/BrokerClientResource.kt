package de.sovity.authorityportal.web.thirdparty.broker

import de.sovity.authorityportal.web.thirdparty.broker.model.AuthorityPortalConnectorInfo
import de.sovity.authorityportal.web.thirdparty.broker.model.AuthorityPortalOrganizationMetadataRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("backend/api/management/wrapper/broker")
interface BrokerClientResource {

    @PUT
    @Path("connectors")
    @Consumes(MediaType.APPLICATION_JSON)
    fun addConnectors(
        @HeaderParam("X-Api-Key") apiKey: String,
        @QueryParam("adminApiKey") adminApiKey: String,
        connectors: List<String>
    ): Response

    @DELETE
    @Path("connectors")
    @Consumes(MediaType.APPLICATION_JSON)
    fun removeConnectors(
        @HeaderParam("X-Api-Key") apiKey: String,
        @QueryParam("adminApiKey") adminApiKey: String,
        connectors: List<String>
    ): Response

    @POST
    @Path("authority-portal-api/connectors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun getConnectorMetadata(
        @HeaderParam("X-Api-Key") apiKey: String,
        @QueryParam("adminApiKey") adminApiKey: String,
        connectors: List<String>
    ): List<AuthorityPortalConnectorInfo>

    @POST
    @Path("authority-portal-api/organization-metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    fun setOrganizationMetadata(
        @HeaderParam("X-Api-Key") apiKey: String,
        @QueryParam("adminApiKey") adminApiKey: String,
        orgMetadata: AuthorityPortalOrganizationMetadataRequest
    ): Response
}
