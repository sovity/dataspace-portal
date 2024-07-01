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

package de.sovity.authorityportal.api.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Possible user registration statuses.", enumAsRef = true)
enum class UserRegistrationStatusDto {

    /**
     * User that has been invited to be a participant user
     */
    INVITED,

    /**
     * User that has been invited to be a participant admin
     */
    ONBOARDING,

    /**
     * User awaiting approval of his self-registration
     */
    PENDING,

    /**
     * Active, fully registered, and approved User
     */
    ACTIVE,

    /**
     * User who got his self-registration rejected
     */
    REJECTED,

    /**
     * User that has been deactivated, and cannot access the portal anymore
     */
    DEACTIVATED
}

