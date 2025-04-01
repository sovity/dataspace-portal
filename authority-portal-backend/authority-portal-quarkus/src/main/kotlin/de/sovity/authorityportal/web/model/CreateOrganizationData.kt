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

package de.sovity.authorityportal.web.model

import de.sovity.authorityportal.db.jooq.enums.OrganizationLegalIdType

class CreateOrganizationData {
    var name: String? = null
    var url: String? = null
    var businessUnit: String? = null
    var industry: String? = null
    var address: String? = null
    var billingAddress: String? = null
    var description: String? = null
    var legalIdType: OrganizationLegalIdType? = null
    var legalIdNumber: String? = null
    var commerceRegisterLocation: String? = null
    var mainContactName: String? = null
    var mainContactEmail: String? = null
    var mainContactPhone: String? = null
    var techContactName: String? = null
    var techContactEmail: String? = null
    var techContactPhone: String? = null
}
