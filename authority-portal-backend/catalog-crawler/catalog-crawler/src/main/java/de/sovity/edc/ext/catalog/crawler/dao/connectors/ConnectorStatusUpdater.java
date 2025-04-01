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
import de.sovity.edc.ext.catalog.crawler.dao.utils.PostgresqlUtils;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.stream.Collectors;

public class ConnectorStatusUpdater {
    public void markAsDead(DSLContext dsl, Collection<ConnectorRef> connectorRefs) {
        var connectorIds = connectorRefs.stream()
                .map(ConnectorRef::getConnectorId)
                .collect(Collectors.toSet());
        var c = Tables.CONNECTOR;
        dsl.update(c).set(c.ONLINE_STATUS, ConnectorOnlineStatus.DEAD)
                .where(PostgresqlUtils.in(c.CONNECTOR_ID, connectorIds)).execute();
    }
}
