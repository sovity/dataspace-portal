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
import {OrganizationDetailsDto} from '@sovity.de/authority-portal-client';
import {Fetched} from 'src/app/core/utils/fetched';
import {HeaderBarConfig} from '../../../shared/common/header-bar/header-bar.model';

export interface AuthorityOrganizationEditPageState {
  organizationId: string;
  organization: Fetched<OrganizationDetailsDto>;
  headerBarConfig: HeaderBarConfig | null;
  busy: boolean;
}

export const DEFAULT_AUTHORITY_ORGANIZATION_EDIT_PAGE_STATE: AuthorityOrganizationEditPageState =
  {
    organizationId: '',
    organization: Fetched.empty(),
    headerBarConfig: null,
    busy: false,
  };
