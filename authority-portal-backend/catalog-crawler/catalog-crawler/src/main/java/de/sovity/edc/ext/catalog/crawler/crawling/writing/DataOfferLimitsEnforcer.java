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

import de.sovity.authorityportal.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.authorityportal.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.authorityportal.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class DataOfferLimitsEnforcer {
    private final CrawlerConfig crawlerConfig;
    private final CrawlerEventLogger crawlerEventLogger;

    public record DataOfferLimitsEnforced(
            Collection<FetchedDataOffer> abbreviatedDataOffers,
            boolean dataOfferLimitsExceeded,
            boolean contractOfferLimitsExceeded
    ) {
    }

    public DataOfferLimitsEnforced enforceLimits(Collection<FetchedDataOffer> dataOffers) {
        // Get limits from config
        var maxDataOffers = crawlerConfig.getMaxDataOffersPerConnector();
        var maxContractOffers = crawlerConfig.getMaxContractOffersPerDataOffer();
        List<FetchedDataOffer> offerList = new ArrayList<>(dataOffers);

        // No limits set
        if (maxDataOffers == -1 && maxContractOffers == -1) {
            return new DataOfferLimitsEnforced(dataOffers, false, false);
        }

        // Validate if limits exceeded
        var dataOfferLimitsExceeded = false;
        if (maxDataOffers != -1 && offerList.size() > maxDataOffers) {
            offerList = offerList.subList(0, maxDataOffers);
            dataOfferLimitsExceeded = true;
        }

        var contractOfferLimitsExceeded = false;
        for (var dataOffer : offerList) {
            var contractOffers = dataOffer.getContractOffers();
            if (contractOffers != null && maxContractOffers != -1 && contractOffers.size() > maxContractOffers) {
                dataOffer.setContractOffers(contractOffers.subList(0, maxContractOffers));
                contractOfferLimitsExceeded = true;
            }
        }

        // Create new list with limited offers
        return new DataOfferLimitsEnforced(offerList, dataOfferLimitsExceeded, contractOfferLimitsExceeded);
    }

    public void logEnforcedLimitsIfChanged(
            DSLContext dsl,
            ConnectorRef connectorRef,
            ConnectorRecord connector,
            DataOfferLimitsEnforced enforcedLimits
    ) {

        // DataOffer
        if (enforcedLimits.dataOfferLimitsExceeded() && connector.getDataOffersExceeded() == ConnectorDataOffersExceeded.OK) {
            var maxDataOffers = crawlerConfig.getMaxDataOffersPerConnector();
            crawlerEventLogger.logConnectorUpdateDataOfferLimitExceeded(dsl, connectorRef, maxDataOffers);
            connector.setDataOffersExceeded(ConnectorDataOffersExceeded.EXCEEDED);
        } else if (!enforcedLimits.dataOfferLimitsExceeded() && connector.getDataOffersExceeded() == ConnectorDataOffersExceeded.EXCEEDED) {
            crawlerEventLogger.logConnectorUpdateDataOfferLimitOk(dsl, connectorRef);
            connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        }

        // ContractOffer
        if (enforcedLimits.contractOfferLimitsExceeded() && connector.getContractOffersExceeded() == ConnectorContractOffersExceeded.OK) {
            var maxContractOffers = crawlerConfig.getMaxContractOffersPerDataOffer();
            crawlerEventLogger.logConnectorUpdateContractOfferLimitExceeded(dsl, connectorRef, maxContractOffers);
            connector.setContractOffersExceeded(ConnectorContractOffersExceeded.EXCEEDED);
        } else if (!enforcedLimits.contractOfferLimitsExceeded() &&
                connector.getContractOffersExceeded() == ConnectorContractOffersExceeded.EXCEEDED) {
            crawlerEventLogger.logConnectorUpdateContractOfferLimitOk(dsl, connectorRef);
            connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        }
    }
}
