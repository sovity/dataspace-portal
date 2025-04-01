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

export const buildOkFn =
  (method: string, url: string, queryParams: URLSearchParams, body: any) =>
  (responseBody: any, log = true): Promise<Response> =>
    new Promise((resolve) => {
      if (log) {
        const output = [
          'Fake Backend',
          method,
          url + (queryParams.toString() ? '?' + queryParams.toString() : ''),
        ];

        if (body) {
          output.push('Requested ', body);
        }

        output.push('Responding ', responseBody);
        console.log(...output);
      }
      const response = new Response(JSON.stringify(responseBody), {
        status: 200,
      });
      setTimeout(() => resolve(response), 400);
    });
