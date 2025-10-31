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

import io.smallrye.config.ConfigMapping
import java.time.Duration
import java.util.Optional

@ConfigMapping(prefix = "authority-portal.deployment")
interface DeploymentEnvironmentConfiguration {
    fun environments(): Map<String, DeploymentEnvironment>

    interface DeploymentEnvironment {
        fun title(): String
        fun position(): Int
        fun daps(): DapsConfig
        fun dataCatalog(): DataCatalogConfig
        fun loggingHouse(): Optional<LoggingHouseConfig>
        fun centralComponents(): Map<String, CentralComponentInitConfig>

        interface CentralComponentInitConfig {
            fun clientId(): String
            fun homepageUrl(): Optional<String>
            fun endpointUrl(): String
            fun certificate(): String
        }

        interface DapsConfig {
            fun url(): String
            fun realmName(): String
            fun clientId(): String
            fun clientSecret(): String
            fun kumaName(): Optional<String>
        }

        interface DataCatalogConfig {
            fun hideOfflineDataOffersAfter(): Duration
            fun catalogPagePageSize(): Int
            fun dataspaceNames(): DataspaceNames
            fun kumaName(): Optional<String>

            interface DataspaceNames {
                fun connectorIds(): Map<String, String>
                fun default(): String
            }
        }

        interface LoggingHouseConfig {
            fun url(): String
            fun kumaName(): Optional<String>
        }
    }
}
