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

package de.sovity.authorityportal.web.services.reporting.utils

import com.opencsv.CSVWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


data class CsvColumn<T>(val name: String, val getter: (T) -> String)

fun <T> buildCsv(columns: List<CsvColumn<T>>, rows: Collection<T>): ByteArrayInputStream {
    val out = mutableListOf<Array<String>>()
    out.add(columns.map { it.name }.toTypedArray())
    out.addAll(rows.map { row -> columns.map { it.getter(row) }.toTypedArray() })
    return buildCsvRaw(out)
}

private fun buildCsvRaw(csvData: MutableList<Array<String>>): ByteArrayInputStream {
    val outputStream = ByteArrayOutputStream()
    CSVWriter(outputStream.writer()).use { csvWriter ->
        csvWriter.writeAll(csvData)
    }

    return ByteArrayInputStream(outputStream.toByteArray())
}
