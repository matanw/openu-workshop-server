set -eux

DB_FOLDER=~/.openu-server
rm -rf $DB_FOLDER
mkdir $DB_FOLDER
DOCKER_BUILDKIT=0 COMPOSE_DOCKER_CLI_BUILD=0 DOCKER_DEFAULT_PLATFORM=linux/x86_64/v8 \
 docker run --volume $DB_FOLDER:/target --env OPENU_SERVER_SKIP_RUN_APP=1 openu-server:latest