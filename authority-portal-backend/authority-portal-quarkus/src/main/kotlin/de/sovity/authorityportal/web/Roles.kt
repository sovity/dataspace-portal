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

package de.sovity.authorityportal.web

object Roles {
    object UserRoles {
        /**
         * User has the role of an operator admin.
         */
        const val OPERATOR_ADMIN = "UR_AUTHORITY-PORTAL_OPERATOR-ADMIN"

        /**
         * User has the role of a service partner admin.
         */
        const val SERVICE_PARTNER_ADMIN = "UR_AUTHORITY-PORTAL_SERVICE_PARTNER-ADMIN"

        /**
         * User has the role of an authority admin.
         */
        const val AUTHORITY_ADMIN = "UR_AUTHORITY-PORTAL_AUTHORITY-ADMIN"

        /**
         * User has the role of an authority user.
         */
        const val AUTHORITY_USER = "UR_AUTHORITY-PORTAL_AUTHORITY-USER"

        /**
         * User has the role of a participant admin.
         */
        const val PARTICIPANT_ADMIN = "UR_AUTHORITY-PORTAL_PARTICIPANT-ADMIN"

        /**
         * User has the role of a participant curator.
         */
        const val PARTICIPANT_CURATOR = "UR_AUTHORITY-PORTAL_PARTICIPANT-CURATOR"

        /**
         * User has the role of a participant user.
         */
        const val PARTICIPANT_USER = "UR_AUTHORITY-PORTAL_PARTICIPANT-USER"
    }
}
