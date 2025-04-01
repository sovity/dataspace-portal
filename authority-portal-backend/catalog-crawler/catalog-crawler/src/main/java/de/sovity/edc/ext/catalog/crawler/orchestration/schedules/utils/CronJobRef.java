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

package de.sovity.edc.ext.catalog.crawler.orchestration.schedules.utils;

import org.quartz.Job;

import java.util.function.Supplier;

/**
 * CRON Job.
 *
 * @param configPropertyName EDC Config property that decides cron expression
 * @param clazz class of the job
 * @param factory factory that initializes the task class
 * @param <T> job type
 */
public record CronJobRef<T extends Job>(
        String configPropertyName,
        Class<T> clazz,
        Supplier<T> factory
) {

    @SuppressWarnings("unchecked")
    public Supplier<Job> asJobSupplier() {
        return (Supplier<Job>) factory;
    }
}
