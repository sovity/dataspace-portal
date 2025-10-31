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
import {NavigationEnd, Router} from '@angular/router';
import {filter, first} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UrlBeforeLoginService {
  public originalUrl: string = '';

  constructor(private router: Router) {
    this.router.events
      .pipe(
        filter(
          (event): event is NavigationEnd =>
            event instanceof NavigationEnd &&
            event.url != null &&
            event.url != undefined &&
            event.url != '' &&
            event.url != '/' &&
            event.url != '/random-redirect-for-force-refresh',
        ),
        first(),
      )
      .subscribe((event) => {
        this.originalUrl = event.urlAfterRedirects || event.url;
        localStorage.setItem('originalUrl', this.originalUrl);
      });
  }

  public clearOriginalUrl(): void {
    this.originalUrl = '';
    localStorage.removeItem('originalUrl');
  }

  public goToOriginalUrl(): void {
    if (this.originalUrl) {
      this.router.navigateByUrl(this.originalUrl);
      this.clearOriginalUrl();
    }
  }
}
