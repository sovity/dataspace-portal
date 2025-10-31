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
import {EDC_CONFIG} from 'src/app/core/services/config/connector-config';
import {ClipboardUtils} from 'src/app/core/utils/clipboard-utils';

@Component({
  selector: 'app-connector-registering-success-message-page',
  templateUrl: './connector-registering-success-message-page.component.html',
})
export class ConnectorRegisteringSuccessMessagePageComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-center')
  @HostBinding('class.items-center')
  @HostBinding('class.text-center')
  @HostBinding('class.my-12')
  @HostBinding('class.overflow-hidden')
  cls = true;

  @Input()
  connectorConfig: string = '...';

  config = EDC_CONFIG;

  constructor(private clipboardUtils: ClipboardUtils) {}

  copyToClipboard() {
    this.clipboardUtils.copyToClipboard(this.connectorConfig);
  }

  copyToClipboardDockerImage() {
    this.clipboardUtils.copyToClipboard(this.config.dockerImage);
  }
}
