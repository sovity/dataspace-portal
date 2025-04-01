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

package de.sovity.authorityportal.api.model.organization

import de.sovity.authorityportal.api.model.MemberInfo
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "Organization information.")
data class OrganizationDetailsDto(
    @field:Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val id: String,
    @field:Schema(description = "Legal name", requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,
    @field:Schema(description = "Business unit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val businessUnit: String?,
    @field:Schema(description = "Industry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val industry: String?,
    @field:Schema(description = "Main Address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val mainAddress: String?,
    @field:Schema(description = "Billing Address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val billingAddress: String?,
    @field:Schema(description = "Legal ID type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val legalIdType: OrganizationLegalIdTypeDto?,
    @field:Schema(description = "Legal ID number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val legalId: String?,
    @field:Schema(description = "Commerce register location (if applicable)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val commerceRegisterLocation: String?,
    @field:Schema(description = "URL of the organization website", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val url: String?,
    @field:Schema(description = "Description of what the company does/is", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val description: String?,
    @field:Schema(description = "Registration status", requiredMode = Schema.RequiredMode.REQUIRED)
    val registrationStatus: OrganizationRegistrationStatusDto,
    @field:Schema(description = "Member count", requiredMode = Schema.RequiredMode.REQUIRED)
    val memberCount: Int,
    @field:Schema(description = "Connector count", requiredMode = Schema.RequiredMode.REQUIRED)
    var connectorCount: Int,
    @field:Schema(description = "Data offer count (sum)", requiredMode = Schema.RequiredMode.REQUIRED)
    var dataOfferCount: Int,
    @field:Schema(description = "Data offer count (with data source)", requiredMode = Schema.RequiredMode.REQUIRED)
    var liveDataOfferCount: Int,
    @field:Schema(description = "Data offer count (on request)", requiredMode = Schema.RequiredMode.REQUIRED)
    var onRequestDataOfferCount: Int,
    @field:Schema(description = "Member information", requiredMode = Schema.RequiredMode.REQUIRED)
    val memberList: List<MemberInfo>,
    @field:Schema(description = "Organization creator: User Id", requiredMode = Schema.RequiredMode.REQUIRED)
    val createdByUserId: String,
    @field:Schema(description = "Organization creator: First Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val createdByFirstName: String,
    @field:Schema(description = "Organization creator: Last Name", requiredMode = Schema.RequiredMode.REQUIRED)
    val createdByLastName: String,
    @field:Schema(description = "Main Contact Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val mainContactName: String?,
    @field:Schema(description = "Main Contact Email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val mainContactEmail: String?,
    @field:Schema(description = "Main Contact Phone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val mainContactPhone: String?,
    @field:Schema(description = "Tech Contact Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val techContactName: String?,
    @field:Schema(description = "Tech Contact Email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val techContactEmail: String?,
    @field:Schema(description = "Tech Contact Phone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val techContactPhone: String?,
    @field:Schema(
        description = "Creation date of organization or organization invite",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val createdAt: OffsetDateTime,
)
