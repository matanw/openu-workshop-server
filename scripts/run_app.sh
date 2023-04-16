set -eux

DB_FOLDER=~/.openu-server
DOCKER_BUILDKIT=0 COMPOSE_DOCKER_CLI_BUILD=0 DOCKER_DEFAULT_PLATFORM=linux/x86_64/v8 \
 docker run  --volume $DB_FOLDER:/target \
 --env OPENU_SERVER_SKIP_INIT_DB=1 -p 8080:8080 openu-server:latest