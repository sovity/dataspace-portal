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

import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import de.sovity.edc.extension.postgresql.FlywayExecutionParams;
import de.sovity.edc.extension.postgresql.FlywayUtils;
import de.sovity.edc.extension.postgresql.HikariDataSourceFactory;
import de.sovity.edc.extension.postgresql.JdbcCredentials;
import org.jooq.DSLContext;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.function.Consumer;

public class CrawlerTestDb implements BeforeAllCallback, AfterAllCallback {
    private final TestDatabaseViaTestcontainers db = new TestDatabaseViaTestcontainers();

    private HikariDataSource dataSource = null;
    private DslContextFactory dslContextFactory = null;

    public void testTransaction(Consumer<DSLContext> code) {
        dslContextFactory.testTransaction(code);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        // Init DB
        db.beforeAll(extensionContext);

        // Init Data Source
        var credentials = new JdbcCredentials(
                db.getJdbcCredentials().jdbcUrl(),
                db.getJdbcCredentials().jdbcUser(),
                db.getJdbcCredentials().jdbcPassword()
        );
        dataSource = HikariDataSourceFactory.newDataSource(credentials, 10, 1000);
        dslContextFactory = new DslContextFactory(dataSource);

        // Migrate DB
        var params = baseConfig("classpath:/migration-test-utils")
                .migrate(true)
                .build();
        try {
            FlywayUtils.cleanAndMigrate(params, dataSource);
        } catch (Exception e) {
            var paramsWithClean = params.withClean(true).withCleanEnabled(true);
            FlywayUtils.cleanAndMigrate(paramsWithClean, dataSource);
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (dataSource != null) {
            dataSource.close();
        }

        // Close DB
        db.afterAll(extensionContext);
    }

    public static FlywayExecutionParams.FlywayExecutionParamsBuilder baseConfig(String additionalMigrationLocations) {
        var migrationLocations = FlywayUtils.parseFlywayLocations(
            "classpath:/db/migration,%s".formatted(additionalMigrationLocations)
        );

        return FlywayExecutionParams.builder()
            .migrationLocations(migrationLocations)
            .table("flyway_schema_history");
    }
}
