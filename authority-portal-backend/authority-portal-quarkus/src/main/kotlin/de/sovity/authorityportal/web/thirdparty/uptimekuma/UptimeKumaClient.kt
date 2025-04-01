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

import de.sovity.authorityportal.web.environment.DeploymentEnvironmentConfiguration.DeploymentEnvironment
import de.sovity.authorityportal.web.environment.DeploymentEnvironmentService
import de.sovity.authorityportal.web.thirdparty.uptimekuma.model.ComponentStatus
import de.sovity.authorityportal.web.thirdparty.uptimekuma.model.ComponentStatusOverview
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.Base64
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@ApplicationScoped
class UptimeKumaClient(
    val deploymentEnvironmentService: DeploymentEnvironmentService,
    val uptimeKumaClientResource: UptimeKumaClientResource,
    @ConfigProperty(name = "authority-portal.kuma.api-key") val uptimeKumaApiKey: Optional<String>,
) {

    fun getStatusByEnvironments(): Map<String, ComponentStatusOverview> {
        val basicAuthHeader = Base64.getEncoder().encodeToString(":$uptimeKumaApiKey".toByteArray())
        val response = uptimeKumaClientResource.getMetrics("Basic $basicAuthHeader")

        val environments = deploymentEnvironmentService.findAll()
        return environments.mapValues { getComponentStatusOverview(response, it.value) }
    }

    private fun getComponentStatusOverview(
        response: String,
        envConfig: DeploymentEnvironment
    ): ComponentStatusOverview =
        ComponentStatusOverview().also {
            it.daps = getComponentStatus(
                envConfig.daps().kumaName().get(),
                response
            ).takeUnless { envConfig.daps().kumaName().getOrNull().isNullOrBlank() }
            it.catalogCrawler = getComponentStatus(
                envConfig.dataCatalog().kumaName().get(),
                response
            ).takeUnless { envConfig.dataCatalog().kumaName().getOrNull().isNullOrBlank() }
            it.loggingHouse = getComponentStatus(
                envConfig.loggingHouse().get().kumaName().get(),
                response
            ).takeUnless { envConfig.loggingHouse().getOrNull()?.kumaName()?.getOrNull().isNullOrBlank() }
        }

    private fun getComponentStatus(componentName: String, response: String): ComponentStatus {
        val regex = Regex("""monitor_status\{[^}]*monitor_name="$componentName"[^}]*\} (\d+)""")
        val matchResult = regex.find(response)
        val statusNumber = matchResult?.groupValues?.get(1)?.toIntOrNull()
            ?: run {
                Log.error("Invalid response from Uptime Kuma. Cannot parse statuses.")
                return ComponentStatus.DOWN
            }

        return ComponentStatus.fromInt(statusNumber)
            ?: run {
                Log.error("Invalid response from Uptime Kuma. Status number $statusNumber is not known.")
                return ComponentStatus.DOWN
            }
    }
}
