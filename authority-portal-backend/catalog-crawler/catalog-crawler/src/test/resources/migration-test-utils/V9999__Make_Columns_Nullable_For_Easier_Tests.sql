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

do
$$
    declare
        r record;
    begin
        for r in (select 'alter table "' || c.table_schema || '"."' || c.table_name || '" alter column "' || c.column_name ||
                         '" drop not null;' as command
                  from information_schema.columns c
                  where c.table_schema not in ('pg_catalog', 'information_schema') -- exclude system schemas
                    and c.table_name in ('connector', 'organization', 'user') -- only selected AP tables
                    and c.is_nullable = 'NO'
                    and not exists (SELECT tc.constraint_type
                         FROM information_schema.table_constraints AS tc
                                  JOIN information_schema.key_column_usage AS kcu
                                       ON tc.constraint_name = kcu.constraint_name
                                           AND tc.table_schema = kcu.table_schema
                         WHERE tc.table_schema = c.table_schema
                           and tc.table_name = c.table_name
                           AND kcu.column_name = c.column_name
                           AND tc.constraint_type = 'PRIMARY KEY')) -- exclude primary keys
            loop
                execute r.command;
            end loop;
    end
$$;
