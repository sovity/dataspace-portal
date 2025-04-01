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

package de.sovity.edc.ext.catalog.crawler.dao.connectors;

import de.sovity.authorityportal.db.jooq.Tables;
import de.sovity.authorityportal.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.authorityportal.db.jooq.tables.Connector;
import de.sovity.authorityportal.db.jooq.tables.Organization;
import de.sovity.authorityportal.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.DSLContext;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class ConnectorQueries {
    private final CrawlerConfig crawlerConfig;

    public ConnectorRecord findByConnectorId(DSLContext dsl, String connectorId) {
        var c = Tables.CONNECTOR;
        return dsl.fetchOne(c, c.CONNECTOR_ID.eq(connectorId));
    }

    public Set<ConnectorRef> findConnectorsForScheduledRefresh(DSLContext dsl, ConnectorOnlineStatus onlineStatus) {
        return queryConnectorRefs(dsl, (c, o) -> c.ONLINE_STATUS.eq(onlineStatus));
    }

    public Set<ConnectorRef> findAllConnectorsForKilling(DSLContext dsl, Duration deleteOfflineConnectorsAfter) {
        var minLastRefresh = OffsetDateTime.now().minus(deleteOfflineConnectorsAfter);
        return queryConnectorRefs(dsl, (c, o) -> c.LAST_SUCCESSFUL_REFRESH_AT.lt(minLastRefresh));
    }

    @NotNull
    private Set<ConnectorRef> queryConnectorRefs(
            DSLContext dsl,
            BiFunction<Connector, Organization, Condition> condition
    ) {
        var c = Tables.CONNECTOR;
        var o = Tables.ORGANIZATION;
        var query = dsl.select(
                        c.CONNECTOR_ID.as("connectorId"),
                        c.ENVIRONMENT.as("environmentId"),
                        o.NAME.as("organizationLegalName"),
                        o.ID.as("organizationId"),
                        c.ENDPOINT_URL.as("endpoint")
                )
                .from(c)
                .leftJoin(o).on(c.ORGANIZATION_ID.eq(o.ID))
                .where(condition.apply(c, o), c.ENVIRONMENT.eq(crawlerConfig.getEnvironmentId()), c.ENDPOINT_URL.isNotNull())
                .fetchInto(ConnectorRef.class);

        return new HashSet<>(query);
    }
}
