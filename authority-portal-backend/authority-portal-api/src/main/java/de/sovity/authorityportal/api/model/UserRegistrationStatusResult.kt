package de.sovity.authorityportal.api.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Object for containing the current registration status for a user.")
data class UserRegistrationStatusResult(
    @field:Schema(description = "Registration status", requiredMode = Schema.RequiredMode.REQUIRED)
    val registrationStatus: UserRegistrationStatusDto,
)

