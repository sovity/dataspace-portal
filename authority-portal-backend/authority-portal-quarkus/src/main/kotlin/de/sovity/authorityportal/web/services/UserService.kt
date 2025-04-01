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

package de.sovity.authorityportal.web.services

import de.sovity.authorityportal.broker.dao.utils.eqAny
import de.sovity.authorityportal.db.jooq.Tables
import de.sovity.authorityportal.db.jooq.enums.UserOnboardingType
import de.sovity.authorityportal.db.jooq.enums.UserRegistrationStatus
import de.sovity.authorityportal.db.jooq.tables.records.UserRecord
import de.sovity.authorityportal.web.model.CreateUserData
import de.sovity.authorityportal.web.utils.TimeUtils
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.time.OffsetDateTime

@ApplicationScoped
class UserService(
    val dsl: DSLContext,
    val timeUtils: TimeUtils
) {

    fun getUserOrThrow(userId: String): UserRecord {
        return getUser(userId) ?: error("User with id $userId not found")
    }

    fun getUserOrCreate(userId: String, userOnboardingType: UserOnboardingType): UserRecord {
        return getUser(userId) ?: createUser(userId, onboardingType = userOnboardingType)
    }

    fun getAllUsers(): List<UserRecord> {
        val u = Tables.USER

        return dsl.selectFrom(u)
            .fetch()
            .toList()
    }

    fun getUsersByOrganizationId(organizationId: String): List<UserRecord> {
        val u = Tables.USER

        return dsl.selectFrom(u)
            .where(u.ORGANIZATION_ID.eq(organizationId))
            .fetch()
            .toList()
    }

    fun getUserCountsByOrganizationIds(): Map<String, Int> {
        val u = Tables.USER

        return dsl.select(u.ORGANIZATION_ID, DSL.count())
            .from(u)
            .groupBy(u.ORGANIZATION_ID)
            .fetchMap(u.ORGANIZATION_ID, DSL.count())
    }

    fun userExistsInDb(email: String): Boolean {
        val u = Tables.USER

        return dsl.fetchExists(
            dsl.selectFrom(u)
                .where(u.EMAIL.eq(email))
        )
    }

    private fun getUser(userId: String): UserRecord? {
        val u = Tables.USER

        return dsl.selectFrom(u)
            .where(u.ID.eq(userId))
            .fetchOne()
    }

    fun createUser(userId: String, organizationId: String? = null, onboardingType: UserOnboardingType, invitedBy: String? = null): UserRecord {
        return dsl.newRecord(Tables.USER).also {
            it.id = userId
            it.organizationId = organizationId
            it.registrationStatus = initialRegistrationStatus(onboardingType)
            it.createdAt = timeUtils.now()
            it.onboardingType = onboardingType
            it.invitedBy = invitedBy

            it.insert()
        }
    }

    fun createUser(
        userId: String,
        userData: CreateUserData,
        organizationId: String? = null,
        onboardingType: UserOnboardingType
    ): UserRecord {
        return dsl.newRecord(Tables.USER).also {
            it.id = userId
            it.email = userData.email?.trim()
            it.firstName = userData.firstName?.trim()
            it.lastName = userData.lastName?.trim()
            it.jobTitle = userData.jobTitle?.trim()
            it.phone = userData.phone?.trim()
            it.organizationId = organizationId
            it.registrationStatus = initialRegistrationStatus(onboardingType)
            it.createdAt = timeUtils.now()
            it.onboardingType = onboardingType

            it.insert()
        }
    }

    private fun initialRegistrationStatus(onboardingType: UserOnboardingType): UserRegistrationStatus? =
        when (onboardingType) {
            UserOnboardingType.SELF_REGISTRATION -> UserRegistrationStatus.PENDING
            UserOnboardingType.INVITATION -> UserRegistrationStatus.INVITED
        }

    fun deleteUser(userId: String) {
        val u = Tables.USER

        dsl.deleteFrom(u)
            .where(u.ID.eq(userId))
            .execute()
    }

    fun deleteUsers(userIds: List<String>) {
        val u = Tables.USER

        dsl.deleteFrom(u)
            .where(u.ID.eqAny(userIds))
            .execute()
    }

    fun getUnconfirmedUserIds(expirationTime: OffsetDateTime): List<String> {
        val u = Tables.USER

        return dsl.select(u.ID)
            .from(u)
            .where(u.REGISTRATION_STATUS.eq(UserRegistrationStatus.INVITED))
            .and(u.CREATED_AT.lt(expirationTime))
            .fetch(u.ID)
    }

    fun removeOrganizationIdFromUnconfirmedUsers(expirationCutoffTime: OffsetDateTime) {
        val u = Tables.USER

        dsl.update(u)
            .setNull(u.ORGANIZATION_ID)
            .where(u.REGISTRATION_STATUS.eq(UserRegistrationStatus.INVITED))
            .and(u.CREATED_AT.lt(expirationCutoffTime))
            .execute()
    }

    fun deleteUnconfirmedUsers(userIds: List<String>): Int {
        val u = Tables.USER

        return dsl.deleteFrom(u)
            .where(u.ID.eqAny(userIds))
            .execute()
    }

    fun deleteInvitationReference(invitedBy: String) {
        val u = Tables.USER

        dsl.update(u)
            .setNull(u.INVITED_BY)
            .where(u.INVITED_BY.eq(invitedBy))
            .execute()
    }

    fun deleteInvitationReferencesToOrgMembers(orgMemberIds: List<String>) {
        val u = Tables.USER

        dsl.update(u)
            .setNull(u.INVITED_BY)
            .where(u.INVITED_BY.eqAny(orgMemberIds))
            .execute()
    }

    fun deleteOrganizationIds(orgMemberIds: List<String>) {
        val u = Tables.USER

        dsl.update(u)
            .setNull(u.ORGANIZATION_ID)
            .where(u.ID.eqAny(orgMemberIds))
            .execute()
    }

    fun updateStatus(userId: String, status: UserRegistrationStatus) {
        val u = Tables.USER

        dsl.update(u)
            .set(u.REGISTRATION_STATUS, status)
            .where(u.ID.eq(userId))
            .execute()
    }

    fun userExistsOutsideOrg(userIds: List<String>, organizationId: String): Boolean {
        val u = Tables.USER
        val otherOrgUsers = dsl
            .selectOne()
            .from(u)
            .where(u.ID.eqAny(userIds), u.ORGANIZATION_ID.notEqual(organizationId))

        return dsl.fetchExists(otherOrgUsers)
    }
}
