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
  Inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import {Subject} from 'rxjs';
import {finalize, takeUntil} from 'rxjs/operators';
import {NgxJsonViewerComponent} from 'ngx-json-viewer';
import {cleanJson} from './clean-json';
import {DialogToolbarButton, JsonDialogData} from './json-dialog.data';

@Component({
  selector: 'app-json-dialog',
  templateUrl: './json-dialog.component.html',
})
export class JsonDialogComponent implements OnInit, AfterViewInit, OnDestroy {
  busy = false;

  removeNulls = true;

  visibleJson: unknown = {};

  @ViewChild(NgxJsonViewerComponent, {read: ElementRef})
  jsonViewer!: ElementRef;

  constructor(
    public dialogRef: MatDialogRef<JsonDialogComponent>,
    private matDialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: JsonDialogData,
  ) {}

  ngOnInit() {
    this.updateVisibleJson();
  }

  ngAfterViewInit() {
    this.jsonViewer.nativeElement.scrollIntoView();
  }

  updateVisibleJson() {
    this.visibleJson = this.removeNulls
      ? cleanJson(this.data.objectForJson)
      : this.data.objectForJson;
  }
  doAction(button: DialogToolbarButton) {
    if (this.busy) {
      return;
    }
    this.busy = true;
    button
      .action()
      .pipe(
        finalize(() => (this.busy = false)),
        takeUntil(this.ngOnDestroy$),
      )
      .subscribe();
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
