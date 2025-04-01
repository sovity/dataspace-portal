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

import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.authorityportal.db.jooq.enums.OrganizationRegistrationStatus
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.seeds.utils.dummyDevAssetId
import de.sovity.authorityportal.seeds.utils.dummyDevOrganizationId
import de.sovity.edc.ext.wrapper.api.common.model.DataSourceAvailability
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class DevScenario(
    val scenarioInstaller: ScenarioInstaller
) : SeedScenario {

    override val name = "dev"

    @Transactional
    override fun install() {
        val scenario = init()
        scenarioInstaller.install(scenario)
    }

    override fun init(): ScenarioData {
        val scenario = ScenarioData().apply {
            // Authority
            user(1, 1) {
                it.firstName = "Authority"
                it.lastName = "Admin"
            }
            organization(1, 1) {
                it.name = "Authority Organization"
            }
            user(2, 1) {
                it.firstName = "Authority"
                it.lastName = "User"
            }

            // Participant
            user(3, 2) {
                it.firstName = "Participant"
                it.lastName = "Admin"
            }
            organization(2, 3) {
                it.name = "Participant Organization"
            }
            user(4, 2) {
                it.firstName = "Participant"
                it.lastName = "User"
            }

            // Pending
            user(5, 3) {
                it.firstName = "Pending"
                it.lastName = "User"
                it.registrationStatus = UserRegistrationStatus.PENDING
            }
            organization(3, 5) {
                it.name = "Pending Organization"
                it.registrationStatus = OrganizationRegistrationStatus.PENDING
            }

            // User without organization
            user(6, null) {
                it.firstName = "Broken"
                it.lastName = "User"
                it.organizationId = null
            }

            // Service Partner Admin
            user(7, 4) {
                it.firstName = "Service Partner"
                it.lastName = "Admin"
            }
            organization(4, 7) {
                it.name = "Service Partner Organization"
            }

            // Operator Admin
            user(8, 5) {
                it.firstName = "Operator"
                it.lastName = "Admin"
            }
            organization(5, 8) {
                it.name = "Operator Organization"
            }

            // Catalog test data
            val objectMapper = ObjectMapper()

            connector(1, 1, 1)
            connector(2, 1, 1)
            connector(3, 1, 1)
            dataOffer(1, 1, 1, assetApplier = {
                it.assetId = dummyDevAssetId(1)
                it.title = "Asset Title"
                it.connectorEndpoint = "https://test-connector/dsp"
                it.participantId = dummyDevOrganizationId(1)
                it.creatorOrganizationName = "Authority Organization"
                it.language = "de"
                it.description = "Long description"
                it.descriptionShortText = "Short description"
                it.dataSourceAvailability = DataSourceAvailability.LIVE
            })
            dataOffer(1, 1, 2, assetApplier = {
                it.assetId = dummyDevAssetId(2)
                it.title = "OnDemand Asset"
                it.connectorEndpoint = null
                it.participantId = dummyDevOrganizationId(1)
                it.creatorOrganizationName = "Authority Organization"
                it.language = "de"
                it.description = "Long description"
                it.descriptionShortText = "Short description"
                it.dataSourceAvailability = DataSourceAvailability.ON_REQUEST
            })
            contractOffer(1, 1, 1, 1)
            contractOffer(1, 1, 2, 2)
        }
        return scenario
    }
}
