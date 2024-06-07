/*
 * Copyright (c) 2024 sovity GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      sovity GmbH - initial implementation
 */

package de.sovity.authorityportal.web.environment

import io.smallrye.config.ConfigMapping
import java.time.Duration

@ConfigMapping(prefix = "authority-portal.deployment")
interface DeploymentEnvironmentConfiguration {
    fun environments(): Map<String, DeploymentEnvironment>
    fun global(): DeploymentGlobalConfig

    interface DeploymentGlobalConfig {
        fun broker(): BrokerGlobalConfig

        interface BrokerGlobalConfig {
            fun defaultDataspace(): String
            fun knownDataSpaceConnectors(): String?
        }
    }

    interface DeploymentEnvironment {
        fun title(): String
        fun position(): Int
        fun daps(): DapsConfig
        fun broker(): BrokerConfig
        fun loggingHouse(): LoggingHouseConfig

        interface DapsConfig {
            fun url(): String
            fun realmName(): String
            fun clientId(): String
            fun clientSecret(): String
            fun kumaName(): String
        }

        interface BrokerConfig {
            fun url(): String
            fun adminApiKey(): String
            fun apiKey(): String
            fun kumaName(): String
            fun hideOfflineDataOffersAfter(): Duration
            fun catalogPagePageSize(): Int
            fun maxDataOffersPerConnector(): Int
            fun maxContractOffersPerDataOffer(): Int
        }

        interface LoggingHouseConfig {
            fun url(): String
            fun kumaName(): String
        }
    }
}
