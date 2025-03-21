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
import {Component, Inject} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {
  APP_CONFIG,
  AppConfig,
} from '../../../../core/services/config/app-config';

@Component({
  selector: 'app-organization-pending-page',
  templateUrl: './organization-pending-page.component.html',
})
export class OrganizationPendingPageComponent {
  constructor(
    @Inject(APP_CONFIG) public appConfig: AppConfig,
    private titleService: Title,
  ) {
    this.titleService.setTitle('Pending');
  }
}
