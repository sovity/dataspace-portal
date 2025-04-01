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
import {Component, HostBinding, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Observable, forkJoin} from 'rxjs';
import {finalize, ignoreElements, tap} from 'rxjs/operators';
import {IdResponse, UserRoleDto} from '@sovity.de/authority-portal-client';
import {ApiService} from 'src/app/core/api/api.service';
import {ErrorService} from 'src/app/core/services/error.service';
import {setEnabled} from 'src/app/core/utils/form-utils';
import {mapRolesToReadableFormat} from 'src/app/core/utils/user-role-utils';
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';
import {UserDetailConfig} from './shared-user-detail.model';

@Component({
  selector: 'app-shared-user-detail',
  templateUrl: './shared-user-detail.component.html',
})
export class SharedUserDetailComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.my-6')
  @HostBinding('class.@container') // tailwind container queries
  cls = true;

  @Input()
  config!: UserDetailConfig;

  applicationRoleCtrl = new FormControl<UserRoleDto[]>([]);
  participantRoleCtrl = new FormControl<UserRoleDto>('USER');

  roleFormToggled = false;
  roleEditBusy = false;

  constructor(
    private apiService: ApiService,
    private errorService: ErrorService,
    private toast: ToastService,
  ) {}

  onRoleEditShowClick() {
    this.roleFormToggled = true;
    this.applicationRoleCtrl.reset(this.config.roles.currentApplicationRoles);

    this.participantRoleCtrl.enable();
    this.participantRoleCtrl.reset(this.config.roles.currentParticipantRole);
    setEnabled(
      this.participantRoleCtrl,
      this.config.roles.canChangeParticipantRole,
    );
  }

  onRoleEditCancel() {
    this.roleFormToggled = false;
  }

  onRoleEditSubmitClick() {
    const newApplicationRole = this.applicationRoleCtrl.value ?? [];
    const hasNewApplicationRoles =
      JSON.stringify(newApplicationRole) !==
      JSON.stringify(this.config.roles.currentApplicationRoles);

    const newParticipantRole = this.participantRoleCtrl.value;
    const hasNewParticipantRole =
      newParticipantRole !== this.config.roles.currentParticipantRole &&
      newParticipantRole != null;

    if (this.roleEditBusy) {
      return;
    }

    const requests$: Observable<never>[] = [];
    if (hasNewApplicationRoles) {
      requests$.push(this.updateApplicationRoles(newApplicationRole));
    }
    if (hasNewParticipantRole) {
      requests$.push(this.updateParticipantRole(newParticipantRole));
    }

    this.roleEditBusy = true;
    forkJoin(requests$)
      .pipe(finalize(() => (this.roleEditBusy = false)))
      .subscribe({
        complete: () => {
          this.config.roles.onRoleUpdateSuccessful();
          this.roleFormToggled = false;
        },
      });
  }

  onboardingType(type: string) {
    switch (type) {
      case 'INVITATION':
        return 'Invitation';
      case 'SELF_REGISTRATION':
        return 'Self Registration';
      default:
        return '';
    }
  }

  private updateApplicationRoles(newApplicationRoles: UserRoleDto[]) {
    let request$: Observable<IdResponse>;
    if (!newApplicationRoles || newApplicationRoles.length === 0) {
      request$ = this.apiService.clearApplicationRoles(this.config.userId);
    } else {
      request$ = this.apiService.updateApplicationRoles(
        this.config.userId,
        newApplicationRoles,
      );
    }
    return request$.pipe(
      tap(() =>
        this.toast.showSuccess(`Successfully updated application role`),
      ),
      this.errorService.toastFailureRxjs('Failed to update application role'),
      ignoreElements(),
    );
  }

  private updateParticipantRole(newParticipantRole: UserRoleDto) {
    return this.apiService
      .updateParticipantRole(this.config.userId, newParticipantRole)
      .pipe(
        tap(() =>
          this.toast.showSuccess(`Successfully updated participant role`),
        ),
        this.errorService.toastFailureRxjs('Failed to update participant role'),
        ignoreElements(),
      );
  }

  UserRoleDto = UserRoleDto;
  mapRolesToReadableFormat = mapRolesToReadableFormat;
}
