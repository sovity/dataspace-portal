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

import de.sovity.edc.ce.versions.CrawlerGradleVersions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestDatabaseViaTestcontainers implements TestDatabase {
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "postgres";
    private static final String POSTGRES_DB = "edc";

    private final PostgreSQLContainer<?> container;

    public TestDatabaseViaTestcontainers() {
        container = new PostgreSQLContainer<>(CrawlerGradleVersions.POSTGRES_IMAGE_TAG)
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD)
                .withDatabaseName(POSTGRES_DB);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        container.stop();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        container.start();
    }
    @Override
    public JdbcCredentials getJdbcCredentials() {
        return new JdbcCredentials(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }
}
