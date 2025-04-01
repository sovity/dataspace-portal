#!/usr/bin/env bash
#
# Data Space Portal
# Copyright (C) 2025 sovity GmbH
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

# Use bash instead of sh,
# because sh in this image is provided by dash (https://git.kernel.org/pub/scm/utils/dash/dash.git/),
# which seems to eat environment variables containing dashes,
# which are required for some EDC configuration values.

# Do not set -u to permit unset variables in .env
set -eo pipefail

if [[ "x${1:-}" == "xstart" ]]; then
    cmd=(java ${JAVA_ARGS:-})

    if [ "${REMOTE_DEBUG:-n}" = "y" ] || [ "${REMOTE_DEBUG:-false}" = "true" ]; then
        cmd+=(
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=${REMOTE_DEBUG_SUSPEND:-n},address=${REMOTE_DEBUG_BIND:-127.0.0.1:5005}"
        )
    fi

    logging_config='/app/logging.properties'
    if [ "${DEBUG_LOGGING:-n}" = "y" ] || [ "${DEBUG_LOGGING:-false}" = "true" ]; then
        logging_config='/app/logging.dev.properties'
    fi

    cmd+=(
        -Djava.util.logging.config.file=${logging_config}
        -jar /app/app.jar
    )
else
    cmd=("$@")
fi

if [ "${REMOTE_DEBUG:-n}" = "y" ] || [ "${REMOTE_DEBUG:-false}" = "true" ]; then
  echo "Jar CMD (printing, because REMOTE_DEBUG=y|true): ${cmd[@]}"
fi

# Use "exec" for termination signals to reach JVM
exec "${cmd[@]}"
