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

export interface TitleBarConfig {
  title: string;
  icon: string;
  status: string;
  statusStyle: string;
  tabs: SlideOverTab[];
  actionMenu?: ActionMenu;
}

export interface SlideOverTab {
  icon: string;
  view: any;
  isDisabled: boolean;
  value?: string | number;
  tooltip?: string;
}

export interface ActionMenu {
  id: string;
  menuOptions: MenuOption[];
}

export interface MenuOption {
  label: string;
  icon: string;
  event: (menuId: string) => void;
  isDisabled: boolean;
}

export interface TitleBarMenuActionEvent {
  menuId: string;
  event: string;
}
