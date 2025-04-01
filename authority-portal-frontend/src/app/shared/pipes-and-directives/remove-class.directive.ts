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
  AfterViewChecked,
  AfterViewInit,
  Directive,
  ElementRef,
  Input,
} from '@angular/core';

/**
 * Angular Material automatically adds CSS classes when you use their directives.
 *
 * But if you don't use their directives, the scrollbars in dialogs break, for example.
 */
@Directive({
  selector: '[removeClass]',
})
export class RemoveClassDirective implements AfterViewChecked, AfterViewInit {
  @Input()
  removeClass!: string;

  constructor(private elementRef: ElementRef) {}

  ngAfterViewInit() {
    this.removeClassIfNecessary();
  }

  ngAfterViewChecked() {
    this.removeClassIfNecessary();
  }

  private removeClassIfNecessary() {
    const classList = (this.elementRef.nativeElement as HTMLElement).classList;
    if (classList.contains(this.removeClass)) {
      classList.remove(this.removeClass);
    }
  }
}
