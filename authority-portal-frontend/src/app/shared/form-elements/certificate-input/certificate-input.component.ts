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
  Component,
  ElementRef,
  HostBinding,
  Input,
  OnDestroy,
} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Subject, interval, takeUntil} from 'rxjs';
import {ClipboardUtils} from '../../../core/utils/clipboard-utils';

@Component({
  selector: 'app-certificate-input',
  templateUrl: './certificate-input.component.html',
})
export class CertificateInputComponent implements AfterViewInit, OnDestroy {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.items-stretch')
  @HostBinding('class.select-none')
  cls = true;

  textAreaClasses = 'border-b-2 border-gray-400 mb-3 py-1 pl-1 !resize-none';

  @Input()
  label = 'Certificate';

  @Input()
  ctrl: FormControl<string> = new FormControl();

  @Input()
  ctrlId = 'certificate-input';

  @Input()
  showLabel = true;

  @Input()
  readonly = false;

  @Input()
  placeholder = [
    '-----BEGIN CERTIFICATE-----',
    'MIICqjCCAhOgAwIBAgIBADANBgk',
    '...',
    'AkmFH0MPvGNeUum02F0=',
    '-----END CERTIFICATE-----',
  ].join('\n');

  private _textarea: HTMLTextAreaElement | null = null;

  constructor(
    private elementRef: ElementRef,
    public clipboardUtils: ClipboardUtils,
  ) {}

  ngAfterViewInit() {
    interval(100)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe(() => {
        let textArea = this.getTextArea();
        let height = textArea.parentElement!.parentElement!.clientHeight - 32;
        textArea.style.height = height + 'px';
      });
  }

  private getTextArea(): HTMLTextAreaElement {
    if (!this._textarea) {
      const componentDomElement = this.elementRef.nativeElement as HTMLElement;
      this._textarea = componentDomElement.querySelector('textarea');
    }
    return this._textarea!;
  }

  ngOnDestroy$ = new Subject();
  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
