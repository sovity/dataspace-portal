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
import {ToastService} from 'src/app/shared/common/toast-notifications/toast.service';

@Injectable({
  providedIn: 'root',
})
export class ClipboardUtils {
  constructor(private toastService: ToastService) {}

  copyToClipboard(text: string | undefined) {
    if (!text) {
      this.toastService.showDanger('Nothing to copy');
      return;
    }

    const textarea = document.createElement('textarea');
    textarea.value = text;

    document.body.appendChild(textarea);
    textarea.select();
    navigator.clipboard
      .writeText(text)
      .then(() => {
        this.toastService.showSuccess('Copied to clipboard');
      })
      .catch(() => {
        this.toastService.showDanger('Failed to copy to clipboard');
      });

    document.body.removeChild(textarea);
  }
}
