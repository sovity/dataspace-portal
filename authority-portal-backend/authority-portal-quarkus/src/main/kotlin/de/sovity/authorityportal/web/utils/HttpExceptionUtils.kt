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

import jakarta.ws.rs.NotAuthorizedException
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response

/**
 * Throws an exception that indicates that the user is not authorized to access the requested resource.
 */
fun unauthorized(message: String = ""): Nothing {
    throw NotAuthorizedException(
        "Access denied. $message",
        Response.status(Response.Status.UNAUTHORIZED)
            .header("WWW-Authenticate", "Bearer realm=\"authority-portal\"")
            .build()
    )
}

fun notFound(message: String = ""): Nothing {
    throw NotFoundException(
        "Resource not found. $message",
        Response.status(Response.Status.NOT_FOUND)
            .header("WWW-Authenticate", "Bearer realm=\"authority-portal\"")
            .build()
    )
}

fun resourceAlreadyExists(message: String = ""): Nothing {
    throw WebApplicationException(
        "Resource already exists. $message",
        Response.status(Response.Status.CONFLICT)
            .header("WWW-Authenticate", "Bearer realm=\"authority-portal\"")
            .build()
    )
}
