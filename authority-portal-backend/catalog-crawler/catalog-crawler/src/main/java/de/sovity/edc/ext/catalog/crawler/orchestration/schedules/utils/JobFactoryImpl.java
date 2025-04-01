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

import lombok.NonNull;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JobFactoryImpl implements JobFactory {
    private final Map<Class<?>, Supplier<Job>> factories;

    public JobFactoryImpl(@NonNull Collection<CronJobRef<?>> jobs) {
        factories = jobs.stream().collect(Collectors.toMap(
                CronJobRef::clazz,
                CronJobRef::asJobSupplier
        ));
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
        Class<?> jobClazz = bundle.getJobDetail().getJobClass();
        Supplier<Job> factory = factories.get(jobClazz);
        if (factory == null) {
            throw new IllegalArgumentException("No factory for Job class %s. Supported Job classes are: %s.".formatted(
                    jobClazz.getName(),
                    factories.keySet().stream().map(Class::getName).collect(Collectors.joining(", "))
            ));
        }
        return factory.get();
    }
}
