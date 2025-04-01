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

package de.sovity.authorityportal.web.environment

import de.sovity.authorityportal.api.model.DeploymentEnvironmentDto
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentConfiguration.DeploymentEnvironment
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentConfiguration.DeploymentEnvironment.DapsConfig
import jakarta.enterprise.context.ApplicationScoped
import kotlin.jvm.optionals.getOrNull

@ApplicationScoped
class DeploymentEnvironmentDtoService(
    val deploymentEnvironmentService: DeploymentEnvironmentService
) {

    fun findByIdOrThrow(envId: String): DeploymentEnvironmentDto =
        buildDto(envId, deploymentEnvironmentService.findByIdOrThrow(envId))

    fun findAll(): List<DeploymentEnvironmentDto> =
        deploymentEnvironmentService.findAll()
            .map { it.value.position() to buildDto(it.key, it.value) }
            .sortedBy { it.first }
            .map { it.second }
            .toList()

    private fun buildDto(envId: String, deploymentEnvironment: DeploymentEnvironment): DeploymentEnvironmentDto {
        return DeploymentEnvironmentDto(
            environmentId = envId,
            title = deploymentEnvironment.title(),
            dapsJwksUrl = buildDapsJwksUrl(deploymentEnvironment.daps()),
            dapsTokenUrl = buildDapsTokenUrl(deploymentEnvironment.daps()),
            loggingHouseUrl = deploymentEnvironment.loggingHouse().getOrNull()?.url()
        )
    }

    private fun buildDapsJwksUrl(daps: DapsConfig): String {
        return "${daps.url()}/realms/${daps.realmName()}/protocol/openid-connect/certs"
    }

    private fun buildDapsTokenUrl(daps: DapsConfig): String {
        return "${daps.url()}/realms/${daps.realmName()}/protocol/openid-connect/token"
    }
}
