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

package de.sovity.edc.ext.catalog.crawler.crawling.logging;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for collecting the information required to build log messages about what was updated.
 */
@Getter
public class ConnectorChangeTracker {
    @Setter
    private int numOffersAdded = 0;

    @Setter
    private int numOffersDeleted = 0;

    @Setter
    private int numOffersUpdated = 0;

    @Setter
    private String participantIdChanged = null;

    public boolean isEmpty() {
        return numOffersAdded == 0 && numOffersDeleted == 0 && numOffersUpdated == 0 && participantIdChanged == null;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Connector is up to date.";
        }

        var msg = "Connector Updated.";
        if (numOffersAdded > 0 || numOffersDeleted > 0 || numOffersUpdated > 0) {
            List<String> offersMsgs = new ArrayList<>();
            if (numOffersAdded > 0) {
                offersMsgs.add("%d added".formatted(numOffersAdded));
            }
            if (numOffersUpdated > 0) {
                offersMsgs.add("%d updated".formatted(numOffersUpdated));
            }
            if (numOffersDeleted > 0) {
                offersMsgs.add("%d deleted".formatted(numOffersDeleted));
            }
            msg += " Data Offers changed: %s.".formatted(String.join(", ", offersMsgs));
        }
        if (participantIdChanged != null) {
            msg += " Participant ID changed to %s.".formatted(participantIdChanged);
        }
        return msg;
    }
}
