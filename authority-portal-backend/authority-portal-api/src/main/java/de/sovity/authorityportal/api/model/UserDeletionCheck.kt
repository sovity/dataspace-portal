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

package de.sovity.authorityportal.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Result of the user deletion check.")
data class UserDeletionCheck(
    @field:Schema(description = "User's ID", requiredMode = Schema.RequiredMode.REQUIRED)
    val userId: String,

    @field:Schema(description = "Indicator if the user can be deleted", requiredMode = Schema.RequiredMode.REQUIRED)
    val canBeDeleted: Boolean,

    @field:Schema(
        description = "Indicator for the user being the last PA in their organization",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @field:JsonProperty("isLastParticipantAdmin") // Workaround because codegen messes up boolean naming
    val isLastParticipantAdmin: Boolean,

    @field:Schema(
        description = "Indicator for the user being the creator of their organization",
        requiredMode = Schema.RequiredMode.REQUIRED,
        name = "isOrganizationCreator"
    )
    @field:JsonProperty("isOrganizationCreator") // Workaround because codegen messes up boolean naming
    val isOrganizationCreator: Boolean,

    @field:Schema(description = "List of possible successors (if needed)", requiredMode = Schema.RequiredMode.REQUIRED)
    var possibleCreatorSuccessors: List<PossibleCreatorSuccessor>,
)
