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
package de.sovity.authorityportal.broker

import de.sovity.authorityportal.api.CatalogResource
import de.sovity.authorityportal.api.model.catalog.CatalogPageQuery
import de.sovity.authorityportal.api.model.catalog.CatalogPageResult
import de.sovity.authorityportal.api.model.catalog.DataOfferDetailPageQuery
import de.sovity.authorityportal.api.model.catalog.DataOfferDetailPageResult
import de.sovity.authorityportal.broker.services.api.CatalogApiService
import de.sovity.authorityportal.broker.services.api.DataOfferDetailApiService
import de.sovity.authorityportal.web.auth.AuthUtils
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

/**
 * Implementation of [CatalogResource]
 */
@ApplicationScoped
class CatalogResourceImpl(
    val catalogApiService: CatalogApiService,
    val dataOfferDetailApiService: DataOfferDetailApiService,
    val authUtils: AuthUtils
) : CatalogResource {

    @Transactional
    override fun catalogPage(environmentId: String, query: CatalogPageQuery): CatalogPageResult {
        authUtils.requiresAuthenticated()
        authUtils.requiresMemberOfAnyOrganization()
        return catalogApiService.catalogPage(environmentId, query)
    }

    @Transactional
    override fun dataOfferDetailPage(
        environmentId: String,
        query: DataOfferDetailPageQuery
    ): DataOfferDetailPageResult {
        authUtils.requiresAuthenticated()
        authUtils.requiresMemberOfAnyOrganization()
        return dataOfferDetailApiService.dataOfferDetailPage(environmentId, query)
    }
}
