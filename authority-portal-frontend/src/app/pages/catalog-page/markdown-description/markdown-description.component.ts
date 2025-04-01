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
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostBinding,
  Input,
  OnChanges,
  OnInit,
  ViewChild,
} from '@angular/core';
import {HtmlSanitizer} from 'src/app/core/services/html-sanitizer';
import {MarkdownConverter} from 'src/app/core/services/markdown-converter';
import {SimpleChangesTyped} from 'src/app/core/utils/angular-utils';

const COLLAPSED_DESCRIPTION_HEIGHT = 280;

@Component({
  selector: 'markdown-description',
  templateUrl: './markdown-description.component.html',
})
export class MarkdownDescriptionComponent
  implements OnInit, OnChanges, AfterViewInit
{
  @HostBinding('class.block') cls = true;
  @Input() description: string | undefined;
  @ViewChild('content') elementView!: ElementRef;
  isLargeDescription = false;
  collapsedDescriptionHeight!: number;

  get isCollapsed(): boolean {
    return this.isLargeDescription && this.collapsed;
  }

  private collapsed = true;
  private isAfterViewInit = false;

  constructor(
    private cd: ChangeDetectorRef,
    public markdownConverter: MarkdownConverter,
    public htmlSanitizer: HtmlSanitizer,
  ) {}

  ngOnInit(): void {
    this.collapsedDescriptionHeight = COLLAPSED_DESCRIPTION_HEIGHT;
  }

  ngOnChanges(changes: SimpleChangesTyped<MarkdownDescriptionComponent>) {
    if (changes.description && this.isAfterViewInit) {
      // We need to wait for the changes to apply first.
      // setTimeout(..., 0) appends the task to the end of the microtask queue
      setTimeout(() => this.recalculateShowMore(), 0);
    }
  }

  ngAfterViewInit() {
    this.isAfterViewInit = true;
    this.recalculateShowMore();
    this.cd.detectChanges();
  }

  onToggleShowMore() {
    this.collapsed = !this.collapsed;
  }

  private recalculateShowMore() {
    const contentHeight = this.elementView.nativeElement.offsetHeight;
    this.isLargeDescription = contentHeight > this.collapsedDescriptionHeight;
  }
}
