FROM docker.io/nginxinc/nginx-unprivileged:1.29-alpine3.23

# Temporarily switch to root to install packages and create symlink in restricted location
USER root
RUN apk update && apk add --no-cache jq curl

COPY dist/app /usr/share/nginx/html
COPY docker/default.conf.template etc/nginx/templates/default.conf.template
# Before starting nginx, apply ENV vars to create app-config.json from AUTHORITY_PORTAL_FRONTEND_* ENV Vars
# Use an entrypoint drop-in instead of modifying the default entrypoint or command,
# so that the automatic envsubst templating is not disabled.
COPY docker/99-generate-app-config.sh /docker-entrypoint.d/99-generate-app-config.sh

RUN ln -sf /tmp/app-config.json /usr/share/nginx/html/assets/config/app-configuration.json \
  # Nginx is configured to reject symlinks that point to a file owned by a different user, for security reasons
  && chown --no-dereference nginx:root /usr/share/nginx/html/assets/config/app-configuration.json

# Switch back to unprivileged user for runtime
USER nginx:nginx

ENV NGINX_BIND="0.0.0.0"
ENV NGINX_PORT=8080

HEALTHCHECK --interval=2s --timeout=5s --retries=10 \
  CMD curl -f http://${NGINX_BIND:"localhost"}:$NGINX_PORT/ || exit 1
