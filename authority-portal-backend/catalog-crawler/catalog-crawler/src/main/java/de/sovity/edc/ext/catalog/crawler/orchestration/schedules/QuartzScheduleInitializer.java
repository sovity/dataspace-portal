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

package de.sovity.edc.ext.catalog.crawler.orchestration.schedules;

import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.utils.CronJobRef;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.utils.JobFactoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Collection;

@RequiredArgsConstructor
public class QuartzScheduleInitializer {
    private final Config config;
    private final Monitor monitor;
    private final Collection<CronJobRef<?>> jobs;

    @SneakyThrows
    public void startSchedules() {
        var jobFactory = new JobFactoryImpl(jobs);
        var scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.setJobFactory(jobFactory);

        jobs.forEach(job -> scheduleCronJob(scheduler, job));
        scheduler.start();
    }

    @SneakyThrows
    private void scheduleCronJob(Scheduler scheduler, CronJobRef<?> cronJobRef) {
        // CRON property name doubles as job name
        var jobName = cronJobRef.configPropertyName();

        // Skip scheduling if property not provided to ensure tests have no schedules running
        var cronTrigger = config.getString(jobName, "");
        if (StringUtils.isBlank(cronTrigger)) {
            monitor.info("No cron trigger configured for %s. Skipping.".formatted(jobName));
            return;
        }

        monitor.info("Starting schedule %s=%s.".formatted(jobName, cronTrigger));
        var job = JobBuilder.newJob(cronJobRef.clazz())
                .withIdentity(jobName)
                .build();
        var trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronTrigger))
                .build();

        scheduler.scheduleJob(job, trigger);
    }
}
