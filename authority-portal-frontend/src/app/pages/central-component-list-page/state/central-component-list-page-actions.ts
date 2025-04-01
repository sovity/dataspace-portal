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
import {CentralComponentDto} from '@sovity.de/authority-portal-client';

const tag = 'CentralComponentListPage';

export class RefreshCentralComponents {
  static readonly type = `[${tag}]  Refresh`;
}

export class DeleteCentralComponent {
  static readonly type = `[${tag}]  Show Central Component Delete Modal`;
  constructor(public centralComponent: CentralComponentDto) {}
}
