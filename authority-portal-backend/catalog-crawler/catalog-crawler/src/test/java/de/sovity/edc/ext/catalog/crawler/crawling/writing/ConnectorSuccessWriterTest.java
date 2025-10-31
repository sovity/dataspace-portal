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

import de.sovity.authorityportal.db.jooq.Tables;
import de.sovity.authorityportal.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.authorityportal.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.CrawlerTestDb;
import de.sovity.edc.ext.catalog.crawler.TestData;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedCatalog;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedContractOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ce.api.common.model.UiAsset;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ConnectorSuccessWriterTest {
    @RegisterExtension
    private static final CrawlerTestDb TEST_DATABASE = new CrawlerTestDb();

    ConnectorUpdateSuccessWriter connectorUpdateSuccessWriter;

    @BeforeEach
    void setup() {
        var container = new DataOfferWriterTestDydi();
        connectorUpdateSuccessWriter = container.getConnectorUpdateSuccessWriter();
        when(container.getCrawlerConfig().getMaxContractOffersPerDataOffer()).thenReturn(1);
        when(container.getCrawlerConfig().getMaxDataOffersPerConnector()).thenReturn(1);
    }

    @Test
    void testDataOfferWriter_fullSingleUpdate() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var connectorRef = TestData.connectorRef;
            TestData.insertConnector(dsl, connectorRef, unused -> {
            });
            var uiAsset = UiAsset.builder()
                    .assetId("assetId")
                    .title("title")
                    .description("# Description\n\n**with Markdown**")
                    .descriptionShortText("descriptionShortText")
                    .dataModel("dataModel")
                    .keywords(List.of("a", "b"))
                    .build();
            var fetchedContractOffer = FetchedContractOffer.builder()
                    .contractOfferId("contractOfferId")
                    .uiPolicyJson("\"test-policy\"")
                    .build();
            var fetchedDataOffer = FetchedDataOffer.builder()
                    .assetId("assetId")
                    .uiAsset(uiAsset)
                    .uiAssetJson("\"test\"")
                    .contractOffers(List.of(fetchedContractOffer))
                    .build();
            var fetchedCatalog = FetchedCatalog.builder()
                    .connectorRef(connectorRef)
                    .dataOffers(List.of(fetchedDataOffer))
                    .build();

            // act
            connectorUpdateSuccessWriter.handleConnectorOnline(
                    dsl,
                    connectorRef,
                    dsl.fetchOne(
                            Tables.CONNECTOR,
                            Tables.CONNECTOR.CONNECTOR_ID.eq(connectorRef.getConnectorId())
                    ),
                    fetchedCatalog
            );

            // assert
            var connector = dsl.fetchOne(
                    Tables.CONNECTOR,
                    Tables.CONNECTOR.CONNECTOR_ID.eq(connectorRef.getConnectorId())
            );
            var dataOffer = dsl.fetchOne(
                    Tables.DATA_OFFER,
                    DSL.and(
                            Tables.DATA_OFFER.CONNECTOR_ID.eq(connectorRef.getConnectorId()),
                            Tables.DATA_OFFER.ASSET_ID.eq("assetId")
                    )
            );
            var contractOffer = dsl.fetchOne(
                    Tables.CONTRACT_OFFER,
                    DSL.and(
                            Tables.CONTRACT_OFFER.CONNECTOR_ID.eq(connectorRef.getConnectorId()),
                            Tables.CONTRACT_OFFER.ASSET_ID.eq("assetId"),
                            Tables.CONTRACT_OFFER.CONTRACT_OFFER_ID.eq("contractOfferId")
                    )
            );

            var now = OffsetDateTime.now();
            var minuteAccuracy = new TemporalUnitLessThanOffset(1, ChronoUnit.MINUTES);
            assertThat(connector).isNotNull();
            assertThat(connector.getOnlineStatus()).isEqualTo(ConnectorOnlineStatus.ONLINE);
            assertThat(connector.getLastRefreshAttemptAt()).isCloseTo(now, minuteAccuracy);
            assertThat(connector.getLastSuccessfulRefreshAt()).isCloseTo(now, minuteAccuracy);
            assertThat(connector.getDataOffersExceeded()).isEqualTo(ConnectorDataOffersExceeded.OK);
            assertThat(connector.getContractOffersExceeded()).isEqualTo(ConnectorContractOffersExceeded.OK);

            assertThat(dataOffer).isNotNull();
            assertThat(dataOffer.getAssetTitle()).isEqualTo("title");
            assertThat(dataOffer.getDescriptionNoMarkdown()).isEqualTo("Description with Markdown");
            assertThat(dataOffer.getShortDescriptionNoMarkdown()).isEqualTo("descriptionShortText");
            assertThat(dataOffer.getDataModel()).isEqualTo("dataModel");
            assertThat(dataOffer.getKeywords()).containsExactly("a", "b");
            assertThat(dataOffer.getKeywordsCommaJoined()).isEqualTo("a, b");
            assertThat(dataOffer.getUiAssetJson().data()).isEqualTo("\"test\"");

            assertThat(contractOffer).isNotNull();
            assertThat(contractOffer.getUiPolicyJson().data()).isEqualTo("\"test-policy\"");
        });
    }
}
