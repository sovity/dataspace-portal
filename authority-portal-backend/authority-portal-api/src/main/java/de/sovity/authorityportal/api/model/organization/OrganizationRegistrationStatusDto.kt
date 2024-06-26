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

package de.sovity.authorityportal.api.model.organization

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Possible user registration statuses.", enumAsRef = true)
enum class OrganizationRegistrationStatusDto {
    INVITED,  // Organization that has been invited by another participant
    ONBOARDING,  // The participant admin for this organization needs to fill in missing data on login
    PENDING,  // Self-registered organization that is waiting for approval
    ACTIVE,  // Organization that has been approved and is active
    REJECTED // Organization that has been rejected
}

