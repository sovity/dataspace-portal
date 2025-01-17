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
import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Observable, Subject, combineLatest, merge, withLatestFrom} from 'rxjs';
import {filter, skip, take, takeUntil} from 'rxjs/operators';
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

  showDetail: boolean = false;
  slideOverConfig!: SlideOverConfig;
  componentToRender = AuthorityOrganizationDetailPageComponent;
  organizationFilter: OrganizationRegistrationStatusDto | null = null;
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
    private router: Router,
  ) {
    this.titleService.setTitle('Participant Management');
  }

  ngOnInit() {
    this.initializeHeaderBar();
    this.initializeFilterBar();
    this.refresh();
    this.startListeningToState();
    this.startRefreshingOnEnvChange();

    this.handleRouteParam();
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

  private handleRouteParam() {
    const paramsChange: Observable<Params> = this.activatedRoute.params;
    const stateReady: Observable<AuthorityOrganizationListPageState> =
      this.store
        .select<AuthorityOrganizationListPageState>(
          AuthorityOrganizationListPageStateImpl,
        )
        .pipe(filter((state) => state.organizations.state === 'ready'));

    // see https://stackoverflow.com/a/39104441
    merge(
      combineLatest([paramsChange, stateReady]).pipe(take(1)),
      paramsChange.pipe(skip(1), withLatestFrom(stateReady)),
    )
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe(([params, state]) => {
        if (params.organizationId) {
          const entry: OrganizationOverviewEntryDto | undefined =
            state.organizations.data.find(
              (org) => org.id === params.organizationId,
            );

          if (entry) {
            if (state.showDetail) {
              this.updateSlideOver(
                state.organizations.data.findIndex(
                  (org) => org.id === entry.id,
                ),
              );
            } else {
              this.openSlideOver(entry);
            }
          } else {
            this.router.navigate(['/authority/organizations']);
          }
        }
      });
  }

  openDetailPage(organization: OrganizationOverviewEntryDto) {
    this.router.navigate(['/authority/organizations', organization.id]);
  }

  handleFilter(event: FilterQuery) {
    this.organizationFilter = event[
      'status'
    ] as OrganizationRegistrationStatusDto;
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
    this.updateSlideOver(nextIndex);
    this.router.navigate([
      '/authority/organizations',
      this.state.organizations.data[nextIndex].id,
    ]);
  }

  closeDetailPage() {
    this.router.navigate(['/authority/organizations']);
  }

  inviteOrganization() {
    this.dialog.open(AuthorityInviteNewOrganizationComponent, {
      width: window.innerWidth > 640 ? '60%' : '100%',
    });
  }

  ngOnDestroy$ = new Subject();
  ngOnDestroy(): void {
    this.closeSlideOver();
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  private openSlideOver(organization: OrganizationOverviewEntryDto) {
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

  private updateSlideOver(index: number) {
    this.slideOverConfig = {
      ...this.slideOverConfig,
      childComponentInput: {
        id: this.state.organizations.data[index].id,
      },
      label: this.state.organizations.data[index].name,
    };
    this.slideOverService.setSlideOverConfig(this.slideOverConfig);
  }

  private closeSlideOver() {
    this.store.dispatch(CloseOrganizationDetail);
    this.slideOverService.slideOverReset();
  }
}
