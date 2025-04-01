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

export interface SlideOverState {
  config: SlideOverConfig;
  currentView: SlideOverViews;
  previousView: SlideOverViews;
}

export interface SlideOverViews {
  viewName: string;
  viewData?: string;
}

export interface SlideOverConfig {
  childComponentInput: ChildComponentInput;
  label: string;
  icon: string;
  showNavigation: boolean;
  navigationType: NavigationType;
}

export enum SlideOverAction {
  PREVIOUS = 'PREVIOUS',
  NEXT = 'NEXT',
}

export enum NavigationType {
  STEPPER = 'STEPPER',
  GO_BACK = 'GO_BACK',
}

export interface ChildComponentInput {
  id: string;
}

export const DEFAULT_SLIDE_OVER_STATE: SlideOverState = {
  config: {
    childComponentInput: {
      id: '',
    },
    label: '',
    icon: '',
    showNavigation: false,
    navigationType: NavigationType.STEPPER,
  },
  currentView: {viewName: ''},
  previousView: {viewName: ''},
};
