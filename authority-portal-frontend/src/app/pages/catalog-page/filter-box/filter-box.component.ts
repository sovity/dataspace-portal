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
  HostBinding,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Subject} from 'rxjs';
import {map} from 'rxjs/operators';
import {SimpleChangesTyped} from 'src/app/core/utils/angular-utils';
import {FilterBoxItem} from './filter-box-item';
import {FilterBoxVisibleState} from './filter-box-visible-state';

@Component({
  selector: 'filter-box',
  templateUrl: './filter-box.component.html',
})
export class FilterBoxComponent implements OnInit, OnChanges, OnDestroy {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.space-y-[10px]')
  cls = true;

  @Input()
  state!: FilterBoxVisibleState;

  @Output()
  selectedItemsChange = new EventEmitter<FilterBoxItem[]>();
  formControl = new FormControl<FilterBoxItem[]>([]);

  ngOnInit(): void {
    this.formControl.valueChanges
      .pipe(map((it) => it ?? []))
      .subscribe((selectedItems) => {
        if (!this.state.isEqualSelectedItems(selectedItems)) {
          this.selectedItemsChange.emit(selectedItems);
        }
      });
  }

  ngOnChanges(changes: SimpleChangesTyped<FilterBoxComponent>) {
    if (changes.state) {
      const selectedItems = this.formControl.value ?? [];
      if (!this.state.isEqualSelectedItems(selectedItems)) {
        this.formControl.setValue(this.state.model.selectedItems);
      }
    }
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
