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

export const getUrl = (
  input: Request | string,
  baseUrl: string,
): {url: string; queryParams: URLSearchParams} => {
  let url = new URL(typeof input === 'string' ? input : input.url);

  let urlNoQuery = url.origin + url.pathname;
  urlNoQuery = urlNoQuery.startsWith(baseUrl)
    ? urlNoQuery.substring(baseUrl.length)
    : urlNoQuery;

  let queryParams = url.searchParams;

  return {url: urlNoQuery, queryParams};
};

export const getMethod = (init: RequestInit | undefined): string =>
  init?.method ?? 'GET';

export const getBody = (input: RequestInit | undefined): null | any => {
  let body = input?.body?.toString();
  return body ? JSON.parse(body) : null;
};
