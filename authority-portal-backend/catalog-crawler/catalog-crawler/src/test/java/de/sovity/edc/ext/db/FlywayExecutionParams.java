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

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Builder
@Value
@With
public class FlywayExecutionParams {
    @Builder.Default
    Consumer<String> infoLogger = System.out::println;

    @Builder.Default
    List<String> migrationLocations = new ArrayList<>();

    @Builder.Default
    boolean migrate = false;

    @Builder.Default
    boolean tryRepairOnFailedMigration = false;

    @Builder.Default
    boolean cleanEnabled = false;

    @Builder.Default
    boolean clean = false;

    @Builder.Default
    String table = "flyway_schema_history";
}
