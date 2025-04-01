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
import {
  Component,
  EventEmitter,
  Injector,
  Input,
  OnChanges,
  OnDestroy,
  Output,
} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {SlideOverService} from 'src/app/core/services/slide-over.service';
import {
  DEFAULT_SLIDE_OVER_STATE,
  NavigationType,
  SlideOverAction,
} from './slide-over.model';

@Component({
  selector: 'app-slide-over',
  templateUrl: './slide-over.component.html',
})
export class SlideOverComponent implements OnChanges, OnDestroy {
  @Input() component: any;

  @Output() close = new EventEmitter();
  @Output() navigate = new EventEmitter();

  state = DEFAULT_SLIDE_OVER_STATE;
  childComponentInputId!: string;
  dynamicComponentInjector!: Injector;

  private ngOnDestroy$ = new Subject();

  constructor(
    private injector: Injector,
    private slideOverService: SlideOverService,
  ) {}

  ngOnChanges() {
    this.startListeningToState();
  }

  private startListeningToState() {
    this.slideOverService.slideOverState$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
        if (
          this.state.config.childComponentInput.id !==
          this.childComponentInputId
        ) {
          this.childComponentInputId = this.state.config.childComponentInput.id;
          this.dynamicComponentInjector = Injector.create({
            parent: this.injector,
            providers: [
              {
                provide: 'childComponentInput',
                useValue: this.state.config.childComponentInput,
              },
            ],
          });
        }
      });
  }

  /**
   * Handle the slide over close
   */
  onClose() {
    this.close.emit();
    this.slideOverService.slideOverReset();
  }

  /**
   * Handles the navigation of the slide over
   * @param direction
   */
  onNavigate(direction: string) {
    this.navigate.emit(direction as SlideOverAction);
  }

  onGoBack() {
    this.slideOverService.setSlideOverViewToPreviousView();
    this.slideOverService.setSlideOverNavigationType(NavigationType.STEPPER);
  }

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
