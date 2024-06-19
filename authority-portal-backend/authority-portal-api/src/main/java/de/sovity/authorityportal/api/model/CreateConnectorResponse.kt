package de.sovity.authorityportal.api.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "Response DTO for connector registration")
data class CreateConnectorResponse(
    @field:Schema(description = "ID of the connector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val id: String?,

    @field:Schema(description = "Date and time of response", requiredMode = Schema.RequiredMode.REQUIRED)
    val changedDate: OffsetDateTime,

    @field:Schema(
        description = "Message status. Informs what type of action is necessary",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val status: CreateConnectorStatusDto,

    @field:Schema(description = "Optional error message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    val message: String?,
) {

    companion object {
        fun ok(connectorId: String, changedTime: OffsetDateTime): CreateConnectorResponse {
            return CreateConnectorResponse(connectorId, changedTime, CreateConnectorStatusDto.OK, null)
        }

        fun error(message: String, changedTime: OffsetDateTime): CreateConnectorResponse {
            return CreateConnectorResponse(null, changedTime, CreateConnectorStatusDto.ERROR, message)
        }

        fun warning(connectorId: String, message: String, changedTime: OffsetDateTime): CreateConnectorResponse {
            return CreateConnectorResponse(connectorId, changedTime, CreateConnectorStatusDto.WARNING, message)
        }
    }
}
