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
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';

@Injectable({
  providedIn: 'root',
})
export class SvgIconServiceService {
  private readonly basePath = 'assets/icons/';
  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
  ) {}

  /**
   * this method takes in list of icons and registers them on matIconRegistry
   * @param iconNames List of icons saved in the assets/icons folder
   */
  initializeIcons(iconNames: string[]): void {
    iconNames.forEach((iconName) => {
      const filePath = `${this.basePath}${iconName}_icon.svg`;
      this.addSvgIcon(iconName, filePath);
    });
  }

  /**
   * register icon on matIconRegistry
   * @param iconName
   * @param filePath
   */
  private addSvgIcon(iconName: string, filePath: string): void {
    this.matIconRegistry.addSvgIcon(
      iconName,
      this.domSanitizer.bypassSecurityTrustResourceUrl(filePath),
    );
  }
}
