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

/**
 * Collects URLs + Method + ResponseFn and then matches them in order.
 *
 * This class only exists to clean up the fake-backend code.
 */
export class UrlInterceptor {
  private entries: {
    urlPattern: string;
    method: string;
    response: ResponseFn;
  }[] = [];

  private lastUrlPattern: string | null = null;

  constructor(public requestUrl: string, public requestMethod: string) {}

  url(urlPattern: string): this {
    this.lastUrlPattern = urlPattern;
    return this;
  }

  on(method: string, response: ResponseFn): this {
    let urlPattern = this.lastUrlPattern;
    if (!urlPattern) {
      throw new Error('Call .url() before calling .on()');
    }
    this.entries.push({urlPattern, method, response});
    return this;
  }

  async tryMatch(): Promise<Response> {
    for (let entry of this.entries) {
      if (entry.method !== this.requestMethod) {
        continue;
      }

      let regexp = '^' + entry.urlPattern.replace(/\*/g, '([^/]*)') + '$';
      let match: string[] = this.requestUrl.match(regexp) || [];
      if (!match.length) {
        continue;
      }

      match = match
        .filter((_, index) => index > 0)
        .map((pathSegment) => decodeURIComponent(pathSegment));

      return await entry.response(...match);
    }

    console.warn(
      `Unmatched request: ${this.requestMethod} ${this.requestUrl}`,
      this.entries.map((it) => `${it.method} ${it.urlPattern}`),
    );

    return Promise.reject(
      `Unmatched request: ${this.requestMethod} ${this.requestUrl}`,
    );
  }
}

export type ResponseFn = (...match: string[]) => Promise<Response>;
