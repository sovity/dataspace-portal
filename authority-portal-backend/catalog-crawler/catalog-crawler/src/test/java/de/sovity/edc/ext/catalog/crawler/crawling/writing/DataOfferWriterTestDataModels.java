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

import lombok.Value;
import lombok.With;

import java.util.ArrayList;
import java.util.List;

class DataOfferWriterTestDataModels {
    /**
     * Dummy Data Offer
     */
    @Value
    static class Do {
        @With
        String assetId;
        @With
        String assetTitle;
        @With
        List<Co> contractOffers;

        public Do withContractOffer(Co co) {
            var list = new ArrayList<>(contractOffers);
            list.add(co);
            return this.withContractOffers(list);
        }

        public static Do forName(String name) {
            return new Do(name, name + " Title", List.of(new Co(name + " CO", name + " Policy")));
        }
    }

    /**
     * Dummy Contract Offer
     */
    @Value
    static class Co {
        @With
        String id;
        @With
        String policyValue;
    }

    public static Co forName(String name) {
        return new Co(name + " CO", name + " Policy");
    }

}
