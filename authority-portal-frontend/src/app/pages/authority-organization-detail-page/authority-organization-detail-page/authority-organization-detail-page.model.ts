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

export enum AuthorityOrganizationDetailTab {
  DETAIL = 'DETAIL',
  MEMBERS = 'MEMBERS',
  CONNECTORS = 'CONNECTORS',
  DATA_OFFERS = 'DATA_OFFERS',
  USER_DETAILS = 'USER_DETAILS',
}

export interface UserDetailPageConfig {
  userId: string;
  organizationId: string;
}
