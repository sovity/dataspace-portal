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

package de.sovity.authorityportal.web.tests.services.catalog

import de.sovity.authorityportal.api.CatalogResource
import de.sovity.authorityportal.api.model.catalog.DataOfferDetailPageQuery
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.seeds.utils.ScenarioData
import de.sovity.authorityportal.seeds.utils.ScenarioInstaller
import de.sovity.authorityportal.seeds.utils.dummyDevAssetId
import de.sovity.authorityportal.seeds.utils.dummyDevConnectorId
import de.sovity.authorityportal.seeds.utils.dummyDevContractOfferId
import de.sovity.authorityportal.web.environment.CatalogDataspaceConfig
import de.sovity.authorityportal.web.environment.CatalogDataspaceConfigService
import de.sovity.authorityportal.web.tests.useDevUser
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpression
import io.quarkus.test.InjectMock
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime

@QuarkusTest
@ExtendWith(MockitoExtension::class)
class DataOfferDetailApiTest {

    @Inject
    lateinit var catalogResource: CatalogResource

    @Inject
    lateinit var scenarioInstaller: ScenarioInstaller

    @Inject
    lateinit var dsl: DSLContext

    @InjectMock
    lateinit var catalogDataspaceConfigService: CatalogDataspaceConfigService

    @Test
    @TestTransaction
    fun `test queryDataOfferDetails`() {
        // arrange
        useDevUser(0, 0)

        ScenarioData().apply {
            organization(0, 0)
            user(0, 0)

            connector(0, 0, 0) {
                it.endpointUrl = "https://connector/dsp"
            }
            dataOffer(0, 0, 0, assetApplier = {
                it.assetId = dummyDevAssetId(0)
                it.title = "Data Offer 0"
                it.dataCategory = "Data Category 0"
                it.description = "Data Offer 0 Description"
            })
            contractOffer(0, 0, 0, 0)

            connector(1, 0, 0)
            dataOffer(1, 0, 1, assetApplier = {
                it.assetId = dummyDevAssetId(1)
                it.title = "Data Offer 1"
                it.dataCategory = "Data Category 1"
                it.description = "Data Offer 1 Description"
            })
            contractOffer(1, 0, 1, 1)

            scenarioInstaller.install(this)
        }

        createDataOfferView(
            datetime = OffsetDateTime.now().minusDays(1),
            connectorId = dummyDevConnectorId(0, 0),
            assetId = dummyDevAssetId(0)
        )
        createDataOfferView(
            datetime = OffsetDateTime.now().minusDays(2),
            connectorId = dummyDevConnectorId(0, 0),
            assetId = dummyDevAssetId(0)
        )

        whenever(catalogDataspaceConfigService.forEnvironment(any())).thenReturn(
            CatalogDataspaceConfig(
                namesByConnectorId = mapOf(
                    dummyDevConnectorId(0, 1) to "Dataspace 1",
                    dummyDevConnectorId(0, 2) to "Dataspace 2"
                ),
                defaultName = "MDS"
            )
        )

        // act
        val result = catalogResource.dataOfferDetailPage(
            environmentId = "test",
            query = DataOfferDetailPageQuery(
                connectorId = dummyDevConnectorId(0, 0),
                assetId = dummyDevAssetId(0)
            )
        )

        // assert
        assertThat(result.assetId).isEqualTo(dummyDevAssetId(0))
        assertThat(result.connectorEndpoint).isEqualTo("https://connector/dsp")
        assertThat(result.asset.dataCategory).isEqualTo("Data Category 0")
        assertThat(result.asset.title).isEqualTo("Data Offer 0")
        assertThat(result.asset.description).isEqualTo("Data Offer 0 Description")
        assertThat(result.contractOffers).hasSize(1)
        assertThat(result.contractOffers.first().contractOfferId).isEqualTo(dummyDevContractOfferId(0))
        assertThat(result.contractOffers.first().contractPolicy.expression)
            .usingRecursiveComparison()
            .isEqualTo(UiPolicyExpression.empty())
        assertThat(result.contractOffers.first().contractPolicy.errors).isEmpty()
        assertThat(result.viewCount).isEqualTo(2)
    }

    private fun createDataOfferView(datetime: OffsetDateTime, connectorId: String, assetId: String) {
        dsl.newRecord(Tables.DATA_OFFER_VIEW_COUNT).also {
            it.assetId = assetId
            it.connectorId = connectorId
            it.date = datetime
            it.store()
        }
    }
}
