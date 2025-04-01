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

package de.sovity.authorityportal.web.pages.connectormanagement

import de.sovity.authorityportal.api.model.ConnectorTypeDto
import de.sovity.authorityportal.db.jooq.enums.ConnectorType

fun ConnectorType.toDto(): ConnectorTypeDto = when (this) {
    ConnectorType.OWN -> ConnectorTypeDto.OWN
    ConnectorType.PROVIDED -> ConnectorTypeDto.PROVIDED
    ConnectorType.CAAS -> ConnectorTypeDto.CAAS
    ConnectorType.CONFIGURING -> ConnectorTypeDto.CONFIGURING
}

fun ConnectorTypeDto.toDb(): ConnectorType = when (this) {
    ConnectorTypeDto.OWN -> ConnectorType.OWN
    ConnectorTypeDto.PROVIDED -> ConnectorType.PROVIDED
    ConnectorTypeDto.CAAS -> ConnectorType.CAAS
    ConnectorTypeDto.CONFIGURING -> ConnectorType.CONFIGURING
}
