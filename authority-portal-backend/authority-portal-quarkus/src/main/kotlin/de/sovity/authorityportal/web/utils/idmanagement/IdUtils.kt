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

package de.sovity.authorityportal.web.utils.idmanagement

import jakarta.enterprise.context.ApplicationScoped
import kotlin.random.Random

@ApplicationScoped
class IdUtils {

    private val charPool = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun randomIdentifier(length: Int) =
        (1..length).map { Random.nextInt(0, charPool.length).let { charPool[it] } }.joinToString("")

    fun calculateVerificationDigits(id: String): String {
        val m = 1271
        val r = 36
        val input = id.uppercase()

        var product = 0
        for (char in input) {
            val value = charPool.indexOf(char)
            product = ((product + value) * r) % m
        }

        product = (product * r) % m
        val checksum = (m - product + 1) % m
        val secondDigit = checksum % r
        val firstDigit = (checksum - secondDigit) / r

        return charPool[firstDigit].toString() + charPool[secondDigit].toString()
    }
}
