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
import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {BehaviorSubject, Observable, combineLatest} from 'rxjs';
import {distinctUntilChanged, filter, map} from 'rxjs/operators';
import {ActiveFeatureSet} from 'src/app/core/services/config/active-feature-set';
import {kebabCaseToSentenceCase} from 'src/app/core/utils/string-utils';
import {BreadcrumbItem} from './breadcrumb.model';

@Injectable({providedIn: 'root'})
export class BreadcrumbService {
  nonLinkable: string[] = [
    'authority',
    'users',
    'service-partner',
    'operator',
    'my-organization',
    'control-center',
  ]; // these are routes that has no associated page

  private replacements = new Map<string, string>();
  private replacementsChange$ = new BehaviorSubject<null>(null);

  breadcrumb$ = new BehaviorSubject<BreadcrumbItem[]>([]);

  constructor(
    private router: Router,
    private activeFeatureSet: ActiveFeatureSet,
  ) {
    combineLatest([this.url$(), this.replacementsChange$]).subscribe(
      ([url, _]) => {
        const breadcrumb = this.buildBreadcrumb(url);
        this.breadcrumb$.next(breadcrumb);
      },
    );
  }

  buildBreadcrumb(url: string): BreadcrumbItem[] {
    let withoutQuery = url.includes('?') ? url.split('?')[0] : url;
    let segments = withoutQuery
      .split('/')
      .filter((segment) => segment !== '' && segment !== 'control-center');

    return [
      {label: 'Home', link: '', isLinkable: true},
      ...segments.map((segment) => ({
        link: segment,
        label: kebabCaseToSentenceCase(
          this.replacements.get(segment) ?? segment,
        ),
        isLinkable: !this.nonLinkable.includes(segment),
      })),
    ];
  }

  private url$(): Observable<string> {
    return this.router.events.pipe(
      map((it) => (it as any)['url']),
      filter((it) => !!it),
      distinctUntilChanged(),
    );
  }

  addReplacement(search: string, replace: string) {
    this.replacements.set(search, replace);
    this.replacementsChange$.next(null);
  }
}
