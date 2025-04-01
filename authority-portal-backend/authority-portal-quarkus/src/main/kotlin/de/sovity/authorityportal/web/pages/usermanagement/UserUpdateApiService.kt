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

package de.sovity.authorityportal.web.pages.usermanagement

import de.sovity.authorityportal.api.model.IdResponse
import de.sovity.authorityportal.api.model.OnboardingUserUpdateDto
import de.sovity.authorityportal.api.model.UpdateUserDto
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.web.services.UserService
import de.sovity.authorityportal.web.thirdparty.keycloak.KeycloakService
import de.sovity.authorityportal.web.utils.TimeUtils
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserUpdateApiService(
    val userService: UserService,
    val keycloakService: KeycloakService,
    val timeUtils: TimeUtils
) {

    fun updateUserDetails(userId: String, updateUserDto: UpdateUserDto): IdResponse {
        val user = userService.getUserOrThrow(userId)
        user.firstName = updateUserDto.firstName.trim()
        user.lastName = updateUserDto.lastName.trim()
        user.phone = updateUserDto.phone.trim()
        user.jobTitle = updateUserDto.jobTitle.trim()
        user.email = updateUserDto.email.trim()
        user.update()
        keycloakService.updateUser(userId, updateUserDto.firstName, updateUserDto.lastName, updateUserDto.email)
        return IdResponse(userId, timeUtils.now())
    }

    fun updateOnboardingUserDetails(userId: String, onboardingUserUpdateDto: OnboardingUserUpdateDto): IdResponse {
        val user = userService.getUserOrThrow(userId)
        user.firstName = onboardingUserUpdateDto.firstName.trim()
        user.lastName = onboardingUserUpdateDto.lastName.trim()
        user.phone = onboardingUserUpdateDto.phoneNumber.trim()
        user.jobTitle = onboardingUserUpdateDto.jobTitle.trim()
        user.registrationStatus = UserRegistrationStatus.ACTIVE
        user.update()
        keycloakService.updateUser(
            userId = userId,
            firstName = onboardingUserUpdateDto.firstName,
            lastName = onboardingUserUpdateDto.lastName,
            email = user.email
        )
        return IdResponse(userId, timeUtils.now())
    }
}
