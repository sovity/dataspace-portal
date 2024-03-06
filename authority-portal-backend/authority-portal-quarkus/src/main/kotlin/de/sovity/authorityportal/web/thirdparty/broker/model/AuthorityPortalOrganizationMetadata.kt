/*
 * Copyright (c) 2024 sovity GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      sovity GmbH - initial implementation
 */
package de.sovity.authorityportal.web.thirdparty.broker.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Information about a single organization from the Authority Portal.")
class AuthorityPortalOrganizationMetadata {
    @Schema(description = "MDS-ID from the Authority Portal")
    var mdsId: String = ""
    @Schema(description = "Company name")
    var name: String = ""
}
