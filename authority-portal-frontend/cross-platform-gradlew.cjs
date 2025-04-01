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

const os = require('os');
const spawn = require('child_process').spawn;

let executable;
if (os.platform() === 'win32') {
  executable = `gradlew.bat`;
} else {
  executable = `./gradlew`;
}

let args = process.argv.slice(2);
console.log(`CWD: ${process.cwd()}`);
console.log(`Running: ${executable} ${args.join(' ')}`);
const run = spawn(executable, args, {stdio: 'inherit'});

run.on('close', (code) => {
  process.exit(code);
});
