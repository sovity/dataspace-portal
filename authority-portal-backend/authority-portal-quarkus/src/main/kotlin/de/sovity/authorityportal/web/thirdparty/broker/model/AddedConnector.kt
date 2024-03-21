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

@Schema(description = "Information for adding an unknown connector.")
class AddedConnector {
    @Schema(description = "Connector Endpoint")
    var connectorEndpoint: String? = null

    @Schema(description = "Organization MDS ID")
    var mdsId: String? = null
}
