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

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Quickly launch {@link org.jooq.DSLContext}s from EDC configuration.
 */
@RequiredArgsConstructor
public class DslContextFactory {
    private final DataSource dataSource;

    /**
     * Create new {@link DSLContext} for querying DB.
     *
     * @return new {@link DSLContext}
     */
    public DSLContext newDslContext() {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    /**
     * Utility method for when the {@link DSLContext} will be used only for a single transaction.
     * <br>
     * An example would be a REST request.
     *
     * @param <R> return type
     * @return new {@link DSLContext} + opened transaction
     */
    public <R> R transactionResult(Function<DSLContext, R> function) {
        return newDslContext().transactionResult(transaction -> function.apply(transaction.dsl()));
    }

    /**
     * Utility method for when the {@link DSLContext} will be used only for a single transaction.
     * <br>
     * An example would be a REST request.
     */
    public void transaction(Consumer<DSLContext> function) {
        newDslContext().transaction(transaction -> function.accept(transaction.dsl()));
    }

    /**
     * Runs given code within a test transaction.
     *
     * @param code code to run within the test transaction
     */
    public void testTransaction(Consumer<DSLContext> code) {
        try {
            transaction(dsl -> {
                code.accept(dsl);
                throw new TestTransactionNoopException();
            });
        } catch (TestTransactionNoopException e) {
            // Ignore
        }
    }

    private static class TestTransactionNoopException extends RuntimeException {
    }
}
