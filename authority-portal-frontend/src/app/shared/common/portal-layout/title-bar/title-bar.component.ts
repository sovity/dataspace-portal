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
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  ViewChild,
} from '@angular/core';
import {Subject} from 'rxjs';
import {GlobalStateUtils} from 'src/app/core/global-state/global-state-utils';
import {SlideOverService} from 'src/app/core/services/slide-over.service';
import {isEllipsisActive} from 'src/app/core/utils/text-ellipsis-utils';
import {MenuOption, TitleBarConfig} from './title-bar.model';

@Component({
  selector: 'app-title-bar',
  templateUrl: './title-bar.component.html',
})
export class TitleBarComponent implements OnDestroy {
  isEllipsisActive: boolean = true;
  @Input() titleBarConfig!: TitleBarConfig;
  @Input() selectedTab!: string;
  @Output() onTabChange = new EventEmitter<string>();

  @ViewChild('title', {static: true})
  title?: ElementRef;

  private ngOnDestroy$ = new Subject();

  constructor(
    private slideOverService: SlideOverService,
    private globalStateUtils: GlobalStateUtils,
  ) {}

  ngAfterViewInit() {
    this.isEllipsisActive = isEllipsisActive(this.title?.nativeElement);
  }

  tabChanged(view: string) {
    this.onTabChange.emit(view);
  }

  menuActionPressed(menuItem: MenuOption) {
    menuItem.event(this.titleBarConfig.actionMenu!.id);
  }

  isAllDisabled(titleBarConfig: TitleBarConfig): boolean {
    return (
      !titleBarConfig.actionMenu ||
      (titleBarConfig.actionMenu &&
        titleBarConfig.actionMenu.menuOptions.every(
          (option) => option.isDisabled,
        ))
    );
  }

  getTitleMaxWidthPx(): string {
    // Slideover title width 762.5px
    const slideOverContentMaxWidth = 762;

    const actionMenuButtonWidth =
      this.titleBarConfig.actionMenu?.menuOptions?.some((it) => !it.isDisabled)
        ? 68
        : 0;

    // Titlebar tab width = 64px with 4px gap
    const rightFlexWidth =
      this.titleBarConfig.tabs.length * 64 +
      (this.titleBarConfig.tabs.length - 1) * 4 +
      actionMenuButtonWidth;

    // Title 2x 8px padding
    const titlePaddingWidth = 16;

    // Title flex 2x 4px gap
    const titleFlexGapWidth = 8;

    // 36px icon
    // Variable status width of ~10.6px per char
    const leftFlexExceptTitleWidth =
      titlePaddingWidth +
      titleFlexGapWidth +
      36 +
      10.6 * this.titleBarConfig.status.length * 1.2;

    const titleMaxWidth =
      slideOverContentMaxWidth - rightFlexWidth - leftFlexExceptTitleWidth - 54;

    return `${titleMaxWidth}px`;
  }

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
