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

package de.sovity.authorityportal.web.utils

class PersonNameUtils {
    companion object {
        /**
         * Splits a person's name into first and last name. The split is done at the right-most white space.
         * A potential second name is wrapped into firstName.
         *
         * @param name the name to split
         * @return a pair of first and last name
         */
        fun splitName(name: String): PersonName {
            val splitIndex = name.lastIndexOf(" ")
            val parts = if (splitIndex == -1) {
                arrayOf(name, "")
            } else {
                arrayOf(name.substring(0, splitIndex), name.substring(splitIndex + 1))
            }
            return PersonName(parts[0], parts[1])
        }
    }

    data class PersonName(val firstName: String, val lastName: String)
}
