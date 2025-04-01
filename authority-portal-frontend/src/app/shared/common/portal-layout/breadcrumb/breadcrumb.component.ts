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
import {Component, OnDestroy} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {Subject, takeUntil} from 'rxjs';
import {ActiveFeatureSet} from 'src/app/core/services/config/active-feature-set';
import {BreadcrumbItem} from './breadcrumb.model';
import {BreadcrumbService} from './breadcrumb.service';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
})
export class BreadcrumbComponent implements OnDestroy {
  breadcrumb: BreadcrumbItem[] = [];

  constructor(private breadcrumbService: BreadcrumbService) {
    this.breadcrumbService.breadcrumb$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((breadcrumb) => {
        this.breadcrumb = breadcrumb;
      });
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  getLink(idx: number) {
    return this.breadcrumb
      .slice(1, idx + 1)
      .map((s) => s.link)
      .join('/');
  }
}
