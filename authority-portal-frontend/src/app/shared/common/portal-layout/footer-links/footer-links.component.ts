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
import {Component, HostBinding, Inject} from '@angular/core';
import {APP_CONFIG, AppConfig} from 'src/app/core/services/config/app-config';
import {FooterLink} from './footer-link.model';

@Component({
  selector: 'app-footer-links',
  templateUrl: './footer-links.component.html',
})
export class FooterLinksComponent {
  @HostBinding('class.flex')
  @HostBinding('class.justify-center')
  @HostBinding('class.gap-1.5')
  @HostBinding('class.whitespace-normal')
  cls = true;

  footerLinks: FooterLink[] = [
    {
      name: 'Privacy Policy',
      href: this.appConfig.privacyPolicyUrl,
    },
    {
      name: 'Legal Notice',
      href: this.appConfig.legalNoticeUrl,
    },
  ];

  constructor(@Inject(APP_CONFIG) public appConfig: AppConfig) {}

  blurFocus(event: MouseEvent) {
    (event.target as HTMLElement).blur();
  }
}
