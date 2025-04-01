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

const {writeFileSync, existsSync, readFileSync} = require('fs');
const dotenv = require('dotenv');

const envName = process.env.ENV || 'local-dev';

// Generate app-config.json from ENV Vars
// Priority: ENV VAR > .env > .env.local-dev
// Usage: node ./config-generator.js

// app-config.json in production is not generated by this script

/**
 * Reads given .env file
 *
 * @param path path to .env file
 * @return vars (Record<string, string>)
 */
const readEnvFileSync = (path) => {
  if (existsSync(path)) {
    return dotenv.parse(readFileSync(path));
  }
  return {};
};

/**
 * Filter object properties by applying filter fn to each key.
 *
 * @param obj any object
 * @param fn filter fn (applied to property name)
 * @return subset of obj
 */
const objFilterKeys = (obj, fn) =>
  Object.fromEntries(Object.entries(obj).filter(([k, _]) => fn(k)));

// Read ENV Vars from .env files as well
const allProps = {
  ...readEnvFileSync('.env.' + envName),
  ...readEnvFileSync('.env'),
  ...process.env,
};

// Collect ENV Vars with prefix AUTHORITY_PORTAL_FRONTEND_
const prefix = 'AUTHORITY_PORTAL_FRONTEND_';
const filteredProps = objFilterKeys(allProps, (k) => k.startsWith(prefix));
if (!Object.keys(filteredProps).length) {
  console.warn(
    `No ${prefix} configuration properties are set in ENV, application might not be configured properly.`,
  );
}

// Write app-config.json
const output = './src/assets/config/app-configuration.json';
const json = JSON.stringify(filteredProps);
writeFileSync(output, json);

// It is ok to log this config as the data will be available in all client browsers
console.log(`Writing app.config.json to ${output}: ${json}`);
