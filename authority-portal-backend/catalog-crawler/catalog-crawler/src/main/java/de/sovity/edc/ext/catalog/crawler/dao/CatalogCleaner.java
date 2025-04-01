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

package de.sovity.edc.ext.catalog.crawler.dao;

import de.sovity.authorityportal.db.jooq.Tables;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.dao.utils.PostgresqlUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.Collection;

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class CatalogCleaner {

    public void removeCatalogByConnectors(DSLContext dsl, Collection<ConnectorRef> connectorRefs) {
        var co = Tables.CONTRACT_OFFER;
        var d = Tables.DATA_OFFER;

        var connectorIds = connectorRefs.stream().map(ConnectorRef::getConnectorId).collect(toSet());

        dsl.deleteFrom(co).where(PostgresqlUtils.in(co.CONNECTOR_ID, connectorIds)).execute();
        dsl.deleteFrom(d).where(PostgresqlUtils.in(d.CONNECTOR_ID, connectorIds)).execute();
    }
}
