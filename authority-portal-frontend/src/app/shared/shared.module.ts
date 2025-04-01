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
import {ClipboardModule} from '@angular/cdk/clipboard';
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatLineModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatLegacyChipsModule} from '@angular/material/legacy-chips';
import {MatListModule} from '@angular/material/list';
import {MatMenuModule} from '@angular/material/menu';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {SvgIconServiceService} from 'src/app/core/services/svg-icon.service.service';
import {ConnectorRegisteringSuccessMessagePageComponent} from './business/connector-registering-success-message-page/connector-registering-success-message-page.component';
import {ConnectorStatusLedComponent} from './business/connector-status-led/connector-status-led.component';
import {DeleteConnectorModalComponent} from './business/delete-connector-modal/delete-connector-modal.component';
import {OrganizationCreateFormComponent} from './business/organization-create-form/organization-create-form.component';
import {OrganizationDeleteDialogComponent} from './business/organization-delete-dialog/organization-delete-dialog.component';
import {OrganizationDeleteDialogService} from './business/organization-delete-dialog/organization-delete-dialog.service';
import {OrganizationEditFormComponent} from './business/organization-edit-form/organization-edit-form.component';
import {SharedConnectorDetailComponent} from './business/shared-connector-detail/shared-connector-detail.component';
import {SharedOrganizationDetailComponent} from './business/shared-organization-detail/shared-organization-detail.component';
import {SharedUserDetailComponent} from './business/shared-user-detail/shared-user-detail.component';
import {SharedUserListComponent} from './business/shared-user-list/shared-user-list.component';
import {UserCreateFormComponent} from './business/user-create-form/user-create-form.component';
import {UserDeleteDialogComponent} from './business/user-delete-dialog/user-delete-dialog.component';
import {UserDeleteDialogService} from './business/user-delete-dialog/user-delete-dialog.service';
import {UserEditFormComponent} from './business/user-edit-form/user-edit-form.component';
import {UserOnboardFormComponent} from './business/user-onboard-form/user-onboard-form.component';
import {AgoComponent} from './common/ago/ago.component';
import {ConfirmationDialogComponent} from './common/confirmation-dialog/confirmation-dialog.component';
import {EmptyElementComponent} from './common/empty-element/empty-element.component';
import {ErrorElementComponent} from './common/error-element/error-element.component';
import {FilterBarComponent} from './common/filter-bar/filter-bar.component';
import {HeaderBarComponent} from './common/header-bar/header-bar.component';
import {LoadingElementComponent} from './common/loading-element/loading-element.component';
import {LogoutButtonComponent} from './common/logout-button/logout-button.component';
import {AvatarComponent} from './common/portal-layout/avatar/avatar.component';
import {BreadcrumbComponent} from './common/portal-layout/breadcrumb/breadcrumb.component';
import {ControlCenterComponent} from './common/portal-layout/control-center/control-center.component';
import {FooterCopyrightComponent} from './common/portal-layout/footer-copyright/footer-copyright.component';
import {FooterForFullPageComponent} from './common/portal-layout/footer-for-full-page/footer-for-full-page.component';
import {FooterLinksComponent} from './common/portal-layout/footer-links/footer-links.component';
import {PortalLayoutComponent} from './common/portal-layout/portal-layout/portal-layout.component';
import {SidebarElementComponent} from './common/portal-layout/sidebar-element/sidebar-element.component';
import {SidebarComponent} from './common/portal-layout/sidebar/sidebar.component';
import {TitleBarComponent} from './common/portal-layout/title-bar/title-bar.component';
import {ToolbarComponent} from './common/portal-layout/toolbar/toolbar.component';
import {SelectionBoxComponent} from './common/selection-box/selection-box.component';
import {SlideOverComponent} from './common/slide-over/slide-over.component';
import {ToastNotificationsComponent} from './common/toast-notifications/toast-notifications.component';
import {E2EDevUserSwitcherComponent} from './dev-utils/e2e-dev-user-switcher/e2e-dev-user-switcher.component';
import {EnvBannerComponent} from './dev-utils/env-banner/env-banner.component';
import {EnvSwitcherComponent} from './dev-utils/env-switcher/env-switcher.component';
import {FakeBackendUserSwitcherComponent} from './dev-utils/fake-backend-user-switcher/fake-backend-user-switcher.component';
import {ApplicationRolesTooltipComponent} from './form-elements/application-roles-tooltip/application-roles-tooltip.component';
import {CertificateInputFormComponent} from './form-elements/certificate-input-form/certificate-input-form.component';
import {CertificateInputComponent} from './form-elements/certificate-input/certificate-input.component';
import {ConnectorUrlInputComponent} from './form-elements/connector-url-input/connector-url-input.component';
import {IndustrySelectComponent} from './form-elements/industry-select/industry-select.component';
import {OrganizationRolesTooltipComponent} from './form-elements/organization-roles-tooltip/organization-roles-tooltip.component';
import {OrganizationSelectComponent} from './form-elements/organization-select/organization-select.component';
import {PasswordInputComponent} from './form-elements/password-input/password-input.component';
import {PasswordRepeatInputComponent} from './form-elements/password-repeat-input/password-repeat-input.component';
import {ReadonlyTextInputComponent} from './form-elements/readonly-text-input/readonly-text-input.component';
import {TextAreaComponent} from './form-elements/text-area/text-area.component';
import {TextInputComponent} from './form-elements/text-input/text-input.component';
import {TosCheckComponent} from './form-elements/tos-check/tos-check.component';
import {AgoPipe} from './pipes-and-directives/ago.pipe';
import {AutofocusDirective} from './pipes-and-directives/autofocus.direcitive';
import {CheckIfNotCurrentUserPipe} from './pipes-and-directives/check-if-not-current-user.pipe';
import {CompareByFieldPipe} from './pipes-and-directives/compare-by-field.pipe';
import {DisabledButtonDirective} from './pipes-and-directives/disabled-button.directive';
import {ExternalLinkDirective} from './pipes-and-directives/external-link.directive';
import {FilterByRegistrationStatusPipe} from './pipes-and-directives/filter-by-registration-status.pipe';
import {FormControlErrorDirective} from './pipes-and-directives/form-control-error.directive';
import {FormatIntegerPipe} from './pipes-and-directives/format-integer.pipe';
import {HasAnyRolePipe} from './pipes-and-directives/has-any-role.pipe';
import {HasRolePipe} from './pipes-and-directives/has-role.pipe';
import {IsActiveFeaturePipe} from './pipes-and-directives/is-active-feature.pipe';
import {RemoveClassDirective} from './pipes-and-directives/remove-class.directive';
import {SentenceCasePipe} from './pipes-and-directives/sentence-case.pipe';
import {TrackByFieldDirective} from './pipes-and-directives/track-by-field.directive';
import {ValuesPipe} from './pipes-and-directives/values.pipe';

const COMPONENTS = [
  // ./business
  ConnectorRegisteringSuccessMessagePageComponent,
  ConnectorStatusLedComponent,
  DeleteConnectorModalComponent,
  OrganizationCreateFormComponent,
  OrganizationDeleteDialogComponent,
  OrganizationEditFormComponent,
  SharedConnectorDetailComponent,
  SharedOrganizationDetailComponent,
  SharedUserDetailComponent,
  SharedUserListComponent,
  UserCreateFormComponent,
  UserDeleteDialogComponent,
  UserEditFormComponent,
  UserOnboardFormComponent,

  // ./common
  AgoComponent,
  ConfirmationDialogComponent,
  EmptyElementComponent,
  ErrorElementComponent,
  FilterBarComponent,
  HeaderBarComponent,
  LoadingElementComponent,
  LogoutButtonComponent,
  SelectionBoxComponent,
  SlideOverComponent,
  ToastNotificationsComponent,

  // ./common/portal-layout
  AvatarComponent,
  BreadcrumbComponent,
  ControlCenterComponent,
  FooterCopyrightComponent,
  FooterForFullPageComponent,
  FooterLinksComponent,
  PortalLayoutComponent,
  SidebarComponent,
  SidebarElementComponent,
  TitleBarComponent,
  ToolbarComponent,

  // ./dev-utils
  E2EDevUserSwitcherComponent,
  EnvBannerComponent,
  EnvSwitcherComponent,
  FakeBackendUserSwitcherComponent,

  // ./form-elements
  ApplicationRolesTooltipComponent,
  CertificateInputComponent,
  CertificateInputFormComponent,
  ConnectorUrlInputComponent,
  IndustrySelectComponent,
  OrganizationRolesTooltipComponent,
  OrganizationSelectComponent,
  PasswordInputComponent,
  PasswordRepeatInputComponent,
  ReadonlyTextInputComponent,
  TextAreaComponent,
  TextInputComponent,
  TosCheckComponent,

  // ./pipes-and-directives
  AgoPipe,
  AutofocusDirective,
  CheckIfNotCurrentUserPipe,
  CompareByFieldPipe,
  DisabledButtonDirective,
  ExternalLinkDirective,
  FilterByRegistrationStatusPipe,
  FormatIntegerPipe,
  FormControlErrorDirective,
  HasAnyRolePipe,
  HasRolePipe,
  IsActiveFeaturePipe,
  RemoveClassDirective,
  SentenceCasePipe,
  TrackByFieldDirective,
  ValuesPipe,
];

const MODULES = [
  // Angular Material
  MatAutocompleteModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDialogModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatLineModule,
  MatMenuModule,
  MatRadioModule,
  MatSelectModule,
  MatSlideToggleModule,
  MatStepperModule,
  MatTabsModule,
  MatToolbarModule,
  MatDividerModule,
  MatDatepickerModule,
  MatTooltipModule,

  // For Catalog?
  MatBadgeModule,
  MatCardModule,
  MatExpansionModule,
  MatListModule,
  MatPaginatorModule,
  MatTableModule,
  MatLegacyChipsModule,

  // Angular CDK
  ClipboardModule,
];

@NgModule({
  declarations: COMPONENTS,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    ...MODULES,
  ],
  exports: [...COMPONENTS, ...MODULES],
  providers: [UserDeleteDialogService, OrganizationDeleteDialogService],
})
export class SharedModule {
  constructor(private svgIconServiceService: SvgIconServiceService) {
    const iconsList = [
      'account-circle',
      'arrow-down-tray',
      'arrow-left-on-rectangle',
      'bell',
      'briefcase',
      'building-office-2',
      'building-office',
      'checkbox',
      'chevron-down',
      'chevron-left',
      'chevron-up',
      'connector-2',
      'connector',
      'dashboard',
      'document-text',
      'ellipsis-horizontal',
      'extension',
      'github',
      'home',
      'navigation-next',
      'pencil',
      'question-mark-circle',
      'search',
      'status',
      'support',
      'tag',
      'user',
      'users',
      'profile',
    ];
    this.svgIconServiceService.initializeIcons(iconsList); // to registers all the SVG icons in matIconRegistry
  }
}
