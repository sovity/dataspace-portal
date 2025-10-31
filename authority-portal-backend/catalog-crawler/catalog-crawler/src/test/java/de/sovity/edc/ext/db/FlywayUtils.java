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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.output.MigrateResult;
import org.flywaydb.core.api.output.RepairResult;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FlywayUtils {
    private final FlywayExecutionParams params;
    private final DataSource dataSource;

    public static void cleanAndMigrate(FlywayExecutionParams params, DataSource dataSource) {
        var instance = new FlywayUtils(params, dataSource);
        instance.cleanIfEnabled();
        instance.migrateOrValidate();
    }

    public static List<String> parseFlywayLocations(String locations) {
        return Arrays.stream(locations.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toList();
    }

    private void cleanIfEnabled() {
        if (params.isClean()) {
            params.getInfoLogger().accept("Running flyway clean.");
            var flyway = setupFlyway();
            flyway.clean();
        }
    }

    private void migrateOrValidate() {
        if (params.isMigrate()) {
            migrate();
        } else {
            validate();
        }
    }

    private void validate() {
        var flyway = setupFlyway();
        flyway.validate();
    }

    private void migrate() {
        var flyway = setupFlyway();
        flyway.info().getInfoResult().migrations.stream()
                .map(migration -> "Found migration: %s".formatted(migration.filepath))
                .forEach(it -> params.getInfoLogger().accept(it));

        try {
            var migrateResult = flyway.migrate();
            handleFlywayMigrationResult(migrateResult);
        } catch (FlywayException e) {
            if (params.isTryRepairOnFailedMigration()) {
                repairAndRetryMigration(flyway);
            } else {
                throw new IllegalStateException("Flyway migration failed for '%s'"
                        .formatted(params.getTable()), e);
            }
        }
    }

    private void repairAndRetryMigration(Flyway flyway) {
        try {
            var repairResult = flyway.repair();
            handleFlywayRepairResult(repairResult);
            var migrateResult = flyway.migrate();
            handleFlywayMigrationResult(migrateResult);
        } catch (FlywayException e) {
            throw new IllegalStateException("Flyway migration failed for '%s'"
                    .formatted(params.getTable()), e);
        }
    }

    private void handleFlywayRepairResult(RepairResult repairResult) {
        if (!repairResult.repairActions.isEmpty()) {
            var repairActions = String.join(", ", repairResult.repairActions);
            params.getInfoLogger().accept("Repair actions for datasource %s: %s"
                    .formatted(params.getTable(), repairActions));
        }

        if (!repairResult.warnings.isEmpty()) {
            var warnings = String.join(", ", repairResult.warnings);
            throw new IllegalStateException("Repairing datasource %s failed: %s"
                    .formatted(params.getTable(), warnings));
        }
    }

    private Flyway setupFlyway() {
        params.getInfoLogger().accept("Flyway migration locations for '%s': %s".formatted(
                params.getTable(), params.getMigrationLocations()));
        return Flyway.configure()
                .baselineOnMigrate(true)
                .failOnMissingLocations(true)
                .dataSource(dataSource)
                .table(params.getTable())
                .locations(params.getMigrationLocations().toArray(new String[0]))
                .cleanDisabled(!params.isCleanEnabled())
                .load();
    }

    private void handleFlywayMigrationResult(MigrateResult migrateResult) {
        if (migrateResult.migrationsExecuted > 0) {
            params.getInfoLogger().accept(String.format(
                    "Successfully migrated database for datasource %s " +
                            "from version %s to version %s",
                    params.getTable(),
                    migrateResult.initialSchemaVersion,
                    migrateResult.targetSchemaVersion));
        } else {
            params.getInfoLogger().accept(String.format(
                    "No migration necessary for datasource %s. Current version is %s",
                    params.getTable(),
                    migrateResult.initialSchemaVersion));
        }
    }
}
