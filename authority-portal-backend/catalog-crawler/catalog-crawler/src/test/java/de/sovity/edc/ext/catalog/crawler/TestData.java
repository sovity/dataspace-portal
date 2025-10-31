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

package de.sovity.edc.ext.catalog.crawler;

import de.sovity.authorityportal.db.jooq.Tables;
import de.sovity.authorityportal.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.authorityportal.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.authorityportal.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.experimental.UtilityClass;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.function.Consumer;

@UtilityClass
public class TestData {
    public static OffsetDateTime old = OffsetDateTime.now().withNano(0).withSecond(0).withMinute(0).withHour(0).minusDays(100);

    public static ConnectorRef connectorRef = new ConnectorRef(
            "MDSL1234XX.C1234XX",
            "test",
            "My Org",
            "MDSL1234XX",
            "https://example.com/api/v1/dsp"
    );

    public static void insertConnector(
            DSLContext dsl,
            ConnectorRef connectorRef,
            Consumer<ConnectorRecord> applier
    ) {
        var organization = dsl.newRecord(Tables.ORGANIZATION);
        organization.setId(connectorRef.getOrganizationId());
        organization.setName(connectorRef.getOrganizationLegalName());
        organization.insert();

        var connector = dsl.newRecord(Tables.CONNECTOR);
        connector.setEnvironment(connectorRef.getEnvironmentId());
        connector.setOrganizationId(connectorRef.getOrganizationId());
        connector.setConnectorId(connectorRef.getConnectorId());
        connector.setName(connectorRef.getConnectorId() + " Name");
        connector.setEndpointUrl(connectorRef.getEndpoint());
        connector.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
        connector.setLastRefreshAttemptAt(null);
        connector.setLastSuccessfulRefreshAt(null);
        connector.setCreatedAt(old);
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        applier.accept(connector);
        connector.insert();
    }
}
