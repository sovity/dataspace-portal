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
import {OperatorFunction, concat, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';

/**
 * Wraps potentially fetched value with state and potential errors.
 */
export class Fetched<T> {
  constructor(
    public state: FetchedState,
    public dataOrUndefined: T | undefined,
    public errorOrUndefined: FetchError | undefined,
  ) {}

  get isLoading(): boolean {
    return this.state === 'loading' || this.state === 'not-loaded';
  }

  get isReady(): boolean {
    return this.state === 'ready';
  }

  get isError(): boolean {
    return this.state === 'error';
  }

  get data(): T {
    if (this.state !== 'ready') {
      throw new Error(`Fetched not ready!`);
    }
    return this.dataOrUndefined!;
  }

  get error(): FetchError {
    if (this.state !== 'error') {
      throw new Error(`Fetched not in error state!`);
    }
    return this.errorOrUndefined!;
  }

  /**
   * Map data if present but keep state
   * @param mapFn mapping fn
   */
  map<R>(mapFn: (t: T) => R): Fetched<R> {
    return new Fetched<R>(
      this.state,
      this.isReady ? mapFn(this.data) : undefined,
      this.errorOrUndefined,
    );
  }

  /**
   * Map data if present and data is non-null
   * @param mapFn mapping fn
   */
  mapNotNull<R>(mapFn: (t: NonNullable<T>) => R): Fetched<R> {
    return new Fetched<R>(
      this.state,
      this.isReady
        ? this.data == null
          ? (this.data as any as R) // preserve null vs undefined
          : mapFn(this.data)
        : undefined,
      this.errorOrUndefined,
    );
  }

  /**
   * Get data or fall back to default value
   * @param defaultValue value to fall back to if no data is present
   */
  orElse<R>(defaultValue: R): T | R {
    return this.isReady ? this.data : defaultValue;
  }

  /**
   * Run function with data if data is ready
   * @param fn function
   */
  ifReady(fn: (t: T) => void): void {
    if (this.isReady) {
      fn(this.data);
    }
  }

  /**
   * Run function with data if data is ready or return default value
   * @param fn function
   * @param defaultValue defaultValue
   */
  ifReadyElse<R>(fn: (t: T) => R, defaultValue: R): R {
    if (this.isReady) {
      return fn(this.data);
    }
    return defaultValue;
  }

  /**
   * Map entire Fetched to a different type.
   *
   * @param opts functions matching the different possible states of Fetched. All need to return the return type.
   * @return value of type R
   */
  match<R>(opts: {
    ifOk: (value: T) => R;
    ifError: (error: FetchError) => R;
    ifLoading: () => R;
  }): R {
    if (this.isError) {
      return opts.ifError(this.error);
    } else if (this.isReady) {
      return opts.ifOk(this.data);
    } else {
      return opts.ifLoading();
    }
  }

  /**
   * Prevents a READY state from being overridden by a LOADING state
   * @param other
   */
  mergeIfReady(other: Fetched<T>): Fetched<T> {
    if (other.isReady) {
      return other;
    }

    return this.isReady ? this : other;
  }

  static empty<T>(): Fetched<T> {
    return new Fetched<T>('not-loaded', undefined, undefined);
  }

  static loading<T>(): Fetched<T> {
    return new Fetched<T>('loading', undefined, undefined);
  }

  static ready<T>(data: T): Fetched<T> {
    return new Fetched<T>('ready', data, undefined);
  }

  static error<T>(failureMessage: string, error: any): Fetched<T> {
    return Fetched.error2(mapFetchError(failureMessage, error));
  }

  static error2<T>(fetchError: FetchError): Fetched<T> {
    return new Fetched<T>('error', undefined, fetchError);
  }

  /**
   * RXJS Operator: Wraps request into multiple emissions that track state.
   *
   * @param opts adit
   */
  static wrap<T>(opts: {
    failureMessage: string;
  }): OperatorFunction<T, Fetched<T>> {
    return (obs) =>
      concat(
        of(Fetched.loading<T>()),
        obs.pipe(
          map((data) => Fetched.ready(data)),
          catchError((err) => {
            console.error(opts.failureMessage, err);
            return of(Fetched.error<T>(opts.failureMessage, err));
          }),
        ),
      );
  }

  /**
   * RXJS Operator: Wraps request into multiple emissions that track state.
   *
   * @param orElse value to fall back to if request fails
   */
  static wrapAndReplaceErrorsSilently<T>(
    orElse: T,
  ): OperatorFunction<T, Fetched<T>> {
    return (obs) =>
      concat(
        of(Fetched.loading<T>()),
        obs.pipe(
          map((data) => Fetched.ready(data)),
          catchError(() => of(Fetched.ready(orElse))),
        ),
      );
  }

  /**
   * RXJS Operator: Map fetched value
   *
   * @param mapFn mapping fn applied to data if present
   */
  static map<T, R>(
    mapFn: (value: T) => R,
  ): OperatorFunction<Fetched<T>, Fetched<R>> {
    return (obs) => obs.pipe(map((fetched) => fetched.map(mapFn)));
  }

  /**
   * RXJS Operator: Get value or fall back to default value
   *
   * @param defaultValue value to fall back to if no data is present
   */
  static orElse<T, R>(defaultValue: R): OperatorFunction<Fetched<T>, T | R> {
    return (obs) => obs.pipe(map((fetched) => fetched.orElse(defaultValue)));
  }
}

/**
 * States a potentially fetched value can assume.
 */
export type FetchedState = 'not-loaded' | 'loading' | 'ready' | 'error';

/**
 * Errors are paired with a user-friendly action-specific failure messages
 * since stack traces might have useless technical error messages.
 */
export interface FetchError {
  type: 'error' | '401';
  failureMessage: string;
  failureIcon: string;
  error: any;
}

export function mapFetchError(failureMessage: string, error: any): FetchError {
  if (error?.status === 401) {
    return {
      type: '401',
      failureIcon: 'refresh',
      failureMessage: 'Session most likely expired. Please refresh browser.',
      error,
    };
  }

  return {
    type: 'error',
    failureIcon: 'error',
    failureMessage,
    error,
  };
}
