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

package de.sovity.edc.ext.catalog.crawler.crawling.logging;

import de.sovity.edc.ext.catalog.crawler.utils.StringUtils2;
import lombok.NonNull;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Helper Dto that contains User Message + Error Stack Trace to be written into
 * {@link de.sovity.authorityportal.db.jooq.tables.CrawlerEventLog}.
 * <br>
 * This class exists so that logging exceptions has a consistent format.
 *
 * @param message message
 * @param stackTraceOrNull stack trace
 */
public record CrawlerEventErrorMessage(String message, String stackTraceOrNull) {

    public static CrawlerEventErrorMessage ofMessage(@NonNull String message) {
        return new CrawlerEventErrorMessage(message, null);
    }

    public static CrawlerEventErrorMessage ofStackTrace(@NonNull String baseMessage, @NonNull Throwable cause) {
        var message = baseMessage;
        message = StringUtils2.removeSuffix(message, ".");
        message = StringUtils2.removeSuffix(message, ":");
        message = "%s: %s".formatted(message, cause.getClass().getName());
        return new CrawlerEventErrorMessage(message, ExceptionUtils.getStackTrace(cause));
    }
}
