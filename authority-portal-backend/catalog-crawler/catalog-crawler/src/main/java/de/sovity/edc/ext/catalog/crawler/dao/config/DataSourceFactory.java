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

package de.sovity.edc.ext.catalog.crawler.dao.config;

import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.ext.catalog.crawler.CrawlerConfigProps;
import de.sovity.edc.ext.db.JdbcCredentials;
import de.sovity.edc.ext.db.HikariDataSourceFactory;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import javax.sql.DataSource;

@RequiredArgsConstructor
public class DataSourceFactory {
    private final Config config;


    /**
     * Create a new {@link DataSource} from EDC Config.
     *
     * @return {@link DataSource}.
     */
    public HikariDataSource newDataSource() {
        var jdbcCredentials = getJdbcCredentials();
        int maxPoolSize = CrawlerConfigProps.CRAWLER_DB_CONNECTION_POOL_SIZE.getInt(config);
        int connectionTimeoutInMs = CrawlerConfigProps.CRAWLER_DB_CONNECTION_TIMEOUT_IN_MS.getInt(config);
        return HikariDataSourceFactory.newDataSource(
                jdbcCredentials,
                maxPoolSize,
                connectionTimeoutInMs
        );
    }


    public JdbcCredentials getJdbcCredentials() {
        return new JdbcCredentials(
                CrawlerConfigProps.CRAWLER_DB_JDBC_URL.getStringOrThrow(config),
                CrawlerConfigProps.CRAWLER_DB_JDBC_USER.getStringOrThrow(config),
                CrawlerConfigProps.CRAWLER_DB_JDBC_PASSWORD.getStringOrThrow(config)
        );
    }
}
