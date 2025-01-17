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
import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Subject, distinctUntilChanged, map, takeUntil, tap} from 'rxjs';
import {take} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {
  MemberInfo,
  OrganizationDetailsDto,
  UserInfo,
} from '@sovity.de/authority-portal-client';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {SlideOverService} from 'src/app/core/services/slide-over.service';
import {
  getOrganizationRegistrationStatusClasses,
  getOrganizationUserRegistrationStatusClasses,
} from 'src/app/core/utils/ui-utils';
import {TitleBarConfig} from 'src/app/shared/common/portal-layout/title-bar/title-bar.model';
import {
  ChildComponentInput,
  NavigationType,
} from 'src/app/shared/common/slide-over/slide-over.model';
import {FormatService} from '../../../core/services/format.service';
import {OrganizationDeleteDialogService} from '../../../shared/business/organization-delete-dialog/organization-delete-dialog.service';
import {UserDeleteDialogService} from '../../../shared/business/user-delete-dialog/user-delete-dialog.service';
import {
  CloseOrganizationDetail,
  RefreshOrganizations,
} from '../../authority-organization-list-page/authority-organization-list-page/state/authority-organization-list-page-actions';
import {
  ApproveOrganization,
  DeactivateUser,
  ReactivateUser,
  RefreshOrganization,
  RejectOrganization,
  SetOrganizationId,
} from '../state/authority-organization-detail-page-actions';
import {
  AuthorityOrganizationDetailPageState,
  DEFAULT_AUTHORITY_ORGANIZATION_DETAIL_PAGE_STATE,
} from '../state/authority-organization-detail-page-state';
import {AuthorityOrganizationDetailPageStateImpl} from '../state/authority-organization-detail-page-state-impl';
import {
  AuthorityOrganizationDetailTab,
  UserDetailPageConfig,
} from './authority-organization-detail-page.model';

@Component({
  selector: 'app-organization-detail-page',
  templateUrl: './authority-organization-detail-page.component.html',
})
export class AuthorityOrganizationDetailPageComponent
  implements OnInit, OnDestroy
{
  state = DEFAULT_AUTHORITY_ORGANIZATION_DETAIL_PAGE_STATE;
  organizationId!: string;
  organization!: OrganizationDetailsDto;
  titleBarConfig!: TitleBarConfig;
  slideOverContent!: AuthorityOrganizationDetailTab;
  userDetailPageConfig!: UserDetailPageConfig;
  currentUserId: string = '';
  isCurrentUserAuthorityAdmin: boolean = false;

  constructor(
    private store: Store,
    @Inject('childComponentInput') childComponentInput: ChildComponentInput,
    private globalStateUtils: GlobalStateUtils,
    private slideOverService: SlideOverService,
    private userDeleteDialogService: UserDeleteDialogService,
    private organizationDialogService: OrganizationDeleteDialogService,
    private formatService: FormatService,
    private router: Router,
  ) {
    this.organizationId = childComponentInput.id;
  }

  // html doesn't see this function if it's just imported
  getOrganizationRegistrationStatusClasses =
    getOrganizationRegistrationStatusClasses;

  ngOnInit() {
    this.setOrganizationOrganizationId(this.organizationId);
    this.refresh();
    this.startListeningToState();
    this.startListeningToSlideOverState();
    this.startRefreshingOnEnvChange();
    this.startListeningToUserInfo();
  }

  startListeningToUserInfo() {
    this.globalStateUtils.userInfo$
      .pipe(
        takeUntil(this.ngOnDestroy$),
        tap((info: UserInfo) => {
          this.currentUserId = info.userId;
          this.isCurrentUserAuthorityAdmin =
            info.roles.includes('AUTHORITY_ADMIN');
        }),
      )
      .subscribe();
  }

  startListeningToState() {
    this.store
      .select<AuthorityOrganizationDetailPageState>(
        AuthorityOrganizationDetailPageStateImpl,
      )
      .pipe(
        takeUntil(this.ngOnDestroy$),
        tap((state) => (this.state = state)),
        map((state) => state.organizationDetail), // Extract organizationDetail from the state
        distinctUntilChanged(),
      ) // Ensure that the organizationDetail has changed)
      .subscribe((response) => {
        if (this.state.organizationDetail.organization.isReady) {
          this.organization = response.organization.data;
          this.slideOverService.setSlideOverViews(
            {viewName: AuthorityOrganizationDetailTab.DETAIL},
            {viewName: ''},
          ); // once the organization is loaded, starts the slide-over state
        }
      });
  }

  /**
   * Based on the slide-over state actions handles change in the slide-over component
   */
  startListeningToSlideOverState() {
    this.slideOverService.slideOverState$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        if (this.organization) {
          this.setupOrganizationTitleBar(this.organization);
          this.slideOverContent = state.currentView
            .viewName as AuthorityOrganizationDetailTab; // default view is the organization detail page
        }
      });
  }

  startRefreshingOnEnvChange() {
    this.globalStateUtils.onDeploymentEnvironmentChangeSkipFirst({
      ngOnDestroy$: this.ngOnDestroy$,
      onChanged: () => {
        this.refresh();
      },
    });
  }

  setOrganizationOrganizationId(organizationId: string) {
    this.store.dispatch(new SetOrganizationId(organizationId));
  }

  /**
   * initialize the title bar component based on organization details
   * @param organization
   */
  setupOrganizationTitleBar(organization: OrganizationDetailsDto) {
    this.titleBarConfig = {
      title: organization.name,
      icon: 'building-office',
      status: organization.registrationStatus,
      statusStyle: this.getOrganizationRegistrationStatusClasses(
        organization.registrationStatus,
      ),
      tabs: [
        {
          view: AuthorityOrganizationDetailTab.DETAIL,
          icon: 'info',
          isDisabled: false,
        },
        {
          view: AuthorityOrganizationDetailTab.MEMBERS,
          icon: 'group',
          isDisabled: organization.memberCount === 0,
          value: this.formatService.formatInteger(organization.memberCount),
        },
        {
          view: AuthorityOrganizationDetailTab.CONNECTORS,
          icon: 'power',
          isDisabled: true, // no page associated yet
          value: this.formatService.formatInteger(organization.connectorCount),
        },
        {
          view: AuthorityOrganizationDetailTab.DATA_OFFERS,
          icon: 'article',
          isDisabled: true, // no page associated yet
          value: this.formatService.formatInteger(organization.dataOfferCount),
          tooltip: [
            `Available: ${this.formatService.formatInteger(
              organization.liveDataOfferCount,
            )}`,
            `On Request: ${this.formatService.formatInteger(
              organization.onRequestDataOfferCount,
            )}`,
          ].join('\n'),
        },
      ],
      actionMenu: {
        id: 'actionMenu',
        menuOptions: [
          {
            label: 'Approve Participant',
            icon: 'check',
            event: () => this.store.dispatch(ApproveOrganization),
            isDisabled: !['PENDING'].includes(organization.registrationStatus),
          },
          {
            label: 'Reject Participant',
            icon: 'close',
            event: () => {
              this.store.dispatch(RejectOrganization);
              this.store.dispatch(CloseOrganizationDetail);
            },
            isDisabled: !['PENDING'].includes(organization.registrationStatus),
          },
          {
            label: 'Edit Organization',
            icon: 'edit',
            event: () => {
              this.store.dispatch(CloseOrganizationDetail);
              this.router.navigate([
                `/authority/organizations/${organization.id}/edit`,
              ]);
            },
            isDisabled: !this.isCurrentUserAuthorityAdmin,
          },
          {
            label: 'Delete Organization',
            icon: 'delete',
            event: () =>
              this.organizationDialogService.showDeleteOrganizationModal(
                {
                  organizationId: organization.id,
                  organizationName: organization.name,
                  onDeleteSuccess: () => {
                    this.store.dispatch(CloseOrganizationDetail);
                    this.slideOverService.slideOverReset();
                    this.store.dispatch(RefreshOrganizations);
                  },
                },
                this.ngOnDestroy$,
              ),
            isDisabled: !this.isCurrentUserAuthorityAdmin,
          },
        ],
      },
    };
  }

  /**
   * initialize the title bar component based on user details
   * @param user
   */
  setupUserTitleBar(user: MemberInfo) {
    this.titleBarConfig = {
      title: user.firstName + ' ' + user.lastName,
      icon: 'account-circle',
      status: user.registrationStatus,
      statusStyle: getOrganizationUserRegistrationStatusClasses(
        user.registrationStatus,
      ),
      tabs: [],
      actionMenu: {
        id: 'actionMenu',
        menuOptions: [
          {
            label: 'Re-Activate User',
            icon: 'verified',
            event: () =>
              this.store.dispatch(
                new ReactivateUser(this.state.openedUserDetail.userId),
              ),

            isDisabled:
              user.registrationStatus !== 'DEACTIVATED' ||
              user.userId === this.currentUserId,
          },
          {
            label: 'Deactivate User',
            icon: 'person_cancel',
            event: () =>
              this.store.dispatch(
                new DeactivateUser(this.state.openedUserDetail.userId),
              ),

            isDisabled:
              user.registrationStatus !== 'ACTIVE' ||
              user.userId === this.currentUserId,
          },
          {
            label: 'Delete User',
            icon: 'delete',
            event: () =>
              this.userDeleteDialogService.showDeleteUserModal(
                {
                  userId: user.userId,
                  userFullName: user.firstName + ' ' + user.lastName,
                  userOrganizationName: this.organization.name,
                  onDeleteSuccess: (deleteCheck) => {
                    if (deleteCheck.isLastParticipantAdmin) {
                      this.store.dispatch(CloseOrganizationDetail);
                      this.slideOverService.slideOverReset();
                      this.store.dispatch(RefreshOrganizations);
                    } else {
                      this.store
                        .dispatch(new RefreshOrganization())
                        .pipe(take(1))
                        .subscribe(() => {
                          this.slideOverService.setSlideOverViews(
                            {viewName: 'MEMBERS'},
                            {viewName: 'DETAIL'},
                          );
                          this.slideOverService.setSlideOverNavigationType(
                            NavigationType.STEPPER,
                          );
                        });
                    }
                  },
                },
                this.ngOnDestroy$,
              ),
            isDisabled: false,
          },
        ],
      },
    };
  }

  refresh() {
    return this.store.dispatch(RefreshOrganization);
  }

  /**
   *  Handles the tab navigation
   * @param viewType
   */
  tabChangeHandler(viewType: any) {
    let view = viewType as AuthorityOrganizationDetailTab;
    this.slideOverService.setSlideOverViews({viewName: view}, {viewName: ''});
  }

  /**
   * Handles the user detail info navigation form the member list
   * @param user
   */
  userSelectedHandler(user: MemberInfo) {
    this.slideOverService.setSlideOverViews(
      {
        viewName: AuthorityOrganizationDetailTab.USER_DETAILS,
        viewData: user.userId,
      },
      {viewName: AuthorityOrganizationDetailTab.MEMBERS},
    );
    this.slideOverService.setSlideOverNavigationType(NavigationType.GO_BACK);
    this.userDetailPageConfig = {
      userId: user.userId,
      organizationId: this.organizationId,
    };
    this.setupUserTitleBar(user);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
