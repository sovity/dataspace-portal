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
package de.sovity.authorityportal.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response

@Path("/api/reporting/")
@Tag(name = "Reporting", description = "Authority Portal Reporting API Endpoints.")
interface ReportingResource {
    @GET
    @Path("/connectors")
    @Produces("text/csv")
    @Operation(description = "Download own organization connectors information as csv")
    fun createConnectorsCsvReport(
        @QueryParam("environmentId")
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        environmentId: String
    ): Response

    @GET
    @Path("/users")
    @Produces("text/csv")
    @Operation(description = "Download organization's users reporting details.")
    fun createUsersAndRolesCsvReport(): Response

    @GET
    @Path("/data-offers")
    @Produces("text/csv")
    @Operation(description = "Download data offers reporting details.")
    fun createDataOffersCsvReport(
        @QueryParam("environmentId")
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        environmentId: String
    ): Response

    @GET
    @Path("/system-stability")
    @Produces("text/csv")
    @Operation(description = "Download system stability reporting details.")
    fun createSystemStabilityCsvReport(
        @QueryParam("environmentId")
        @Valid @NotBlank(message = "EnvironmentId cannot be blank")
        environmentId: String
    ): Response
}
