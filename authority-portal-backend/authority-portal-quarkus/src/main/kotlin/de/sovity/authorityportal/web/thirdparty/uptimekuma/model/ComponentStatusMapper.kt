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

package de.sovity.authorityportal.web.thirdparty.uptimekuma.model

import de.sovity.authorityportal.api.model.ComponentStatusDto
import de.sovity.authorityportal.db.jooq.enums.ComponentOnlineStatus

fun ComponentStatus.toDb(): ComponentOnlineStatus = when (this) {
    ComponentStatus.UP -> ComponentOnlineStatus.UP
    ComponentStatus.DOWN -> ComponentOnlineStatus.DOWN
    ComponentStatus.PENDING -> ComponentOnlineStatus.PENDING
    ComponentStatus.MAINTENANCE -> ComponentOnlineStatus.MAINTENANCE
}

fun ComponentOnlineStatus.toDto(): ComponentStatusDto = when (this) {
    ComponentOnlineStatus.UP -> ComponentStatusDto.UP
    ComponentOnlineStatus.DOWN -> ComponentStatusDto.DOWN
    ComponentOnlineStatus.PENDING -> ComponentStatusDto.PENDING
    ComponentOnlineStatus.MAINTENANCE -> ComponentStatusDto.MAINTENANCE
}
