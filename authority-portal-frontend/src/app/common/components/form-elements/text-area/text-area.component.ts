/*
 * Copyright (c) 2024 sovity GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      sovity GmbH - initial implementation
 */
import {Component, HostBinding, Input, booleanAttribute} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-text-area',
  templateUrl: './text-area.component.html',
})
export class TextAreaComponent {
  @HostBinding('class.select-none')
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-between')
  @HostBinding('class.items-center')
  cls = true;

  @Input()
  ctrl: FormControl<string> = new FormControl();

  @Input()
  ctrlId = 'missing-id-' + Math.random().toString(36).substring(7);

  @Input()
  label: string = 'Unnamed';

  @Input()
  placeholder: string = '...';

  @Input()
  requiredMessage = 'Field is required.';

  @Input({transform: booleanAttribute})
  required = false;
}
