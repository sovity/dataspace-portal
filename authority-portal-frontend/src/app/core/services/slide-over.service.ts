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
import {BehaviorSubject} from 'rxjs';
import {
  DEFAULT_SLIDE_OVER_STATE,
  NavigationType,
  SlideOverConfig,
  SlideOverState,
  SlideOverViews,
} from '../../shared/common/slide-over/slide-over.model';

@Injectable({
  providedIn: 'root',
})
export class SlideOverService {
  private slideOverSubject = new BehaviorSubject<SlideOverState>(
    DEFAULT_SLIDE_OVER_STATE,
  );

  slideOverState$ = this.slideOverSubject.asObservable();
  /**
   * Set SlideOver Component Configuration
   */
  setSlideOverConfig(slideOverConfig: SlideOverConfig) {
    let slideOverState = this._getSlideOverState();
    this._updateSlideOverState({
      ...slideOverState,
      config: slideOverConfig,
    });
  }

  /**
   * Set SlideOver Component Navigation Type
   */
  setSlideOverNavigationType(navigationType: NavigationType) {
    let slideOverState = this._getSlideOverState();
    this._updateSlideOverState({
      ...slideOverState,
      config: {
        ...slideOverState.config,
        navigationType: navigationType,
      },
    });
  }

  /**
   * Set SlideOver Component Current & Previous View
   * @param currentView
   * @param previousView
   */
  setSlideOverViews(currentView: SlideOverViews, previousView: SlideOverViews) {
    let slideOverState = this._getSlideOverState();
    this._updateSlideOverState({
      ...slideOverState,
      currentView: currentView,
      previousView: previousView,
    });
  }

  /**
   * Set SlideOver Component Current View To Previous View
   */
  setSlideOverViewToPreviousView() {
    let slideOverState = this._getSlideOverState();

    this._updateSlideOverState({
      ...slideOverState,
      currentView: slideOverState.previousView,
      previousView: {viewName: ''},
    });
  }

  /**
   * Reset SlideOver Component
   */
  slideOverReset() {
    this._updateSlideOverState(DEFAULT_SLIDE_OVER_STATE);
  }

  /**
   * updates SlideOver State
   * @param slideOverState
   */
  _updateSlideOverState(slideOverState: SlideOverState): void {
    this.slideOverSubject.next(slideOverState);
  }

  /**
   * get Slide-over state
   * @returns SlideOver
   */
  _getSlideOverState(): SlideOverState {
    return this.slideOverSubject.getValue();
  }
}
