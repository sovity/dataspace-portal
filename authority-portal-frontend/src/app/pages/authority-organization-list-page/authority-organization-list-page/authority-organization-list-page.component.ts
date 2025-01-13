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
import {Location} from '@angular/common';
import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute} from '@angular/router';
import {Subject} from 'rxjs';
import {take, takeUntil} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {
  OrganizationOverviewEntryDto,
  OrganizationRegistrationStatusDto,
  UserRoleDto,
} from '@sovity.de/authority-portal-client';
import {SlideOverService} from 'src/app/core/services/slide-over.service';
import {sliderOverNavigation} from 'src/app/core/utils/helper';
import {getOrganizationRegistrationStatusClasses} from 'src/app/core/utils/ui-utils';
import {
  FilterBarConfig,
  FilterOption,
  FilterQuery,
} from 'src/app/shared/common/filter-bar/filter-bar.model';
import {HeaderBarConfig} from 'src/app/shared/common/header-bar/header-bar.model';
import {
  NavigationType,
  SlideOverAction,
  SlideOverConfig,
} from 'src/app/shared/common/slide-over/slide-over.model';
import {GlobalStateUtils} from '../../../core/global-state/global-state-utils';
import {AuthorityOrganizationDetailPageComponent} from '../../authority-organization-detail-page/authority-organization-detail-page/authority-organization-detail-page.component';
import {AuthorityInviteNewOrganizationComponent} from '../authority-invite-new-organization/authority-invite-new-organization.component';
import {
  CloseOrganizationDetail,
  RefreshOrganizations,
  ShowOrganizationDetail,
} from './state/authority-organization-list-page-actions';
import {
  AuthorityOrganizationListPageState,
  DEFAULT_AUTHORITY_ORGANIZATION_LIST_PAGE_STATE,
} from './state/authority-organization-list-page-state';
import {AuthorityOrganizationListPageStateImpl} from './state/authority-organization-list-page-state-impl';

@Component({
  selector: 'app-authority-organization-list-page',
  templateUrl: './authority-organization-list-page.component.html',
})
export class AuthorityOrganizationListPageComponent
  implements OnInit, OnDestroy
{
  state = DEFAULT_AUTHORITY_ORGANIZATION_LIST_PAGE_STATE;

  organizationId!: string | undefined;
  showDetail: boolean = false;
  slideOverConfig!: SlideOverConfig;
  componentToRender = AuthorityOrganizationDetailPageComponent;
  filter: OrganizationRegistrationStatusDto | null = null;
  headerConfig!: HeaderBarConfig;
  filterBarConfig!: FilterBarConfig;

  // html doesn't see this function if it's just imported
  getOrganizationRegistrationStatusClasses =
    getOrganizationRegistrationStatusClasses;

  constructor(
    private store: Store,
    public dialog: MatDialog,
    private slideOverService: SlideOverService,
    private globalStateUtils: GlobalStateUtils,
    private titleService: Title,
    private activatedRoute: ActivatedRoute,
    private location: Location,
  ) {
    this.titleService.setTitle('Participant Management');
  }

  ngOnInit() {
    this.activatedRoute.params.pipe(take(1)).subscribe((params) => {
      this.organizationId = params.organizationId;
      if (this.organizationId) {
        this.location.go('/authority/organizations');
      }

      this.initializeHeaderBar();
      this.initializeFilterBar();
      this.refresh();
      this.startListeningToState();
      this.startRefreshingOnEnvChange();
    });
  }

  initializeHeaderBar() {
    this.headerConfig = {
      title: 'Participant Management',
      subtitle: 'Manage all organizations and their users here',
      headerActions: [
        {
          label: 'Invite Organization',
          action: () => this.inviteOrganization(),
          permissions: [UserRoleDto.AuthorityAdmin, UserRoleDto.AuthorityUser],
        },
      ],
    };
  }

  initializeFilterBar() {
    this.filterBarConfig = {
      filters: [
        {
          id: 'status',
          label: 'Status',
          type: 'SELECT',
          icon: 'status',
          options: Object.values(OrganizationRegistrationStatusDto).map(
            (status) => {
              return {id: status, label: status};
            },
          ) as FilterOption[],
        },
      ],
    };
  }

  refresh() {
    this.store.dispatch(RefreshOrganizations);
  }

  private startListeningToState() {
    this.store
      .select<AuthorityOrganizationListPageState>(
        AuthorityOrganizationListPageStateImpl,
      )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
        this.showDetail = state.showDetail;

        if (this.state.organizations.state === 'ready' && this.organizationId) {
          const entry: OrganizationOverviewEntryDto | undefined =
            state.organizations.data.find(
              (org) => org.id === this.organizationId,
            );

          if (entry) {
            this.openDetailPage(entry);
          }

          this.organizationId = undefined;
        }
      });
  }

  private startRefreshingOnEnvChange() {
    this.globalStateUtils.onDeploymentEnvironmentChangeSkipFirst({
      ngOnDestroy$: this.ngOnDestroy$,
      onChanged: () => {
        this.refresh();
      },
    });
  }

  openDetailPage(organization: OrganizationOverviewEntryDto) {
    this.slideOverConfig = {
      childComponentInput: {
        id: organization.id,
      },
      label: organization.name,
      icon: 'organization',
      showNavigation: this.state.organizations.data.length > 1,
      navigationType: NavigationType.STEPPER,
    };
    this.slideOverService.setSlideOverConfig(this.slideOverConfig);
    this.store.dispatch(ShowOrganizationDetail);
  }

  handleFilter(event: FilterQuery) {
    this.filter = event['status'] as OrganizationRegistrationStatusDto;
  }

  handleNavigation(direction: SlideOverAction, currentConnectorId: string) {
    let totalOrganizations = this.state.organizations.data.length;
    let currentIndex = this.state.organizations.data.findIndex(
      (organization) => organization.id === currentConnectorId,
    );
    let nextIndex = sliderOverNavigation(
      direction,
      currentIndex,
      totalOrganizations,
    );
    this.slideOverConfig = {
      ...this.slideOverConfig,
      childComponentInput: {
        id: this.state.organizations.data[nextIndex].id,
      },
      label: this.state.organizations.data[nextIndex].name,
    };
    this.slideOverService.setSlideOverConfig(this.slideOverConfig);
  }

  closeDetailPage() {
    this.store.dispatch(CloseOrganizationDetail);
    this.slideOverService.slideOverReset();
  }

  inviteOrganization() {
    this.dialog.open(AuthorityInviteNewOrganizationComponent, {
      width: window.innerWidth > 640 ? '60%' : '100%',
    });
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.closeDetailPage();
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
