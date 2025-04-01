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

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.authorityportal.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.catalog.crawler.AssertionUtils;
import de.sovity.edc.ext.catalog.crawler.CrawlerTestDb;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.ConnectorChangeTracker;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static de.sovity.edc.ext.catalog.crawler.crawling.writing.DataOfferWriterTestDataModels.Co;
import static de.sovity.edc.ext.catalog.crawler.crawling.writing.DataOfferWriterTestDataModels.Do;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ConnectorUpdateCatalogWriterTest {
    @RegisterExtension
    private static final CrawlerTestDb TEST_DATABASE = new CrawlerTestDb();

    @Test
    void testDataOfferWriter_allSortsOfUpdates() {
        TEST_DATABASE.testTransaction(dsl -> {
            var testDydi = new DataOfferWriterTestDydi();
            var testData = new DataOfferWriterTestDataHelper();
            var changes = new ConnectorChangeTracker();
            var dataOfferWriter = testDydi.getConnectorUpdateCatalogWriter();
            when(testDydi.getCrawlerConfig().getEnvironmentId()).thenReturn("test");

            // arrange
            var unchanged = Do.forName("unchanged");
            testData.existing(unchanged);
            testData.fetched(unchanged);

            var fieldChangedExisting = Do.forName("fieldChanged");
            var fieldChangedFetched = fieldChangedExisting.withAssetTitle("changed");
            testData.existing(fieldChangedExisting);
            testData.fetched(fieldChangedFetched);

            var added = Do.forName("added");
            testData.fetched(added);

            var removed = Do.forName("removed");
            testData.existing(removed);

            var changedCoExisting = Do.forName("contractOffer");
            var changedCoFetched = changedCoExisting.withContractOffers(List.of(
                    changedCoExisting.getContractOffers().get(0).withPolicyValue("changed")
            ));
            testData.existing(changedCoExisting);
            testData.fetched(changedCoFetched);

            var addedCoExisting = Do.forName("contractOfferAdded");
            var addedCoFetched = addedCoExisting.withContractOffer(new Co("added co", "added co"));
            testData.existing(addedCoExisting);
            testData.fetched(addedCoFetched);

            var removedCoExisting = Do.forName("contractOfferRemoved")
                    .withContractOffer(new Co("removed co", "removed co"));
            var removedCoFetched = Do.forName("contractOfferRemoved");
            testData.existing(removedCoExisting);
            testData.fetched(removedCoFetched);

            // act
            dsl.transaction(it -> testData.initialize(it.dsl()));
            dsl.transaction(it -> dataOfferWriter.updateDataOffers(
                    it.dsl(),
                    testData.connectorRef,
                    testData.fetchedDataOffers,
                    changes
            ));
            var actual = dsl.transactionResult(it -> new DataOfferWriterTestResultHelper(it.dsl()));

            // assert
            assertThat(actual.numDataOffers()).isEqualTo(6);
            assertThat(changes.getNumOffersAdded()).isEqualTo(1);
            assertThat(changes.getNumOffersUpdated()).isEqualTo(4);
            assertThat(changes.getNumOffersDeleted()).isEqualTo(1);

            var now = OffsetDateTime.now();
            var minuteAccuracy = new TemporalUnitLessThanOffset(1, ChronoUnit.MINUTES);
            var addedActual = actual.getDataOffer(added.getAssetId());
            assertAssetPropertiesEqual(testData, addedActual, added);
            assertThat(addedActual.getCreatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(addedActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(actual.numContractOffers(added.getAssetId())).isEqualTo(1);
            assertPolicyEquals(actual, testData, added, added.getContractOffers().get(0));

            var unchangedActual = actual.getDataOffer(unchanged.getAssetId());
            assertThat(unchangedActual.getUpdatedAt()).isEqualTo(testData.old);
            assertThat(unchangedActual.getCreatedAt()).isEqualTo(testData.old);

            var fieldChangedActual = actual.getDataOffer(fieldChangedExisting.getAssetId());
            assertAssetPropertiesEqual(testData, fieldChangedActual, fieldChangedFetched);
            assertThat(fieldChangedActual.getCreatedAt()).isEqualTo(testData.old);
            assertThat(fieldChangedActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);

            var removedActual = actual.getDataOffer(removed.getAssetId());
            assertThat(removedActual).isNull();

            var changedCoActual = actual.getDataOffer(changedCoExisting.getAssetId());
            assertThat(changedCoActual.getCreatedAt()).isEqualTo(testData.old);
            assertThat(changedCoActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(actual.numContractOffers(changedCoExisting.getAssetId())).isEqualTo(1);
            assertPolicyEquals(actual, testData, changedCoFetched, changedCoFetched.getContractOffers().get(0));

            var addedCoActual = actual.getDataOffer(addedCoExisting.getAssetId());
            assertThat(addedCoActual.getCreatedAt()).isEqualTo(testData.old);
            assertThat(addedCoActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(actual.numContractOffers(addedCoActual.getAssetId())).isEqualTo(2);

            var removedCoActual = actual.getDataOffer(removedCoExisting.getAssetId());
            assertThat(removedCoActual.getCreatedAt()).isEqualTo(testData.old);
            assertThat(removedCoActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(actual.numContractOffers(removedCoActual.getAssetId())).isEqualTo(1);
        });
    }

    private void assertAssetPropertiesEqual(DataOfferWriterTestDataHelper testData, DataOfferRecord actual,
                                            Do expected) {
        var actualUiAssetJson = actual.getUiAssetJson().data();
        var expectedUiAssetJson = testData.dummyAssetJson(expected);
        AssertionUtils.assertEqualJson(actualUiAssetJson, expectedUiAssetJson);
    }

    private void assertPolicyEquals(
            DataOfferWriterTestResultHelper actual,
            DataOfferWriterTestDataHelper scenario,
            Do expectedDo,
            Co expectedCo
    ) {
        var actualContractOffer = actual.getContractOffer(expectedDo.getAssetId(), expectedCo.getId());
        var actualUiPolicyJson = actualContractOffer.getUiPolicyJson().data();
        var expectedUiPolicyJson = scenario.dummyPolicyJson(expectedCo.getPolicyValue());
        assertThat(actualUiPolicyJson).isEqualTo(expectedUiPolicyJson);
    }
}
