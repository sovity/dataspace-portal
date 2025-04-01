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

import de.sovity.authorityportal.web.environment.DeploymentEnvironmentConfiguration.DeploymentEnvironment
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject

@ApplicationScoped
class DeploymentEnvironmentService {

    @Inject
    lateinit var deploymentEnvironmentConfiguration: DeploymentEnvironmentConfiguration

    fun onStartUp(@Observes event: StartupEvent) {
        findAll().ifEmpty {
            error("No deployment environments were configured. Please configure at least one environment.")
        }
    }

    fun findAll(): Map<String, DeploymentEnvironment> = deploymentEnvironmentConfiguration.environments()

    fun findByIdOrThrow(envId: String): DeploymentEnvironment =
        deploymentEnvironmentConfiguration.environments()[envId] ?: error("Environment $envId not found")

    fun assertValidEnvId(envId: String) {
        findByIdOrThrow(envId)
    }
}
