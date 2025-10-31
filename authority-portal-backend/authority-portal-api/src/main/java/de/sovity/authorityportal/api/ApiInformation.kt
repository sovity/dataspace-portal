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
package de.sovity.authorityportal.api

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License

@OpenAPIDefinition(
    info = Info(
        title = "Data Space Portal API",
        version = "5.0.0",
        description = "REST API for sovity's Data Space Portal.",
        contact = Contact(
            name = "sovity GmbH",
            email = "contact@sovity.de",
            url = "https://github.com/sovity/dataspace-portal/issues/new/choose"
        ),
        license = License(name = "Apache License 2.0", url = "https://github.com/sovity/dataspace-portal/blob/main/LICENSE")
    ),
    externalDocs = ExternalDocumentation(
        description = "Data Space Portal API definitions.",
        url = "https://github.com/sovity/dataspace-portal/tree/main"
    )
)
interface ApiInformation
