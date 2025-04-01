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

package de.sovity.authorityportal.seeds

import io.quarkus.runtime.Startup
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import jakarta.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.Optional

@ApplicationScoped
class SeedInstaller(
    val scenarios: Instance<SeedScenario>,
    @ConfigProperty(name = "authority-portal.seed.scenario") val scenarioId: Optional<String>
) {

    @Startup
    @Transactional
    fun seed() {
        if (scenarioId.isEmpty || scenarioId.get() == "none") {
            return
        }

        val scenario = scenarios.find { it.name == scenarioId.get() }
        scenario?.install() ?: error("Unknown seed scenario: ${scenarioId.get()}")
    }
}
