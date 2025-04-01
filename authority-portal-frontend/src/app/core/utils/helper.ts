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
import {SlideOverAction} from 'src/app/shared/common/slide-over/slide-over.model';

/**
 *  get the next or previous index of an array
 * @param direction
 * @param currentIndex
 * @param totalLength
 * @returns
 */
export function sliderOverNavigation(
  direction: SlideOverAction,
  currentIndex: number,
  totalLength: number,
): number {
  if (direction === SlideOverAction.NEXT) {
    return currentIndex < totalLength - 1 ? currentIndex + 1 : 0;
  } else {
    // PREVIOUS
    return currentIndex > 0 ? currentIndex - 1 : totalLength - 1;
  }
}
/**
 * generate and download a file
 * @param fileTitle
 * @param content
 * @param contentType
 */
export function downloadFile(
  fileTitle: string,
  content: string,
  contentType: string,
) {
  const a = document.createElement('a');
  const blob = new Blob([content], {type: contentType});
  a.href = URL.createObjectURL(blob);
  a.download = fileTitle;
  a.click();
  window.URL.revokeObjectURL(window.URL.createObjectURL(blob));
}
