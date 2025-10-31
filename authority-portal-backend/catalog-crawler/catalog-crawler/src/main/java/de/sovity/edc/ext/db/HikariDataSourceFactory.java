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

package de.sovity.edc.ext.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;

import javax.sql.DataSource;

@UtilityClass
public class HikariDataSourceFactory {
    /**
     * Create a new {@link DataSource}.
     * <br>
     * This method is static, so we can use from test code.
     *
     * @param jdbcCredentials jdbc credentials
     * @param maxPoolSize max pool size
     * @param connectionTimeoutInMs connection timeout in ms
     * @return {@link DataSource}.
     */
    public static HikariDataSource newDataSource(
        JdbcCredentials jdbcCredentials,
        int maxPoolSize,
        int connectionTimeoutInMs
    ) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcCredentials.jdbcUrl());
        hikariConfig.setUsername(jdbcCredentials.jdbcUser());
        hikariConfig.setPassword(jdbcCredentials.jdbcPassword());
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setPoolName("edc-server");
        hikariConfig.setMaxLifetime(50000);
        hikariConfig.setConnectionTimeout(connectionTimeoutInMs);

        return new HikariDataSource(hikariConfig);
    }
}
