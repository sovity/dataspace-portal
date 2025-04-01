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
import {Provider} from '@angular/core';

/**
 * Type for creating type-safe Jasmine Spies
 */
export type Spied<T> = {
  [Method in keyof T]: jasmine.Spy;
};

export type ConstructorOf<T> = new (...args: any[]) => T;

/**
 * Provide spies for units in tests
 * @param type service class
 * @param spy spy
 */
export function provideSpy<T>(type: ConstructorOf<T>, spy: Spied<T>): Provider {
  return {provide: type, useValue: spy};
}
